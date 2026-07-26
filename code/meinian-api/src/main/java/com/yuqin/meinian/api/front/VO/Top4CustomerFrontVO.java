package com.yuqin.meinian.api.front.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Top4CustomerFrontVO implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

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
