package com.yuqin.meinian.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.mis.DTO.OrderPageQueryMisDTO;
import com.yuqin.meinian.api.mis.DTO.ProcessOfflineRefundDTO;
import com.yuqin.meinian.api.mis.DTO.SyncPaymentResultDTO;
import com.yuqin.meinian.api.mis.VO.OrderPageQueryMisVO;
import com.yuqin.meinian.api.service.TradeOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/mis/order")
@RequiredArgsConstructor
@RestController
@Validated
public class TradeOrderController {
    private final TradeOrderService tradeOrderService;

    @PostMapping("/page")
    @SaCheckLogin()
    @SaCheckPermission(value = {"ROOT", "ORDER:SELECT"}, mode = SaMode.OR)
    public R<IPage<OrderPageQueryMisVO>> queryPage(@Valid @RequestBody OrderPageQueryMisDTO dto) {
        IPage<OrderPageQueryMisVO> orderPageQueryMisVOIPage = tradeOrderService.pageQueryByCondition(dto);
        return R.ok(orderPageQueryMisVOIPage);
    }

    @PostMapping("/syncPaymentResult")
    @SaCheckLogin()
    @SaCheckPermission(value = {"ROOT", "ORDER:UPDATE"}, mode = SaMode.OR)
    public R<Integer> syncPaymentResult(@Valid @RequestBody SyncPaymentResultDTO dto) {
        int i = tradeOrderService.syncPaymentResult(dto.getOutTradeNos());
        return R.ok(i);
    }

    @DeleteMapping("/delete")
    @SaCheckLogin()
    @SaCheckPermission(value = {"ROOT", "ORDER:DELETE"}, mode = SaMode.OR)
    public R<Integer> removeById(@RequestParam
                                 @NotNull(message = "orderId不能为空")
                                 @Min(value = 1, message = "orderId不能小于1") Integer orderId) {
        int i = tradeOrderService.removeByIdForMis(orderId);
        return R.ok(i);
    }

    @PutMapping("/offlineRefund")
    @SaCheckPermission(value = {"ROOT", "ORDER:UPDATE"}, mode = SaMode.OR)
    public R<Boolean> offlineRefund(@RequestBody @Valid ProcessOfflineRefundDTO form){
        String outTradeNo = form.getOutTradeNo();
        boolean success = tradeOrderService.modifyStatusByOutTradeNo(outTradeNo);
        return R.ok(success);
    }
}
