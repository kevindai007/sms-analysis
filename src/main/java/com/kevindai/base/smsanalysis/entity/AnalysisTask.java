package com.kevindai.base.smsanalysis.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_task", indexes = {
    @Index(name = "idx_task_id", columnList = "task_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_id", nullable = false, unique = true)
    private String taskId;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "status", length = 24)
    private String status;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructor with task_id only
    public AnalysisTask(String taskId) {
        this.taskId = taskId;
        this.status = "CREATED";
    }
}