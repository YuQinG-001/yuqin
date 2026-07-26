package com.yuqin.meinian.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.front.VO.CustomerUserVO;
import com.yuqin.meinian.api.mis.DTO.FindCustomerSummaryDTO;
import com.yuqin.meinian.api.service.CrmCustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/mis/customer")
public class CustomerController {

    private final CrmCustomerService customerService;

    @PostMapping("/findSummary")
    @SaCheckLogin
    public R<CustomerUserVO> findSummary(@RequestBody @Valid FindCustomerSummaryDTO form) {
        CustomerUserVO summary = customerService.findSummary(form.getCustomerId());
        return R.ok(summary);
    }
}