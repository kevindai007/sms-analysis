package com.kevindai.base.smsanalysis.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_detail", indexes = {
    @Index(name = "idx_task_detail_task_id", columnList = "task_id"),
    @Index(name = "idx_task_detail_year_month", columnList = "year_month")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_id", nullable = false)
    private String taskId;
    
    @Column(name = "year_month", nullable = false, length = 10)
    private String yearMonth;
    
    @Column(name = "sms_id")
    private String smsId;
    
    @Column(name = "sms_address")
    private String smsAddress;
    
    @Column(name = "sms_date")
    private Long smsDate;
    
    @Column(name = "sms_date_sent")
    private Long smsDateSent;
    
    @Column(name = "sms_body", columnDefinition = "TEXT")
    private String smsBody;
    
    @Column(name = "sms_type")
    private Integer smsType;
    
    @Column(name = "sms_thread_id")
    private Integer smsThreadId;
    
    @Column(name = "sms_subscription_id")
    private Integer smsSubscriptionId;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}