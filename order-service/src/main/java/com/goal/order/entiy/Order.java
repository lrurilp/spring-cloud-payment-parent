package com.goal.order.entiy;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author My.Peng
 */
@Data
@TableName("order_info")
public class Order implements Serializable {
    private static final long serialVersionUID = -27774237662750056L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;
    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 商品id
     */
    @TableField("product_id")
    private String productId;
    /**
     * 商品描述
     */
    @TableField("subject")
    private String subject;
    /**
     * 支付方式
     */
    @TableField("payment_type")
    private String paymentType;
    /**
     * 商品金额
     */
    @TableField("total_amount")
    private Double totalAmount;
    /**
     * 二维码地址
     */
    @TableField("code_url")
    private String codeUrl;
    /**
     * 订单状态
     */
    @TableField("order_status")
    private String orderStatus;
    /**
     * 购买数量
     */
    @TableField("quantity")
    private Integer quantity;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
