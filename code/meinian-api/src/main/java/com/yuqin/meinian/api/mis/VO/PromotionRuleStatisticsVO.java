package com.yuqin.meinian.api.mis.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


@Data
public class PromotionRuleStatisticsVO implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 促销规则ID
     */
    private Integer ruleId;

    /**
     * 促销规则名称
     */
    private String ruleName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 关联的体检套餐数量
     */
    private Integer count;
}