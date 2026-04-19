package com.yuqin.meinian.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.mis.VO.DeptIdNameVO;
import com.yuqin.meinian.api.service.SysDepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "部门管理", description = "提供部门信息的查询、新增、修改、删除等核心功能")
@RequestMapping("/mis/dept")
public class DeptController {
    private final SysDepartmentService sysDepartmentService;

    @GetMapping("/list")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "DEPT:SELECT"}, mode = SaMode.OR)
    @Operation(summary = "部门列表", description = "获取所有部门列表")
    public R<List<DeptIdNameVO>> list() {
        List<DeptIdNameVO> deptIdNameVOS = sysDepartmentService.queryDeptIdName();
        return R.ok(deptIdNameVOS);
    }
}
