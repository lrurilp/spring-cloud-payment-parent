package com.goal.payment.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.goal.payment.param.ParNativeOrderParam;

import java.util.Map;

public interface AliPayService {
    /**
     * 获取客户端对象
     *
     * @return
     */
    AlipayClient getClient();

    /**
     * 创建支付
     *
     * @return
     */
    String createTrade(ParNativeOrderParam orderParam);

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @return
     */
    boolean closeTrade(String orderNo);

    /**
     * 申请退款
     *
     * @param orderParam
     * @return
     */
    boolean refund(ParNativeOrderParam orderParam);

    /**
     * 退款查询
     *
     * @param orderNo 订单编号
     * @return
     */
    String queryRefund(String orderNo) ;

    /**
     * 验证签名方法
     *
     * @param params
     * @return
     * @throws AlipayApiException
     */
    boolean aliSignature(Map<String, String> params) throws AlipayApiException;
}

