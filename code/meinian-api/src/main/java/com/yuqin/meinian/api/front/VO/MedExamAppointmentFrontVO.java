package com.yuqin.meinian.api.front.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class MedExamAppointmentFrontVO implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    private String patientName;      // 患者姓名
    private String gender;           // 性别
    private String phone;            // 电话
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate appointmentDate; //预约时间
    private Integer status;           // 状态
    private Integer age;             // 年龄（实时计算）
    private String goodsTitle;
}
