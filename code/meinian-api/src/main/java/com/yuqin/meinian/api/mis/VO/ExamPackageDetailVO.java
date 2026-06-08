package com.yuqin.meinian.api.mis.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.yuqin.meinian.api.db.entity.ExamItem;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ExamPackageDetailVO implements Serializable {
    private String packageCode;
    private String packageName;
    private String description;
    private String coverImage;
    private String originalPrice;
    private String currentPrice;
    private Long   ruleId;
    private String ruleName;
    private String packageType;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    private String categoryId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<ExamItem> departmentExam;

    private Integer count1;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<ExamItem> labExam;

    private Integer count2;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<ExamItem> medicalExam;

    private Integer count3;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<ExamItem> otherExam;

    private Integer count4;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
