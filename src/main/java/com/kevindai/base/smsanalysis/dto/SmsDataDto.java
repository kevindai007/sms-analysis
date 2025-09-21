package com.kevindai.base.smsanalysis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsDataDto {
    private String smsId;
    private String smsAddress;
    private Long smsDate;
    private Long smsDateSent;
    private String smsBody;
    private Integer smsType;
    private Integer smsThreadId;
    private Integer smsSubscriptionId;
    private String yearMonth;  // Format: "YYYY-MM"
}