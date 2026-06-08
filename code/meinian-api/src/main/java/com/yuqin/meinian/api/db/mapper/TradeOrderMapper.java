package com.yuqin.meinian.api.db.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.yuqin.meinian.api.db.entity.TradeOrderEntity;
import com.yuqin.meinian.api.mis.DTO.OrderPageQueryMisDTO;
import com.yuqin.meinian.api.mis.VO.OrderPageQueryMisVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author YuQin
 * @description 针对表【trade_order(交易订单表)】的数据库操作Mapper
 * @createDate 2026-04-03 02:27:34
 * @Entity com.yuqin.meinian.api.db.entity.TradeOrderEntity
 */
public interface TradeOrderMapper extends MPJBaseMapper<TradeOrderEntity> {


    IPage<OrderPageQueryMisVO> selectPageVO(Page<?> page, @Param("dto") OrderPageQueryMisDTO dto);

    TradeOrderEntity selectTranIdAndAmountByOrderId(@Param("loginIdAsInt") Integer loginIdAsInt,@Param("orderId") Integer orderId);
}




