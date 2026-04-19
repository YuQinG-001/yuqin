package com.yuqin.meinian.api.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuqin.meinian.api.db.entity.TradeOrderEntity;
import com.yuqin.meinian.api.service.TradeOrderService;
import com.yuqin.meinian.api.db.mapper.TradeOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author YuQin
* @description 针对表【trade_order(交易订单表)】的数据库操作Service实现
* @createDate 2026-04-03 02:27:34
*/
@Service
public class TradeOrderServiceImpl extends ServiceImpl<TradeOrderMapper, TradeOrderEntity>
    implements TradeOrderService{

}




