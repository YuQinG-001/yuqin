package com.yuqin.meinian.api.mis.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单分页查询返回VO
 *
 * @description 对应OrderPageQueryMisDTO的分页查询结果
 */
@Data
public class OrderPageQueryMisVO {

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品单价
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal goodsPrice;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 订单总金额
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal totalAmount;

    /**
     * 订单状态(1:未付款,2:已关闭,3:已付款,4:已退款,5:已预约,6:已完成)
     */
    private Integer orderStatus;

    /**
     * 创建时间 (格式: yyyy-MM-dd HH:mm)
     */
    private LocalDateTime createTime;

    /**
     * 创建日期
     */
    private LocalDate createDate;

    /**
     * 退款时间 (格式: yyyy-MM-dd HH:mm)
     */
    private LocalDateTime refundTime;

    /**
     * 退款日期
     */
    private LocalDate refundDate;

    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 客户照片URL
     */
    private String photoUrl;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 注册时间 (格式: yyyy-MM-dd)
     */
    private LocalDateTime registerTime;

    /**
     * 订单流水号
     */
    private String outTradeNo;

    /**
     * 付款单id
     */
    private String transactionId;

    /**
     * 退款单流水号
     */
    private String outRefundNo;

    /**
     * 商品快照ID
     */
    private String snapshotId;

    /**
     * 预约数量 (同一订单的预约记录数)
     */
    private Integer num;
}
