package com.yuqin.meinian.api.mis.DTO;

import com.yuqin.meinian.api.common.BasePageDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderPageQueryMisDTO extends BasePageDTO {

    @Pattern(regexp = "^1[1-9]\\d{9}$", message = "phone内容不正确")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "packageCode内容不正确")
    private String packageCode;

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{1,50}$", message = "keyword内容不正确")
    private String keyword;

    @Range(min = 1, max = 6, message = "orderStatus内容不正确")
    private Integer orderStatus;

    private LocalDate startDate;

    private LocalDate endDate;
}
