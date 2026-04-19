package com.yuqin.meinian.api.mis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "保存用户信息请求参数")
public class SaveUserDTO {
    @Schema(description = "用户名（5~20位字母数字）", example = "zhangsan123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "username不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,20}$", message = "username内容不正确")
    private String username;

    @Schema(description = "密码（6~20位字母数字）", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "password不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "password内容不正确")
    private String password;

    @Schema(description = "真实姓名（2~10位纯中文）", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "姓名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,10}$", message = "姓名内容不正确")
    private String realName;

    @Schema(description = "性别（男/女）", example = "男", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "gender不能为空")
    @Pattern(regexp = "^男$|^女$", message = "gender内容不正确")
    private String gender;

    @Schema(description = "手机号码（11位符合中国大陆手机号格式）", example = "13812345678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "mobile不能为空")
    @Pattern(regexp = "^1[1-9]\\d{9}$", message = "mobile内容不正确")
    private String mobile;

    @Schema(description = "邮箱地址", example = "zhangsan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "email内容不正确")
    @Email(message = "email内容不正确")
    private String email;

    @Schema(description = "部门ID（≥1）", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "deptId不能小于1")
    private Integer deptId;

    @Schema(description = "入职日期（YYYY-MM-DD格式）", example = "2023-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "hireDate不能为空")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29))$", message = "hireDate内容不正确")
    private String hireDate;

    @Schema(description = "角色ID数组（至少包含一个角色ID）", example = "[1,2]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "roleIds不能为空")
    private Integer[] roleIds;

    @Schema(description = "用户状态（1-在职，2-离职）", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "userStatus不能为空")
    private Integer userStatus = 1;

}
