package com.yuqin.meinian.api.mis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "修改密码请求参数")
public class UpdatePasswordDTO {

    @Schema(description = "原密码（6~20位字母数字）", example = "old123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "原密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "原密码格式错误")
    private String oldPassword;

    @Schema(description = "新密码（6~20位字母数字）", example = "new123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "新密码格式错误")
    private String newPassword;
}