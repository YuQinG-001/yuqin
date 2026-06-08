package com.yuqin.meinian.api.schedule;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yuqin.meinian.api.db.entity.TradeOrderEntity;
import com.yuqin.meinian.api.db.mapper.TradeOrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class OrderSchedule {

    @Resource
    private TradeOrderMapper tradeOrderMapper;

    // 代表 每天每小时的第0分第0秒执行
    @Scheduled(cron = "0 0 * * * ?")  // 每小时执行一次
    @Transactional
    public void closeOrder() {
        LambdaUpdateWrapper<TradeOrderEntity> wrapper = Wrappers.lambdaUpdate(TradeOrderEntity.class)
                .set(TradeOrderEntity::getOrderStatus, 2)
                .eq(TradeOrderEntity::getOrderStatus, 1)
                .apply("TIMESTAMPDIFF(MINUTE, create_time, NOW()) > 20");

        int rows = tradeOrderMapper.update(wrapper);
        if (rows > 0) {
            log.info("定时任务关闭了{}个超时未支付的订单", rows);
        }
    }
}
