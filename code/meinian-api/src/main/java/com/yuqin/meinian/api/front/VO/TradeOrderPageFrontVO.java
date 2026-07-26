package com.yuqin.meinian.api.front.VO;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TradeOrderPageFrontVO implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 快照ID
     */
    private String snapshotId;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品价格
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal goodsPrice;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 总金额
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal totalAmount;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 商品描述
     */
    private String goodsDescription;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 是否可取消（超时20分钟）
     */
    private Boolean disabled;

    /**
     * 创建日期
     */
    private LocalDate createDate;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 预约数量
     */
    private Long appointCount;
}