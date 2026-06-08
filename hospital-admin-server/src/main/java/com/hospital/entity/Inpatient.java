package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inpatient")
public class Inpatient {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String inpatientNo;

    private Long patientId;

    private String patientName;

    private Integer patientGender;

    private Integer patientAge;

    private LocalDateTime admissionDate;

    private Integer expectedDays;

    private String department;

    private Long doctorId;

    private String doctorName;

    private Long bedId;

    private String bedNo;

    private Long wardId;

    private String wardName;

    private String diagnosis;

    private Integer admissionType;

    private Integer status;

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
