package com.yuqin.meinian.api.mis.VO;

import com.yuqin.meinian.api.db.entity.SysUserEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@Schema(description = "登录成功响应对象")
public class LoginVO {

    @Schema(description = "登录结果（true-成功）")
    private Boolean result;

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "权限列表")
    private List<String> permissions;

    @Schema(description = "用户基本信息")
    private UserInfoVO userInfo;
}