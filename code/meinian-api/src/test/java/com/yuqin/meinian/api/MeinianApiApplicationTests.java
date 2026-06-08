package com.yuqin.meinian.api;

import cn.hutool.json.JSONUtil;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.yuqin.meinian.api.config.TestConfig;
import com.yuqin.meinian.api.config.satoken.StpCustomerUtil;
import com.yuqin.meinian.api.db.entity.SysUserEntity;
import com.yuqin.meinian.api.db.entity.TradeOrderEntity;
import com.yuqin.meinian.api.db.mapper.MedExamPackageMapper;
import com.yuqin.meinian.api.db.mapper.SysUserMapper;
import com.yuqin.meinian.api.mis.VO.ExamPackageDetailVO;
import com.yuqin.meinian.api.service.MedExamPackageService;
import com.yuqin.meinian.api.service.TradeOrderService;
import com.yuqin.meinian.api.serviceImpl.TradeOrderServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalTime.now;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MeinianApiApplicationTests {

    @Resource
    private SysUserMapper        sysUserMapper;
    @Autowired
    private MedExamPackageMapper medExamPackageMapper;
private  TradeOrderServiceImpl tradeOrderService;
    @Test
    void test001() {

        System.out.println("-------------------"+ LocalDateTime.now().toLocalDate());
    }
    @Test
    void test002() {
        // 模拟用户登录（假设 Sa-Token）
        TradeOrderEntity tradeOrderEntity = tradeOrderService.selectTranIdAndAmountByOrderId(25);
        System.out.println(tradeOrderEntity);
        if (tradeOrderEntity != null) {
            System.out.println("transactionId: " + tradeOrderEntity.getTransactionId());
            System.out.println("totalAmount: " + tradeOrderEntity.getTotalAmount());
        }
    }
}
