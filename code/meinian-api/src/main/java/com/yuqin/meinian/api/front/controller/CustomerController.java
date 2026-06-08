package com.yuqin.meinian.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.config.satoken.StpCustomerUtil;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.front.DTO.CreatePayDTO;
import com.yuqin.meinian.api.front.DTO.FindTop4ByCategoryIdsDTO;
import com.yuqin.meinian.api.front.DTO.ModifyCustomerDTO;
import com.yuqin.meinian.api.front.VO.CustomerLoginVO;
import com.yuqin.meinian.api.front.VO.CustomerUserVO;
import com.yuqin.meinian.api.service.CrmCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RequestMapping("/front/customer")
@RequiredArgsConstructor
@RestController
@Slf4j
public class CustomerController {
    private final CrmCustomerService crmCustomerService;

    @GetMapping("/isLogin")
    public R<Void> checkLogin() {
        boolean login = StpCustomerUtil.isLogin();
        if (!login) {
            throw new HisException("用户登入过期");
        }
        return R.ok();
    }

    @GetMapping("/sendSmsCode")
    public R<Boolean> sendSmsCode(
            @RequestParam
            @NotBlank(message = "phone不能为空")
            @Pattern(regexp = "^1[1-9]\\d{9}$", message = "phone内容错误")
            String phone) {
        boolean b = crmCustomerService.sendSmsCode(phone);
        return R.ok(b);
    }

    @PostMapping("/login")
    public R<CustomerLoginVO> login(
            @RequestParam @NotBlank(message = "手机号不能为空") @Pattern(regexp = "^1[1-9]\\d{9}$", message = "请正确输入手机号") String phone,
            @RequestParam @NotBlank(message = "验证码不能为空") @Pattern(regexp = "^\\d{6}$", message = "请正确输入6位验证码") String code) {
        CustomerLoginVO loginVO = crmCustomerService.login(phone, code);
        return R.ok(loginVO);
    }

    @GetMapping("/logout")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R<Void> logout() {
        int loginIdAsInt = StpCustomerUtil.getLoginIdAsInt();
        StpCustomerUtil.logout(loginIdAsInt, "PC");
        return R.ok();
    }

    @GetMapping("/getSummary")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R<CustomerUserVO> getSummary() {
        CustomerUserVO customerUserVO = crmCustomerService.selectByLoginIdForFront();
        return R.ok(customerUserVO);
    }

    @PostMapping("/modify")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R<String> modify(@RequestBody @Valid ModifyCustomerDTO dto) {
        int modify = crmCustomerService.modify(dto);
        return R.ok("已更新"+ modify +"条数据量");
    }
    @PostMapping("/uploadImage")
    @Operation(summary = "上传图片", description = "将图片文件上传到MinIO对象存储")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 实际调用 MinIO 上传服务，返回文件路径
        String fileUrl = crmCustomerService.upLoad(file);
        return R.ok(fileUrl);
    }
}
