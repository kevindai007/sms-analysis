package com.kevindai.base.smsanalysis.service;

import com.kevindai.base.smsanalysis.entity.AnalysisTask;
import com.kevindai.base.smsanalysis.entity.TaskDetail;
import com.kevindai.base.smsanalysis.entity.TaskSummary;
import com.kevindai.base.smsanalysis.repository.AnalysisTaskRepository;
import com.kevindai.base.smsanalysis.repository.TaskDetailRepository;
import com.kevindai.base.smsanalysis.repository.TaskSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsAnalysisService {
    
    private final AnalysisTaskRepository analysisTaskRepository;
    private final TaskDetailRepository taskDetailRepository;
    private final TaskSummaryRepository taskSummaryRepository;
    
    /**
     * Create a new analysis task
     */
    @Transactional
    public AnalysisTask createAnalysisTask(String taskId) {
        if (analysisTaskRepository.existsByTaskId(taskId)) {
            throw new IllegalArgumentException("Task with ID " + taskId + " already exists");
        }
        
        AnalysisTask task = new AnalysisTask(taskId);
        task.setStartTime(LocalDateTime.now());
        task.setStatus("IN_PROGRESS");
        
        return analysisTaskRepository.save(task);
    }
    
    /**
     * Get analysis task by task ID
     */
    public Optional<AnalysisTask> getAnalysisTask(String taskId) {
        return analysisTaskRepository.findByTaskId(taskId);
    }
    
    /**
     * Add SMS data to a task
     */
    @Transactional
    public void addSmsData(String taskId, List<TaskDetail> smsDataList) {
        // Verify task exists
        AnalysisTask task = analysisTaskRepository.findByTaskId(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        
        // Set task ID for all details
        smsDataList.forEach(detail -> detail.setTaskId(taskId));
        
        // Save all SMS data
        taskDetailRepository.saveAll(smsDataList);
        
        log.info("Added {} SMS records to task {}", smsDataList.size(), taskId);
    }
    
    /**
     * Generate summary for a specific year-month
     */
    @Transactional
    public TaskSummary generateSummary(String taskId, String yearMonth) {
        // Get SMS data for the specific year-month
        List<TaskDetail> smsData = taskDetailRepository.findByTaskIdAndYearMonth(taskId, yearMonth);
        
        if (smsData.isEmpty()) {
            log.warn("No SMS data found for task {} in year-month {}", taskId, yearMonth);
            return null;
        }
        
        // Calculate statistics
        Map<String, Object> result = calculateStatistics(smsData);
        
        // Create or update summary
        TaskSummary summary = taskSummaryRepository.findByTaskIdAndYearMonth(taskId, yearMonth)
            .orElse(new TaskSummary());
        
        summary.setTaskId(taskId);
        summary.setYearMonth(yearMonth);
        summary.setTotalSms(smsData.size());
        summary.setResult(result);
        
        return taskSummaryRepository.save(summary);
    }
    
    /**
     * Complete analysis task and generate all summaries
     */
    @Transactional
    public void completeAnalysisTask(String taskId) {
        AnalysisTask task = analysisTaskRepository.findByTaskId(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        
        // Get all distinct year-months for this task
        List<String> yearMonths = taskDetailRepository.findDistinctYearMonthsByTaskId(taskId);
        
        // Generate summary for each year-month
        for (String yearMonth : yearMonths) {
            generateSummary(taskId, yearMonth);
        }
        
        // Update task status
        task.setStatus("COMPLETED");
        task.setEndTime(LocalDateTime.now());
        analysisTaskRepository.save(task);
        
        log.info("Completed analysis task {} with {} month summaries", taskId, yearMonths.size());
    }
    
    /**
     * Get all summaries for a task
     */
    public List<TaskSummary> getTaskSummaries(String taskId) {
        return taskSummaryRepository.findByTaskId(taskId);
    }
    
    /**
     * Calculate statistics from SMS data
     */
    private Map<String, Object> calculateStatistics(List<TaskDetail> smsData) {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic counts
        int totalCount = smsData.size();
        int incomingCount = 0;
        int outgoingCount = 0;
        
        // Address statistics
        Map<String, Integer> addressCounts = new HashMap<>();
        
        // Message length statistics
        List<Integer> messageLengths = new ArrayList<>();
        
        for (TaskDetail detail : smsData) {
            // Count by SMS type (1 = incoming, 2 = outgoing typically)
            if (detail.getSmsType() != null) {
                if (detail.getSmsType() == 1) {
                    incomingCount++;
                } else if (detail.getSmsType() == 2) {
                    outgoingCount++;
                }
            }
            
            // Count by address
            if (detail.getSmsAddress() != null) {
                addressCounts.put(detail.getSmsAddress(), 
                    addressCounts.getOrDefault(detail.getSmsAddress(), 0) + 1);
            }
            
            // Message length
            if (detail.getSmsBody() != null) {
                messageLengths.add(detail.getSmsBody().length());
            }
        }
        
        // Calculate averages and statistics
        double avgMessageLength = messageLengths.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        
        // Build result map
        stats.put("totalCount", totalCount);
        stats.put("incomingCount", incomingCount);
        stats.put("outgoingCount", outgoingCount);
        stats.put("averageMessageLength", avgMessageLength);
        stats.put("topAddresses", getTopAddresses(addressCounts, 5));
        stats.put("generatedAt", LocalDateTime.now().toString());
        
        return stats;
    }
    
    /**
     * Get top N addresses by message count
     */
    private List<Map<String, Object>> getTopAddresses(Map<String, Integer> addressCounts, int limit) {
        return addressCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Map<String, Object> item = new HashMap<>();
                item.put("address", entry.getKey());
                item.put("count", entry.getValue());
                return item;
            })
            .toList();
    }
}