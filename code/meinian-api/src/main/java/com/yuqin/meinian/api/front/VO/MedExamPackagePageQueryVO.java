package com.yuqin.meinian.api.front.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yuqin.meinian.api.common.BasePageDTO;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedExamPackagePageQueryVO extends BasePageDTO implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private String packageName;
    private String packageCode;
    private String description;
    private String coverImage;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal currentPrice;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal originalPrice;
    private Integer salesVolume;
    private Integer id;

}
