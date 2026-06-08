package com.yuqin.meinian.api.front.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Top4CustomerFrontVO {
    private Integer    id;
    private String     packageCode;
    private String     coverImage;
    private String     packageName;
    private String     description;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal currentPrice;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal originalPrice;
    private Integer    salesVolume;
}
