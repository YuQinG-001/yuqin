package com.yuqin.meinian.api.serviceImpl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.yuqin.meinian.api.config.satoken.StpCustomerUtil;
import com.yuqin.meinian.api.db.entity.GoodsSnapshotEntity;
import com.yuqin.meinian.api.db.entity.MedExamAppointmentEntity;
import com.yuqin.meinian.api.db.entity.MedExamPackageEntity;
import com.yuqin.meinian.api.db.entity.TradeOrderEntity;
import com.yuqin.meinian.api.db.mapper.MedExamPackageMapper;
import com.yuqin.meinian.api.db.mapper.TradeOrderMapper;
import com.yuqin.meinian.api.db.mongo.GoodsSnapshotDao;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.front.DTO.CreatePayDTO;
import com.yuqin.meinian.api.front.DTO.OrderPageQueryDTO;
import com.yuqin.meinian.api.front.VO.PaymentVO;
import com.yuqin.meinian.api.front.VO.TradeOrderPageFrontVO;
import com.yuqin.meinian.api.mis.DTO.OrderPageQueryMisDTO;
import com.yuqin.meinian.api.mis.VO.OrderPageQueryMisVO;
import com.yuqin.meinian.api.service.MedExamPackageService;
import com.yuqin.meinian.api.service.PaymentService;
import com.yuqin.meinian.api.service.TradeOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author YuQin
 * @description 针对表【trade_order(交易订单表)】的数据库操作Service实现
 * @createDate 2026-04-03 02:27:34
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeOrderServiceImpl extends MPJBaseServiceImpl<TradeOrderMapper, TradeOrderEntity>
        implements TradeOrderService {

    private final MedExamPackageService medExamPackageService;
    private final PaymentService paymentService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GoodsSnapshotDao goodsSnapshotDao;
    private final TradeOrderMapper tradeOrderMapper;
    private final MedExamPackageMapper medExamPackageMapper;
    // 常量定义
    private static final int QR_CODE_WIDTH = 230;
    private static final int QR_CODE_HEIGHT = 230;
    private static final int QR_CODE_MARGIN = 2;
    private static final String QR_CODE_IMAGE_FORMAT = "jpg";
    private static final String CODE_URL_CACHE_PREFIX = "codeUrl:";
    private static final int ORDER_EXPIRE_MINUTES = 20;
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final String WECHAT_PAY_DESC = "购买体检套餐";
    @Value("${wechat.pay.v3.meinian-vue.domain}")
    String domain;
    // 规则执行器单例（避免每次都 new）
    private final ExpressRunner expressRunner = new ExpressRunner();


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO createPayment(CreatePayDTO dto, Integer customerId) {
        // 1. 每日限额校验（失败直接抛异常，不返回 null）
        checkDailyLimit(customerId);
        // 2. 获取商品快照（含促销规则）
        GoodsSnapshotEntity snapshot = getGoodsSnapshotWithCache(dto.getGoodsId());
        // 3. 计算最终应付金额
        BigDecimal amount = calculateFinalAmount(snapshot, dto.getBuyCount());
        // 4. 调用微信支付统一下单，获取 code_url 和订单号
        WechatPayResult payResult = createWechatPayOrder(customerId, amount);

        // 5. 保存订单记录到数据库
        @SuppressWarnings("unused")
        TradeOrderEntity order = saveOrder(snapshot, amount, payResult, dto, customerId);

        // 6. 更新商品销量（原子操作）
        updateSalesVolume(dto.getGoodsId(), dto.getBuyCount());

        // 7. 生成二维码 base64 并返回
        String qrCodeBase64 = generateQrCodeBase64(payResult.codeUrl());

        return PaymentVO.builder()
                .qrCode(qrCodeBase64)
                .outTradeNo(payResult.outTradeNo())
                .build();
    }

    @Override
    @Transactional
    public int syncPaymentResult(String[] outTradeNos) {
        if (outTradeNos == null || outTradeNos.length == 0) {
            throw new HisException("未填入订单流水号：outTradeNo");
        }
        int result = 0;
        for (String outTradeNo : outTradeNos) {
            String transactionId = paymentService.getPaymentResult(outTradeNo);
            result += this.updatePayment(transactionId, outTradeNo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayment(String transactionId, String outTradeNo) {
        Wrapper<TradeOrderEntity> wrapper = Wrappers.<TradeOrderEntity>lambdaUpdate()
                .set(TradeOrderEntity::getOutTradeNo, outTradeNo)
                .set(TradeOrderEntity::getTransactionId, transactionId)
                .set(TradeOrderEntity::getOrderStatus, 3)
                .eq(TradeOrderEntity::getOutTradeNo, outTradeNo)
                .in(TradeOrderEntity::getOrderStatus, 1, 2);
        TradeOrderEntity tOrder = new TradeOrderEntity();
        return baseMapper.update(tOrder, wrapper);
    }

    @Override
    public Integer findCustomerIdByOutTradeNo(String outTradeNo) {
        return getCustomerIdByOutTradeNo(outTradeNo);
    }

    private Integer getCustomerIdByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<TradeOrderEntity> wrapper = Wrappers.lambdaQuery(TradeOrderEntity.class)
                .select(TradeOrderEntity::getCustomerId)
                .eq(TradeOrderEntity::getOutTradeNo, outTradeNo);
        TradeOrderEntity tradeOrderEntity = baseMapper.selectOne(wrapper);
        if (tradeOrderEntity == null) {
            throw new HisException("没有查询到订单流水号：" + outTradeNo);
        }
        Integer customerId = tradeOrderEntity.getCustomerId();
        if (customerId == null) {
            throw new HisException("订单流水号[" + outTradeNo + "]对应的客户ID为空");
        }
        return tradeOrderEntity.getCustomerId();
    }

    @Override
    public void clearRedisPayment(String outTradeNo) {
        if (outTradeNo == null || outTradeNo.trim().isEmpty()) {
            throw new HisException("订单流水号不能为空");
        }
        Integer customerId = getCustomerIdByOutTradeNo(outTradeNo);
        String key = CODE_URL_CACHE_PREFIX + customerId + "_" + outTradeNo;
        redisTemplate.delete(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean getPaymentResult(String outTradeNo) {
        String transactionId = paymentService.getPaymentResult(outTradeNo);
        if (transactionId == null) {
            throw new HisException("支付失败");
        }
        updatePayment(transactionId, outTradeNo);
        return true;
    }

    /**
     * 分页查询
     */
    public IPage<TradeOrderPageFrontVO> findPageByCondition(Integer customerId, OrderPageQueryDTO dto) {
        return selectPageByCondition(customerId, dto.getOrderStatus(), dto.getKeyword(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refund(Integer orderId) {
        String outRefundNo = selectOutRefundNoByOrderId(orderId);
        if (outRefundNo != null) {
            log.info("已经退过款了");
            return false;
        }
        int loginIdAsInt = StpCustomerUtil.getLoginIdAsInt();
        TradeOrderEntity tradeOrderEntity = baseMapper.selectTranIdAndAmountByOrderId(loginIdAsInt, orderId);
        String transactionId = tradeOrderEntity.getTransactionId();
        BigDecimal totalAmount = tradeOrderEntity.getTotalAmount();
        long cents = totalAmount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
        cents = 1L;
        Long refund = 1L;
        String notifyUrl = domain + "/front/order/refundCallback";
        try {
            outRefundNo = paymentService.refund(transactionId, refund, cents, notifyUrl);
        } catch (Exception e) {
            throw new HisException(e);
        }
        if (outRefundNo == null) {
            log.info("outRefundNo is null");
            return false;
        }
        int rows = updateOutRefundNo(orderId, outRefundNo);
        if (rows != 1) {
            throw new HisException("退款流程出现非唯一数据！");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyStatusByOutTradeNo(String outTradeNo) {
        return updateStatusByOutTradeNo(outTradeNo) == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyStatusByOrderId(Integer orderId) {
        return updateStatusByOrderId(orderId) == 1;
    }

    @Override
    public int removeByIdForMis(Integer id) {
        Wrapper<TradeOrderEntity> wrapper = Wrappers.lambdaQuery(TradeOrderEntity.class)
                .eq(TradeOrderEntity::getOrderId, id)
                .eq(TradeOrderEntity::getOrderStatus, 2);
        return baseMapper.delete(wrapper);
    }

    @Override
    public boolean hasOwnOrder(Map<String, Object> param) {
        return tradeOrderMapper.hasOwnOrder(param) != null;
    }

    @Override
    public IPage<OrderPageQueryMisVO> pageQueryByCondition(OrderPageQueryMisDTO dto) {
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        // 只有一个为空时，报错
        if ((startDate != null) ^ (endDate != null)) {
            throw new HisException("开始日期和结束日期必须同时填写");
        } else if (endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new HisException("开始日期必须在结束日期之前");
            }
        }
        Page<Object> page = Page.of(dto.getPageNum(), dto.getPageSize());
        return baseMapper.selectPageVO(page, dto);
    }

    // ========== 私有方法 ==========
    private IPage<TradeOrderPageFrontVO> selectPageByCondition(
            Integer customerId,
            String orderStatus,
            String keyword,
            Integer pageNum,
            Integer pageSize) {

        Page<TradeOrderPageFrontVO> page = new Page<>(pageNum, pageSize);

        MPJLambdaWrapper<TradeOrderEntity> wrapper = JoinWrappers.lambda("o", TradeOrderEntity.class);
        wrapper
                // 选择字段
                .select(TradeOrderEntity::getOrderId, TradeOrderEntity::getOutTradeNo, TradeOrderEntity::getGoodsId, TradeOrderEntity::getSnapshotId, TradeOrderEntity::getGoodsTitle,
                        TradeOrderEntity::getGoodsPrice, TradeOrderEntity::getQuantity, TradeOrderEntity::getTotalAmount, TradeOrderEntity::getGoodsImage, TradeOrderEntity::getGoodsDescription,
                        TradeOrderEntity::getOrderStatus)
                // 复杂 SQL 函数
                .select("IF(o.order_status = 1 AND TIMESTAMPDIFF(MINUTE,o.create_time,NOW()) > 20, true, false) AS disabled")
                .select("DATE_FORMAT(o.create_date,'%Y-%m-%d') AS createDate")
                .select("DATE_FORMAT(o.create_time,'%Y-%m-%d %H:%i:%s') AS createTime")
                // 关联表字段
                .selectCount(MedExamAppointmentEntity::getOrderId, "appointCount")
                // 左连接
                .leftJoin(MedExamAppointmentEntity.class, MedExamAppointmentEntity::getOrderId, TradeOrderEntity::getOrderId)
                // 基本条件
                .eq(TradeOrderEntity::getCustomerId, customerId)
                // 分组
                .groupBy(TradeOrderEntity::getOrderId)
                // 排序
                .orderByDesc(TradeOrderEntity::getOrderId);

        // 动态条件：订单状态
        if (orderStatus != null && !orderStatus.isBlank()) {
            wrapper.eq(TradeOrderEntity::getOrderStatus, orderStatus);
        }

        // 动态条件：关键词搜索
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w ->
                    w.eq(TradeOrderEntity::getOutTradeNo, keyword)
                            .or()
                            .like(TradeOrderEntity::getGoodsTitle, keyword)
            );
        }
        // 执行分页查询
        return tradeOrderMapper.selectJoinPage(page, TradeOrderPageFrontVO.class, wrapper);
    }

    private void checkDailyLimit(Integer customerId) {
        // 原方法 isCustomerReachedDailyLimit 的具体实现未知，这里保持逻辑
        if (isCustomerReachedDailyLimit(customerId)) {
            throw new HisException("您操作频繁，请稍后再试");
        }
    }

    private GoodsSnapshotEntity getGoodsSnapshotWithCache(Integer goodsId) {
        GoodsSnapshotEntity snapshot = medExamPackageService.findPackageAndPromotionById(goodsId);
        if (snapshot == null) {
            throw new HisException("商品不存在");
        }
        // 如果不存在该商品快照，就创建快照记录（使用 MD5 去重）
        String _id = goodsSnapshotDao.hasGoodsSnapshot(snapshot.getMd5Hash());
        if (_id == null) {
            _id = goodsSnapshotDao.insert(snapshot);
        }
        snapshot.set_id(_id); // 确保快照 ID 被设置
        return snapshot;
    }

    private BigDecimal calculateFinalAmount(GoodsSnapshotEntity snapshot, Integer buyCount) {
        BigDecimal originalPrice = snapshot.getCurrentPrice();
        String ruleContent = snapshot.getRuleContent();

        if (ruleContent == null || ruleContent.isBlank()) {
            // 无促销规则：单价 * 数量
            return originalPrice.multiply(BigDecimal.valueOf(buyCount));
        }

        // 有促销规则：使用规则引擎计算
        try {
            DefaultContext<String, Object> context = new DefaultContext<>();
            context.put("price", originalPrice.toString());
            //            context.put("price", originalPrice.intValue());
            context.put("number", buyCount);
            Object result = expressRunner.execute(ruleContent, context, null, true, false);
            return new BigDecimal(result.toString());
        } catch (Exception e) {
            log.error("规则引擎执行失败, ruleContent={}, price={}, number={}", ruleContent, originalPrice, buyCount, e);
            throw new HisException("价格计算失败", e);
        }
    }

    private WechatPayResult createWechatPayOrder(Integer customerId, BigDecimal amount) {
        String outTradeNo = IdUtil.simpleUUID().toUpperCase();
        // 金额单位转换为分
        long totalCents = amount.multiply(HUNDRED).longValue();
        String notifyUrl = domain + "/front/order/paymentCallback";
        // 订单过期时间：当前时间 + 20 分钟
        DateTime expireTime = DateUtil.offsetMinute(new Date(), ORDER_EXPIRE_MINUTES);
        String timeExpire = expireTime.toInstant().toString();

        // 调用微信支付统一下单接口
        ObjectNode jsonNodes = paymentService.unifiedOrder(outTradeNo, totalCents, WECHAT_PAY_DESC, notifyUrl, timeExpire);
        String codeUrl = jsonNodes.get("code_url").textValue();
        if (codeUrl == null || codeUrl.isEmpty()) {
            throw new HisException("获取微信支付二维码失败");
        }

        // 缓存 codeUrl 到 Redis，过期时间与订单过期时间一致
        String cacheKey = CODE_URL_CACHE_PREFIX + customerId + "_" + outTradeNo;
        redisTemplate.opsForValue().set(cacheKey, codeUrl, ORDER_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return new WechatPayResult(outTradeNo, codeUrl, expireTime);
    }

    private TradeOrderEntity saveOrder(GoodsSnapshotEntity snapshot, BigDecimal amount, WechatPayResult payResult,
                                       CreatePayDTO dto, Integer customerId) {
        TradeOrderEntity order = new TradeOrderEntity();
        order.setOutTradeNo(payResult.outTradeNo());
        order.setCustomerId(customerId);
        order.setGoodsId(dto.getGoodsId());
        order.setGoodsTitle(snapshot.getPackageName());
        order.setGoodsPrice(snapshot.getCurrentPrice());
        order.setGoodsImage(snapshot.getCoverImage());
        order.setGoodsDescription(snapshot.getDescription());
        order.setQuantity(dto.getBuyCount());
        order.setTotalAmount(amount);
        order.setOrderStatus(1); //未付款状态
        order.setSnapshotId(snapshot.get_id());         // 商品快照 ID

        int rows = tradeOrderMapper.insert(order);
        if (rows != 1) {
            throw new HisException("保存订单失败");
        }
        return order;
    }

    private void updateSalesVolume(Integer goodsId, Integer buyCount) {
        // 使用 setSql 实现原子自增
        LambdaUpdateWrapper<MedExamPackageEntity> wrapper = Wrappers.lambdaUpdate(MedExamPackageEntity.class)
                .setSql("sales_volume = sales_volume + " + buyCount)
                .eq(MedExamPackageEntity::getId, goodsId);
        int rows = medExamPackageMapper.update(null, wrapper);
        if (rows != 1) {
            throw new HisException("更新商品销量失败");
        }
    }

    private String generateQrCodeBase64(String content) {
        QrConfig qrConfig = new QrConfig();
        qrConfig.setWidth(QR_CODE_WIDTH);
        qrConfig.setHeight(QR_CODE_HEIGHT);
        qrConfig.setMargin(QR_CODE_MARGIN);
        return QrCodeUtil.generateAsBase64(content, qrConfig, QR_CODE_IMAGE_FORMAT);
    }

    private boolean isCustomerReachedDailyLimit(Integer customerId) {
        LambdaQueryWrapper<TradeOrderEntity> wrapper1 = Wrappers.lambdaQuery(TradeOrderEntity.class);
        wrapper1.eq(TradeOrderEntity::getCustomerId, customerId)
                .eq(TradeOrderEntity::getCreateDate, LocalDateTime.now().toLocalDate())  //今天创建的订单
                .lt(TradeOrderEntity::getOrderStatus, 3);  //状态1表示未付款，状态2表示已关闭
        LambdaQueryWrapper<TradeOrderEntity> wrapper2 = Wrappers.lambdaQuery(TradeOrderEntity.class);
        wrapper2.eq(TradeOrderEntity::getCustomerId, customerId)
                .eq(TradeOrderEntity::getRefundDate, LocalDateTime.now().toLocalDate()) //检查今天退款的订单
                .eq(TradeOrderEntity::getOrderStatus, 4); //状态值为4表示已退款
        long l1 = baseMapper.selectCount(wrapper1);
        long l2 = baseMapper.selectCount(wrapper2);
        return 10 <= l1 || 5 <= l2; // 已关闭或未付款超过10笔 或者 已退款超过5笔
    }

    // 内部类或独立类，用于封装微信支付结果
    private record WechatPayResult(String outTradeNo, String codeUrl, DateTime expireTime) {

    }

    private String selectOutRefundNoByOrderId(Integer orderId) {
        Wrapper<TradeOrderEntity> wrapper = Wrappers.lambdaQuery(TradeOrderEntity.class)
                .select(TradeOrderEntity::getOutRefundNo)
                .eq(TradeOrderEntity::getOrderId, orderId);
        TradeOrderEntity entity = baseMapper.selectOne(wrapper, true);
        return entity != null ? entity.getOutRefundNo() : null;
    }

    private TradeOrderEntity selectTranIdAndAmountByOrderId(Integer orderId) {
        int loginIdAsInt = StpCustomerUtil.getLoginIdAsInt();
        Wrapper<TradeOrderEntity> wrapper = Wrappers.lambdaQuery(TradeOrderEntity.class)
                .select(TradeOrderEntity::getTransactionId)
                .select(TradeOrderEntity::getTotalAmount)
                .eq(TradeOrderEntity::getOrderId, orderId)
                .eq(TradeOrderEntity::getOrderStatus, 3)
                .eq(TradeOrderEntity::getCustomerId, 6);//loginIdAsInt
        return baseMapper.selectOne(wrapper);
    }

    private int updateOutRefundNo(Integer orderId, String outRefundNo) {
        LambdaUpdateWrapper<TradeOrderEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TradeOrderEntity::getOrderId, orderId)
                .eq(TradeOrderEntity::getOrderStatus, 3)
                .set(TradeOrderEntity::getOutRefundNo, outRefundNo)
                .set(TradeOrderEntity::getRefundDate, LocalDate.now())
                .set(TradeOrderEntity::getRefundTime, LocalDateTime.now());
        return tradeOrderMapper.update(null, wrapper);
    }

    private int updateStatusByOutTradeNo(String outTradeNo) {
        Wrapper<TradeOrderEntity> wrapper = Wrappers.lambdaUpdate(TradeOrderEntity.class)
                .set(TradeOrderEntity::getOrderStatus, 4)
                .eq(TradeOrderEntity::getOutTradeNo, outTradeNo)
                .eq(TradeOrderEntity::getOrderStatus, 3);
        return baseMapper.update(null, wrapper);
    }

    private int updateStatusByOrderId(Integer orderId) {
        Wrapper<TradeOrderEntity> wrapper = Wrappers.lambdaUpdate(TradeOrderEntity.class)
                .set(TradeOrderEntity::getOrderStatus, 2)
                .eq(TradeOrderEntity::getOrderId, orderId);
        return baseMapper.update(null, wrapper);
    }
}


