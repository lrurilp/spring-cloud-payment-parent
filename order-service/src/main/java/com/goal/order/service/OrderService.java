package com.goal.order.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.order.entiy.Order;
import com.goal.order.vo.OrderVo;


public interface OrderService extends IService<Order> {


    /**
     * WechatPay
     *
     * @param order
     * @return
     */
    OrderVo createWechatOrder(Order order);

    /**
     * WechatRefund
     *
     * @param refundNo 退款编号
     * @return
     */
    boolean wechatRefund(String refundNo);

    /**
     * AliPay
     *
     * @param order
     * @return
     */
    String createAliOrder(Order order);

    /**
     * aliRefund
     *
     * @param refundNo 退款编号
     * @return
     */
    boolean aliRefund(String refundNo);

    /**
     * 支付成功
     *
     * @param tradeNo 订单编号
     */
    void queryOrderSuccessStatus(String tradeNo);

    /**
     * 关闭订单
     *
     * @param tradeNo 订单编号
     */
    void checkOrderOffStatus(String tradeNo);

    /**
     * 退款
     *
     * @param refundNo 退款编号
     */
    void checkRefundStatus(String refundNo);

    /**
     * 根据订单编号查询订单
     *
     * @param tradeNo 订单编号
     * @return 订单
     */
    Order getByTradeNo(String tradeNo);


    /**
     * 更新订单状态
     *
     * @param orderNo      订单编号
     * @param tradeSuccess 状态
     */
    void updateOrderStatus(String orderNo, String tradeSuccess);


}
