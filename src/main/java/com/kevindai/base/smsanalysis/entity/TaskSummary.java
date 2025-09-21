package com.kevindai.base.smsanalysis.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "task_summary", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"task_id", "year_month"})
       },
       indexes = {
           @Index(name = "idx_task_summary_task_id", columnList = "task_id"),
           @Index(name = "idx_task_summary_year_month", columnList = "year_month")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_id", nullable = false)
    private String taskId;
    
    @Column(name = "year_month", nullable = false, length = 10)
    private String yearMonth;
    
    @Column(name = "total_sms")
    private Integer totalSms;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result", columnDefinition = "jsonb")
    private Map<String, Object> result;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}