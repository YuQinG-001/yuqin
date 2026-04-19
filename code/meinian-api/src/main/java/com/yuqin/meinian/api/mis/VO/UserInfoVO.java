package com.yuqin.meinian.api.mis.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@Schema(description = "用户基本信息响应对象")
public class UserInfoVO {

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "性别（男/女）")
    private String gender;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "部门ID")
    private Integer deptId;

    @Schema(description = "入职日期")
    private LocalDate hireDate;

    @Schema(description = "角色ID集合")
    private String roleIds;

    @Schema(description = "是否超级管理员（0-否，1-是）")
    private Integer isSuperAdmin;

    @Schema(description = "用户状态（1-在职，2-离职）")
    private Integer userStatus;
}