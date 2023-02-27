package com.goal.payment.param;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * Native下单-请求参数
 * 请求URL：https://api.mch.weixin.qq.com/v3/pay/partner/transactions/native
 * 请求方式：POST
 *
 * @author fxli
 * @date 2022/10/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class ParNativeOrderParam extends AbstractPayPartnerParam implements Serializable {
    /**
     * 商品描述
     */
    private String subject;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 商品金额
     */
    private Double totalAmount;

}
