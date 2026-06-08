package com.yuqin.meinian.api.front.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePayDTO {
    @NotNull(message = "goodsId不能为空")
    @Min(value = 1, message = "goodsId不能小于1")
    private Integer goodsId;

    @NotNull(message = "buyCount不能为空")
    @Min(value = 1, message = "buyCount不能小于1")
    private Integer buyCount;
}
