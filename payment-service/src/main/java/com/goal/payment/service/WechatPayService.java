package com.goal.payment.service;

import com.goal.payment.param.ParNativeOrderParam;

import java.util.Map;

public interface WechatPayService {

    /**
     * native支付
     * @param orderParam
     * @return
     */
    String nativePay(ParNativeOrderParam orderParam);

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @return 是否成功
     */
    boolean cancelOrder(String orderNo);

    /**
     * 申请退款
     *
     * @param orderParam 订单
     * @return 是否成功
     */
    boolean refund(ParNativeOrderParam orderParam);

    /**
     * 解密验签数据 | AES方式
     *
     * @param map 数据
     * @return 解密数据
     */
    Map<String, Object> processOrderData(Map<String, Object> map);
}
