package com.yuqin.meinian.api.db.entity;

import lombok.Data;

// 体检项目详情（替代 Map）
@Data
public class CheckupItem {
    private String item;   // 项目名称，如 "裸眼视力(左)"
    private String code;   // 项目编码
    private String sex;    // 适用性别："无"、"男"、"女"
    private String name;   // 所属分类，如 "眼科检查"
    private String place;  // 科室
    private String type;   // 录入方式
    private String value;  // 结果值
}