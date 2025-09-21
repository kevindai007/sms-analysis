package com.kevindai.base.smsanalysis.controller;

import com.kevindai.base.smsanalysis.dto.ApiResponse;
import com.kevindai.base.smsanalysis.dto.SmsDataDto;
import com.kevindai.base.smsanalysis.dto.SmsUploadRequest;
import com.kevindai.base.smsanalysis.entity.AnalysisTask;
import com.kevindai.base.smsanalysis.entity.TaskDetail;
import com.kevindai.base.smsanalysis.entity.TaskSummary;
import com.kevindai.base.smsanalysis.service.SmsAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sms-analysis")
@RequiredArgsConstructor
@Slf4j
public class SmsAnalysisController {
    
    private final SmsAnalysisService smsAnalysisService;
    
    /**
     * Create a new analysis task
     */
    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<AnalysisTask>> createTask() {
        try {
            String taskId = UUID.randomUUID().toString();
            AnalysisTask task = smsAnalysisService.createAnalysisTask(taskId);
            return ResponseEntity.ok(ApiResponse.success("Analysis task created successfully", task));
        } catch (Exception e) {
            log.error("Error creating analysis task", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create analysis task: " + e.getMessage()));
        }
    }
    
    /**
     * Get analysis task by ID
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<AnalysisTask>> getTask(@PathVariable String taskId) {
        return smsAnalysisService.getAnalysisTask(taskId)
            .map(task -> ResponseEntity.ok(ApiResponse.success(task)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Upload SMS data to a task
     */
    @PostMapping("/tasks/{taskId}/upload")
    public ResponseEntity<ApiResponse<String>> uploadSmsData(
            @PathVariable String taskId,
            @RequestBody List<SmsDataDto> smsDataList) {
        try {
            // Convert DTOs to entities
            List<TaskDetail> taskDetails = smsDataList.stream()
                .map(this::convertToEntity)
                .toList();
            
            smsAnalysisService.addSmsData(taskId, taskDetails);
            return ResponseEntity.ok(ApiResponse.success("SMS data uploaded successfully", 
                "Uploaded " + smsDataList.size() + " SMS records"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading SMS data", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to upload SMS data: " + e.getMessage()));
        }
    }
    
    /**
     * Complete analysis and generate summaries
     */
    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<ApiResponse<String>> completeAnalysis(@PathVariable String taskId) {
        try {
            smsAnalysisService.completeAnalysisTask(taskId);
            return ResponseEntity.ok(ApiResponse.success("Analysis completed successfully", taskId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error completing analysis", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to complete analysis: " + e.getMessage()));
        }
    }
    
    /**
     * Get analysis summaries for a task
     */
    @GetMapping("/tasks/{taskId}/summaries")
    public ResponseEntity<ApiResponse<List<TaskSummary>>> getTaskSummaries(@PathVariable String taskId) {
        try {
            List<TaskSummary> summaries = smsAnalysisService.getTaskSummaries(taskId);
            return ResponseEntity.ok(ApiResponse.success(summaries));
        } catch (Exception e) {
            log.error("Error getting task summaries", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get summaries: " + e.getMessage()));
        }
    }
    
    /**
     * Generate summary for specific year-month
     */
    @PostMapping("/tasks/{taskId}/summaries/{yearMonth}")
    public ResponseEntity<ApiResponse<TaskSummary>> generateSummary(
            @PathVariable String taskId,
            @PathVariable String yearMonth) {
        try {
            TaskSummary summary = smsAnalysisService.generateSummary(taskId, yearMonth);
            if (summary != null) {
                return ResponseEntity.ok(ApiResponse.success("Summary generated successfully", summary));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("No data found for the specified year-month"));
            }
        } catch (Exception e) {
            log.error("Error generating summary", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate summary: " + e.getMessage()));
        }
    }
    
    /**
     * Convert DTO to entity
     */
    private TaskDetail convertToEntity(SmsDataDto dto) {
        TaskDetail detail = new TaskDetail();
        detail.setSmsId(dto.getSmsId());
        detail.setSmsAddress(dto.getSmsAddress());
        detail.setSmsDate(dto.getSmsDate());
        detail.setSmsDateSent(dto.getSmsDateSent());
        detail.setSmsBody(dto.getSmsBody());
        detail.setSmsType(dto.getSmsType());
        detail.setSmsThreadId(dto.getSmsThreadId());
        detail.setSmsSubscriptionId(dto.getSmsSubscriptionId());
        detail.setYearMonth(dto.getYearMonth());
        return detail;
    }
}