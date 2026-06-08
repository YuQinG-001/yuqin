package com.yuqin.meinian.api.db.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExaminationDetail implements Serializable {
    private String sex;
    private String code;
    private String item;       // 项目名（如“血糖”）
    private String name;       // 类别（如“实验室检查”）
    private String type;       // 导入类型
    private String unit;       // 单位
    private String place;      // 采血室等
    private String value;      // 值
    private String standard;   // 参考标准
    private String template;   // 模板
    @Serial
    private static final long serialVersionUID = 1L;
}
