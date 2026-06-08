package com.yuqin.meinian.api.front.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerUserVO {
    private Integer       id;
    private String        customerName;
    private String        gender;
    private String        phone;
    private String        photoUrl;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime registerTime;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal    totalAmount;   // 总金额
    private Integer       totalCount;       // 订单总数
    private Integer       totalQuantity;    // 商品总数量
}
