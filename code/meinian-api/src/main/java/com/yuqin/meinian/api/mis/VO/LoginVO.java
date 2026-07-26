package com.yuqin.meinian.api.mis.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Schema(description = "登录成功响应对象")
public class LoginVO implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Schema(description = "登录结果（true-成功）")
    private Boolean result;

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "权限列表")
    private List<String> permissions;

    @Schema(description = "用户基本信息")
    private UserInfoVO userInfo;
}