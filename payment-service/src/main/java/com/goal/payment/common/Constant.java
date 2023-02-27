package com.goal.payment.common;

/**
 * @author Mr.Peng
 */
public interface Constant {

    /**
     * Native下单地址
     */
    String WECHAT_NATIVE_PAY = "/v3/pay/transactions/native";

    /**
     * 订单查询
     */
    String ORDER_QUERY_BY_NO = "/v3/pay/transactions/out-trade-no/%s";

    /**
     * 关闭订单
     */
    String CLOSE_ORDER_BY_NO = "/v3/pay/transactions/out-trade-no/%s/close";

    /**
     * 申请退款
     */
    String DOMESTIC_REFUNDS = "/v3/refund/domestic/refunds";

    /**
     * 支付成功通知
     */
    String NATIVE_NOTIFY = "/payment/notify/wechat/pay";

    /**
     * 退款成功通知
     */
    String REFUND_NOTIFY = "/payment/notify/wechat/refunds";

}
