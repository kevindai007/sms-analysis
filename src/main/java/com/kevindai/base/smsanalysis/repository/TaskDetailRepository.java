package com.kevindai.base.smsanalysis.repository;

import com.kevindai.base.smsanalysis.entity.TaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDetailRepository extends JpaRepository<TaskDetail, Long> {
    
    List<TaskDetail> findByTaskId(String taskId);
    
    List<TaskDetail> findByTaskIdAndYearMonth(String taskId, String yearMonth);
    
    @Query("SELECT COUNT(td) FROM TaskDetail td WHERE td.taskId = :taskId")
    long countByTaskId(@Param("taskId") String taskId);
    
    @Query("SELECT COUNT(td) FROM TaskDetail td WHERE td.taskId = :taskId AND td.yearMonth = :yearMonth")
    long countByTaskIdAndYearMonth(@Param("taskId") String taskId, @Param("yearMonth") String yearMonth);
    
    @Query("SELECT DISTINCT td.yearMonth FROM TaskDetail td WHERE td.taskId = :taskId ORDER BY td.yearMonth")
    List<String> findDistinctYearMonthsByTaskId(@Param("taskId") String taskId);
}