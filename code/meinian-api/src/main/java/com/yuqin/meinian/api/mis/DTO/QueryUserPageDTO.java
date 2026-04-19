package com.yuqin.meinian.api.mis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "用户分页查询请求参数")
public class QueryUserPageDTO extends BasePageDTO {

    @Schema(description = "真实姓名（1~10位纯中文）", example = "张三")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,10}$", message = "realName内容不正确")
    private String realName;

    @Schema(description = "性别（男/女）", example = "男")
    @Pattern(regexp = "^男$|^女$", message = "gender内容不正确")
    private String gender;

    @Schema(description = "部门ID（≥1）", example = "1001")
    @Min(value = 1, message = "deptId不能小于1")
    private Integer deptId;

    @Schema(description = "角色ID（≥0）", example = "5")
    @Min(value = 0, message = "roleId不能小于0")
    private Integer roleId;

    @Schema(description = "用户状态（1-在职，2-离职）", example = "1")
    @Range(min = 1, max = 2, message = "userStatus只能是1或2")
    private Integer userStatus;
}