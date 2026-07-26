package com.yuqin.meinian.api.mis.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PackageWithRuleVO implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    private Integer    id;
    private String     packageName;
    private String     packageCode;
    private BigDecimal originalPrice;
    private BigDecimal currentPrice;
    private Integer    salesVolume;
    private String     packageType;
    private String     ruleName;
    private Boolean    hasCheckup;
    private Integer    status;
    private Integer    categoryId;

}