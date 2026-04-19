package com.yuqin.meinian.api.mis.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "用户分页查询响应对象")
public class UserPageVO {

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "性别（男/女）")
    private String gender;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "入职日期")
    private LocalDate hireDate;

    @Schema(description = "所属部门名称")
    private String deptName;

    @Schema(description = "角色列表（逗号分隔）")
    private String roles;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户状态（1-在职，2-离职）")
    private Integer userStatus;

    @Schema(description = "是否超级管理员（0-否，1-是）")
    private Integer isSuperAdmin;
}