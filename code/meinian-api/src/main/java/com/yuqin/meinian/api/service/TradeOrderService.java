package com.yuqin.meinian.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import com.yuqin.meinian.api.db.entity.TradeOrderEntity;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.front.DTO.CreatePayDTO;
import com.yuqin.meinian.api.front.DTO.OrderPageQueryDTO;
import com.yuqin.meinian.api.front.VO.PaymentVO;
import com.yuqin.meinian.api.front.VO.TradeOrderPageFrontVO;
import com.yuqin.meinian.api.mis.DTO.OrderPageQueryMisDTO;
import com.yuqin.meinian.api.mis.VO.OrderPageQueryMisVO;

import java.util.Map;

/**
 * @author YuQin
 * @description 针对表【trade_order(交易订单表)】的数据库操作Service
 * @createDate 2026-04-03 02:27:34
 */
public interface TradeOrderService extends MPJBaseService<TradeOrderEntity> {
    /**
     * 创建支付订单
     *
     * @param dto        创建支付请求参数，包含商品ID、购买数量等信息
     * @param customerId 当前登录的客户ID
     * @return 支付信息，包含二维码Base64和商户订单号
     * @throws HisException 当每日限额超限、商品不存在、价格计算失败或微信支付下单失败时抛出
     */
    PaymentVO createPayment(CreatePayDTO dto, Integer customerId);

    /**
     * 更新支付结果（支付成功回调）
     *
     * @param transactionId 微信支付订单号（交易流水号）
     * @param outTradeNo    商户订单号
     * @return 更新数量
     */
    int updatePayment(String transactionId, String outTradeNo);

    /**
     * 根据交易流水号，同步付款结果
     * @param outTradeNos 要同步的订单的交易流水号
     * @return 同步的订单数量
     */
    int syncPaymentResult(String[] outTradeNos);
    /**
     * 清除Redis中缓存的支付二维码
     *
     * @param outTradeNo 商户订单号
     * @throws HisException 当订单流水号为空或未查询到对应订单时抛出
     */
    void clearRedisPayment(String outTradeNo);

    /**
     * 根据商户订单号查询客户ID
     *
     * @param outTradeNo 商户订单号
     * @return 客户ID
     * @throws HisException 当未查询到订单或订单对应的客户ID为空时抛出
     */
    Integer findCustomerIdByOutTradeNo(String outTradeNo);

    /**
     * 主动查询支付结果
     *
     * @param outTradeNo 商户订单号
     * @return true-支付成功，false-支付失败
     * @throws HisException 当微信支付查询接口调用失败时抛出
     */
    boolean getPaymentResult(String outTradeNo);

    /**
     * 分页条件查询订单列表
     *
     * @param customerId 客户ID
     * @param dto        分页查询条件，包含订单状态、关键词、页码、每页大小
     * @return 订单分页数据
     */
    IPage<TradeOrderPageFrontVO> findPageByCondition(Integer customerId, OrderPageQueryDTO dto);

    /**
     * 申请退款
     *
     * @param orderId 订单ID
     * @return true-退款申请成功，false-已退过款或退款失败
     * @throws HisException 当退款更新非唯一数据时抛出
     */
    boolean refund(Integer orderId);

    /**
     * 根据商户订单号修改订单状态（关闭订单）
     *
     * @param outTradeNo 商户订单号
     * @return true-修改成功，false-修改失败
     */
    boolean modifyStatusByOutTradeNo(String outTradeNo);

    /**
     * 根据订单ID修改订单状态（关闭订单）
     *
     * @param orderId 订单ID
     * @return true-修改成功，false-修改失败
     */
    boolean modifyStatusByOrderId(Integer orderId);


    IPage<OrderPageQueryMisVO> pageQueryByCondition(OrderPageQueryMisDTO dto);

    int removeByIdForMis(Integer id);

    boolean hasOwnOrder(Map<String, Object> param);
}
