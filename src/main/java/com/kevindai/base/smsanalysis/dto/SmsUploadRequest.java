package com.kevindai.base.smsanalysis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsUploadRequest {
    private String taskId;
    private List<SmsDataDto> smsDataList;
}