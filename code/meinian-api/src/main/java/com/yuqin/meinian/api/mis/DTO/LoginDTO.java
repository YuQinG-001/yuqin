package com.yuqin.meinian.api.mis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "用户登录请求参数")
public class LoginDTO {

    @Schema(description = "用户名（5~50位字母数字）", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "username不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,50}$", message = "username格式错误")
    private String username;

    @Schema(description = "密码（6~20位字母数字）", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "password不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "password格式错误")
    private String password;
}