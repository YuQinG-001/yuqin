package com.yuqin.meinian.api.mis.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PackageWithRuleVO {
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