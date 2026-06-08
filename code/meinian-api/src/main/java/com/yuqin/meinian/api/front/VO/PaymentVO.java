package com.yuqin.meinian.api.front.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVO {
    private String qrCode;
    private String outTradeNo;
}
