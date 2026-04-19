package com.yuqin.meinian.api.mis.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色ID和名称响应对象")
public class RoleNameListVO {

    @Schema(description = "角色ID")
    private Integer roleId;

    @Schema(description = "角色名称")
    private String roleName;
}