package com.yuqin.meinian.api.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 交易订单表
 * @TableName trade_order
 */
@TableName(value ="trade_order")
@Data
public class TradeOrderEntity implements Serializable {
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Integer orderId;

    /**
     * 订单流水号
     */
    private String outTradeNo;

    /**
     * 客户ID
     */
    private Integer customerId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品单价
     */
    private BigDecimal goodsPrice;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 商品描述
     */
    private String goodsDescription;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 付款单id
     */
    private String transactionId;

    /**
     * 订单状态(1:未付款,2:已关闭,3:已付款,4:已退款,5:已预约,6:已完成)
     */
    private Integer orderStatus;

    /**
     * 商品快照ID
     */
    private String snapshotId;

    /**
     * 退款单流水号
     */
    private String outRefundNo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建日期
     */
    private LocalDate createDate;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款日期
     */
    private LocalDate refundDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TradeOrderEntity other = (TradeOrderEntity) that;
        return (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getOutTradeNo() == null ? other.getOutTradeNo() == null : this.getOutTradeNo().equals(other.getOutTradeNo()))
            && (this.getCustomerId() == null ? other.getCustomerId() == null : this.getCustomerId().equals(other.getCustomerId()))
            && (this.getGoodsId() == null ? other.getGoodsId() == null : this.getGoodsId().equals(other.getGoodsId()))
            && (this.getGoodsTitle() == null ? other.getGoodsTitle() == null : this.getGoodsTitle().equals(other.getGoodsTitle()))
            && (this.getGoodsPrice() == null ? other.getGoodsPrice() == null : this.getGoodsPrice().equals(other.getGoodsPrice()))
            && (this.getGoodsImage() == null ? other.getGoodsImage() == null : this.getGoodsImage().equals(other.getGoodsImage()))
            && (this.getGoodsDescription() == null ? other.getGoodsDescription() == null : this.getGoodsDescription().equals(other.getGoodsDescription()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getTotalAmount() == null ? other.getTotalAmount() == null : this.getTotalAmount().equals(other.getTotalAmount()))
            && (this.getTransactionId() == null ? other.getTransactionId() == null : this.getTransactionId().equals(other.getTransactionId()))
            && (this.getOrderStatus() == null ? other.getOrderStatus() == null : this.getOrderStatus().equals(other.getOrderStatus()))
            && (this.getSnapshotId() == null ? other.getSnapshotId() == null : this.getSnapshotId().equals(other.getSnapshotId()))
            && (this.getOutRefundNo() == null ? other.getOutRefundNo() == null : this.getOutRefundNo().equals(other.getOutRefundNo()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
            && (this.getRefundTime() == null ? other.getRefundTime() == null : this.getRefundTime().equals(other.getRefundTime()))
            && (this.getRefundDate() == null ? other.getRefundDate() == null : this.getRefundDate().equals(other.getRefundDate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getOutTradeNo() == null) ? 0 : getOutTradeNo().hashCode());
        result = prime * result + ((getCustomerId() == null) ? 0 : getCustomerId().hashCode());
        result = prime * result + ((getGoodsId() == null) ? 0 : getGoodsId().hashCode());
        result = prime * result + ((getGoodsTitle() == null) ? 0 : getGoodsTitle().hashCode());
        result = prime * result + ((getGoodsPrice() == null) ? 0 : getGoodsPrice().hashCode());
        result = prime * result + ((getGoodsImage() == null) ? 0 : getGoodsImage().hashCode());
        result = prime * result + ((getGoodsDescription() == null) ? 0 : getGoodsDescription().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getTotalAmount() == null) ? 0 : getTotalAmount().hashCode());
        result = prime * result + ((getTransactionId() == null) ? 0 : getTransactionId().hashCode());
        result = prime * result + ((getOrderStatus() == null) ? 0 : getOrderStatus().hashCode());
        result = prime * result + ((getSnapshotId() == null) ? 0 : getSnapshotId().hashCode());
        result = prime * result + ((getOutRefundNo() == null) ? 0 : getOutRefundNo().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getRefundTime() == null) ? 0 : getRefundTime().hashCode());
        result = prime * result + ((getRefundDate() == null) ? 0 : getRefundDate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", orderId=").append(orderId);
        sb.append(", outTradeNo=").append(outTradeNo);
        sb.append(", customerId=").append(customerId);
        sb.append(", goodsId=").append(goodsId);
        sb.append(", goodsTitle=").append(goodsTitle);
        sb.append(", goodsPrice=").append(goodsPrice);
        sb.append(", goodsImage=").append(goodsImage);
        sb.append(", goodsDescription=").append(goodsDescription);
        sb.append(", quantity=").append(quantity);
        sb.append(", totalAmount=").append(totalAmount);
        sb.append(", transactionId=").append(transactionId);
        sb.append(", orderStatus=").append(orderStatus);
        sb.append(", snapshotId=").append(snapshotId);
        sb.append(", outRefundNo=").append(outRefundNo);
        sb.append(", createTime=").append(createTime);
        sb.append(", createDate=").append(createDate);
        sb.append(", refundTime=").append(refundTime);
        sb.append(", refundDate=").append(refundDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}