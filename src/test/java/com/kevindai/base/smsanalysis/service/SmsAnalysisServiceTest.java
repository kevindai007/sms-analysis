package com.kevindai.base.smsanalysis.service;

import com.kevindai.base.smsanalysis.entity.AnalysisTask;
import com.kevindai.base.smsanalysis.entity.TaskDetail;
import com.kevindai.base.smsanalysis.repository.AnalysisTaskRepository;
import com.kevindai.base.smsanalysis.repository.TaskDetailRepository;
import com.kevindai.base.smsanalysis.repository.TaskSummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmsAnalysisServiceTest {

    @Mock
    private AnalysisTaskRepository analysisTaskRepository;
    
    @Mock
    private TaskDetailRepository taskDetailRepository;
    
    @Mock
    private TaskSummaryRepository taskSummaryRepository;
    
    @InjectMocks
    private SmsAnalysisService smsAnalysisService;

    @Test
    void createAnalysisTask_Success() {
        // Given
        String taskId = "test-task-id";
        when(analysisTaskRepository.existsByTaskId(taskId)).thenReturn(false);
        when(analysisTaskRepository.save(any(AnalysisTask.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        AnalysisTask result = smsAnalysisService.createAnalysisTask(taskId);
        
        // Then
        assertNotNull(result);
        assertEquals(taskId, result.getTaskId());
        assertEquals("IN_PROGRESS", result.getStatus());
        assertNotNull(result.getStartTime());
        verify(analysisTaskRepository).save(any(AnalysisTask.class));
    }

    @Test
    void createAnalysisTask_TaskAlreadyExists_ThrowsException() {
        // Given
        String taskId = "existing-task-id";
        when(analysisTaskRepository.existsByTaskId(taskId)).thenReturn(true);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> smsAnalysisService.createAnalysisTask(taskId));
        verify(analysisTaskRepository, never()).save(any(AnalysisTask.class));
    }

    @Test
    void getAnalysisTask_Success() {
        // Given
        String taskId = "test-task-id";
        AnalysisTask task = new AnalysisTask(taskId);
        when(analysisTaskRepository.findByTaskId(taskId)).thenReturn(Optional.of(task));
        
        // When
        Optional<AnalysisTask> result = smsAnalysisService.getAnalysisTask(taskId);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(taskId, result.get().getTaskId());
    }

    @Test
    void addSmsData_Success() {
        // Given
        String taskId = "test-task-id";
        AnalysisTask task = new AnalysisTask(taskId);
        TaskDetail detail1 = new TaskDetail();
        detail1.setSmsBody("Test message 1");
        TaskDetail detail2 = new TaskDetail();
        detail2.setSmsBody("Test message 2");
        
        when(analysisTaskRepository.findByTaskId(taskId)).thenReturn(Optional.of(task));
        when(taskDetailRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        smsAnalysisService.addSmsData(taskId, Arrays.asList(detail1, detail2));
        
        // Then
        assertEquals(taskId, detail1.getTaskId());
        assertEquals(taskId, detail2.getTaskId());
        verify(taskDetailRepository).saveAll(any());
    }

    @Test
    void addSmsData_TaskNotFound_ThrowsException() {
        // Given
        String taskId = "non-existent-task";
        when(analysisTaskRepository.findByTaskId(taskId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> smsAnalysisService.addSmsData(taskId, Arrays.asList(new TaskDetail())));
        verify(taskDetailRepository, never()).saveAll(any());
    }
}