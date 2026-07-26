package com.yuqin.meinian.api.mis.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.service.CrmCustomerImService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/mis/customer/im")
public class CustomerImController {

    private final CrmCustomerImService customerImService;

    @GetMapping("/getServiceAccount")
    @SaCheckPermission(value = {"ROOT", "ORDER:SELECT"}, mode = SaMode.OR)
    public R<Map<String, Object>> getServiceAccount() {
        Map<String, Object> result = customerImService.getServiceAccount();
        return R.ok(result);
    }
}