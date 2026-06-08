package com.yuqin.meinian.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.mis.DTO.OrderPageQueryMisDTO;
import com.yuqin.meinian.api.mis.VO.OrderPageQueryMisVO;
import com.yuqin.meinian.api.service.TradeOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/mis/order")
@RequiredArgsConstructor
@RestController
public class TradeOrderController {
    private final TradeOrderService tradeOrderService;

    @PostMapping("/page")
    @SaCheckLogin()
    @SaCheckPermission(value = {"ROOT", "ORDER:SELECT"}, mode = SaMode.OR)
    public R<IPage<OrderPageQueryMisVO>> queryPage(@Valid @RequestBody OrderPageQueryMisDTO dto) {
        IPage<OrderPageQueryMisVO> orderPageQueryMisVOIPage = tradeOrderService.pageQueryByCondition(dto);
        return R.ok(orderPageQueryMisVOIPage);
    }
}
