package com.yuqin.meinian.api.front.DTO;

import com.yuqin.meinian.api.common.BasePageDTO;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderPageQueryDTO extends BasePageDTO {
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{1,50}$", message = "keyword内容不正确")
    private String keyword;

    @Pattern(regexp = "^1$|^3$", message = "orderStatus内容不正确")
    private String orderStatus;

}