package com.yuqin.meinian.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.db.entity.SysUserEntity;
import com.yuqin.meinian.api.mis.DTO.*;
import com.yuqin.meinian.api.mis.VO.LoginVO;
import com.yuqin.meinian.api.mis.VO.UserInfoVO;
import com.yuqin.meinian.api.mis.VO.UserPageVO;
import com.yuqin.meinian.api.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "用户管理", description = "提供用户登录、登出、密码修改等核心功能")
@RestController
@Validated
@RequestMapping("/mis/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService sysUserService;

    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录，成功后将返回 Token 及权限列表。")
    @PostMapping("/login")
    public R<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        Integer id = sysUserService.login(loginDTO);
        if (id == null) {
            return R.error("用户名或密码错误");
        }
        // 注意：设备类型统一使用 "web"（与 logout 保持一致）
        StpUtil.login(id, "web");
        LoginVO loginVO = LoginVO.builder()
                .result(true)
                .token(StpUtil.getTokenValue())
                .permissions(StpUtil.getPermissionList())
                .build();
        return R.ok(loginVO);
    }

    @Operation(summary = "用户退出", description = "使当前登录用户的 Token 失效，退出系统。需要携带有效的 Token。")
    @SaCheckLogin
    @GetMapping("/logout")
    public R<Void> logout() {
        int loginIdAsInt = StpUtil.getLoginIdAsInt();
        StpUtil.logout(loginIdAsInt, "web");
        return R.ok();
    }

    @Operation(summary = "修改密码", description = "登录用户修改自己的账户密码。需要提供旧密码和新密码，且新旧密码不能相同。")
    @SaCheckLogin
    @PostMapping("/modifyPassword")
    public R<Boolean> modifyPassword(@RequestBody @Valid UpdatePasswordDTO dto) {
        // 校验新旧密码不能相同
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            return R.error("新密码不能与旧密码相同");
        }
        // 修改成功后，如果需要强制重新登录，可以在此处调用登出逻辑
        // 这里暂不自动登出，由前端决定是否跳转登录页
        return R.ok("密码修改成功", sysUserService.modifyPassword(dto));

    }

    @PostMapping("/page")
    @Operation(summary = "user分页", description = "获取user分页信息")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public R<IPage<UserPageVO>> page(@RequestBody @Valid QueryUserPageDTO dto) {
        IPage<UserPageVO> userPageVOIPage = sysUserService.queryUserWithRolesPage(dto);
        return R.ok(userPageVOIPage);
    }

    @Operation(summary = "保存用户信息", description = "保存用户信息")
    @SaCheckLogin
    @PostMapping("/save")
    @SaCheckPermission(value = {"ROOT", "USER:INSERT"}, mode = SaMode.OR)
    public R<Boolean> save(@RequestBody @Valid SaveUserDTO dto) {
        SysUserEntity sysUserEntity = BeanUtil.copyProperties(dto, SysUserEntity.class);
        sysUserEntity.setRoleIds(JSONUtil.toJsonStr(dto.getRoleIds()));
        boolean save = sysUserService.saveUser(sysUserEntity);
        return R.ok(save);
    }

    @Operation(summary = "查询用户信息", description = "根据ID查询用户信息，用于modal窗口的修改")
    @SaCheckLogin
    @GetMapping("/queryUserById")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public R<UserInfoVO> queryUserById(@RequestParam @NotNull(message = "userId不能为空")
                                       @Min(value = 0, message = "userId不能小于0")
                                       @Parameter(description = "User表的主键", example = "1", required = true)
                                       Integer userId) {
        SysUserEntity sysUserEntity = sysUserService.queryUser(userId);
        return R.ok(BeanUtil.copyProperties(sysUserEntity, UserInfoVO.class));
    }

    @Operation(summary = "更新用户信息", description = "将信息修改为模态框修改后的数据,返回值true或者异常")
    @SaCheckLogin
    @PutMapping("/modifyUser")
    @SaCheckPermission(value = {"ROOT", "USER:UPDATE"}, mode = SaMode.OR)
    public R<String> modifyUser(@RequestBody @Valid UpdateUserDTO dto) {
        int i = sysUserService.modifyUser(dto);
        return R.ok("成功修改 " + i + " 条数据");
    }

    @Operation(summary = "删除用户信息", description = "批量删除用户，亦可删除指定用户")
    @SaCheckLogin
    @DeleteMapping("/removeByIds")
    @SaCheckPermission(value = {"ROOT", "USER:DELETE"}, mode = SaMode.OR)
    public R<String> removeByIds(
            @Parameter(description = "用户ID数组，至少提供一个ID", required = true)
            @RequestParam
            @NotNull(message = "ids 不能为 null")
            @Size(min = 1, message = "至少需要提供一个用户ID")
            List<Integer> ids) {
        int i = sysUserService.removeUserByIds(ids);
        ids.forEach(StpUtil::logout);
        return R.ok("已删除：" + i + " 条数据");
    }

    @Operation(summary = "用户离职", description = "指定用户离职,后续可能会扩展")
    @SaCheckLogin
    @PutMapping("/dismiss")
    @SaCheckPermission(value = {"ROOT", "USER:UPDATE"}, mode = SaMode.OR)
    public R<String> dismiss(
            @Parameter(description = "用户ID数组，至少提供一个ID", required = true)
            @RequestParam
            @NotNull(message = "ids 不能为 null")
            @Size(min = 1, message = "至少需要提供一个用户ID")
            @Valid List<Integer> ids) {
        int updatedCount = sysUserService.dismissUser(ids);
        return R.ok("成功设置 " + updatedCount + " 位用户离职");
    }

}