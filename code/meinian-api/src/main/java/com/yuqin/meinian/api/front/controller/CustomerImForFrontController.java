package com.yuqin.meinian.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.config.satoken.StpCustomerUtil;
import com.yuqin.meinian.api.service.CrmCustomerImService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/front/customer/im")
public class CustomerImForFrontController {

    private final CrmCustomerImService customerImService;

    @GetMapping("/createAccount")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R<Map<String, Object>> createAccount() {
        //从satoken中取出当前用户id
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        //发送IM请求，创建IM账号
        Map<String, Object> result = customerImService.createAccount(customerId);
        return R.ok(result);
    }
}