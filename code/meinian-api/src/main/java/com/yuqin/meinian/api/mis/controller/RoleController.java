package com.yuqin.meinian.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.mis.VO.RoleNameListVO;
import com.yuqin.meinian.api.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/mis/role")
@RestController
@Tag(name = "权限管理", description = "提供用户权限控制、修改等核心功能")
public class RoleController {
    private final SysRoleService sysRoleService;

    @GetMapping("/list")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    @Operation(summary = "权限列表", description = "获取所有权限列表")
    public R<List<RoleNameListVO>> listRoles() {
        List<RoleNameListVO> roleList = sysRoleService.queryRoleNameList();
        return R.ok(roleList);
    }
}
