package com.yuqin.meinian.api.front.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ModifyCustomerDTO {
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,10}$", message = "customerName内容不正确")
    private String customerName;

    @Pattern(regexp = "^男$|^女$", message = "gender内容不正确")
    private String gender;

    @NotBlank(message = "phone不能为空")
    @Pattern(regexp = "^1[1-9]\\d{9}$", message = "phone内容错误")
    private String phone;

    private String photoUrl;
}
