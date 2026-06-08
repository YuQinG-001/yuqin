package com.yuqin.meinian.api.front.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerLoginVO {
    private String token;
    private Integer Id;
}
