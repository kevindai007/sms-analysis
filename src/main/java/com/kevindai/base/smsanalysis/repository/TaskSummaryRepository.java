package com.kevindai.base.smsanalysis.repository;

import com.kevindai.base.smsanalysis.entity.TaskSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskSummaryRepository extends JpaRepository<TaskSummary, Long> {
    
    List<TaskSummary> findByTaskId(String taskId);
    
    Optional<TaskSummary> findByTaskIdAndYearMonth(String taskId, String yearMonth);
    
    boolean existsByTaskIdAndYearMonth(String taskId, String yearMonth);
}