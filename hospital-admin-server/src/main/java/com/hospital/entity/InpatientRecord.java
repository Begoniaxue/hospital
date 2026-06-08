package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inpatient_record")
public class InpatientRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long inpatientId;

    private String inpatientNo;

    private Long patientId;

    private String patientName;

    private LocalDateTime recordDate;

    private String recordType;

    private String title;

    private String content;

    private String vitalSigns;

    private Long doctorId;

    private String doctorName;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;

    @TableLogic
    private Integer deleted;
}
