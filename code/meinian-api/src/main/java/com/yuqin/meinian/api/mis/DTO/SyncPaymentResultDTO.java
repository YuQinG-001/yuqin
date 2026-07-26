package com.yuqin.meinian.api.mis.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SyncPaymentResultDTO {
    @NotEmpty(message = "outTradeNos不能为空")
    private String[] outTradeNos;
}

