package com.yuqin.meinian.api.front.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefundFrontDTO {
    @NotNull
    @Min(value = 1, message = "orderId不能小于1")
    private Integer orderId;
}