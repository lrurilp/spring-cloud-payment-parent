package com.goal.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.common.core.constant.Constant;
import com.goal.order.entiy.Order;
import com.goal.order.feign.PaymentFeign;
import com.goal.order.mapper.OrderMapper;
import com.goal.order.service.OrderService;
import com.goal.order.utils.StringUtils;
import com.goal.order.vo.OrderVo;
import com.goal.payment.param.ParNativeOrderParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author My.Peng
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private PaymentFeign paymentFeign;


    @Override
    public OrderVo createWechatOrder(Order order) {
        order.setOrderNo(StringUtils.createOrderNum());
        order.setPaymentType("WX");
        order.setOrderStatus(Constant.WAIT_BUYER_PAY);

        ParNativeOrderParam parNativeOrderParam = new ParNativeOrderParam();
        BeanUtils.copyProperties(order, parNativeOrderParam);

        String codeUrl = paymentFeign.nativePay(parNativeOrderParam).getData();
        order.setCodeUrl(codeUrl);

        orderMapper.insert(order);
        return OrderVo.builder().codeUrl(codeUrl)
                .orderNo(order.getOrderNo()).build();
    }

    @Override
    public boolean wechatRefund(String refundNo) {
        Order order = this.getByTradeNo(refundNo);
        ParNativeOrderParam parNativeOrderParam = new ParNativeOrderParam();
        BeanUtils.copyProperties(order, parNativeOrderParam);
        return paymentFeign.refunds(parNativeOrderParam).getData();
    }

    @Override
    public String createAliOrder(Order order) {
        order.setOrderNo(StringUtils.createOrderNum());
        order.setUserId("1");
        order.setProductId("1");
        order.setPaymentType("ALI");
        order.setSubject("支付宝测试");
        order.setOrderStatus(Constant.WAIT_BUYER_PAY);

        //调用mapper写入数据
        orderMapper.insert(order);
        ParNativeOrderParam parNativeOrderParam = new ParNativeOrderParam();
        BeanUtils.copyProperties(order, parNativeOrderParam);
        return paymentFeign.aliPay(parNativeOrderParam).getData();
    }

    @Override
    public boolean aliRefund(String refundNo) {
        Order order = this.getByTradeNo(refundNo);
        System.out.println(order.toString());
        ParNativeOrderParam parNativeOrderParam = new ParNativeOrderParam();
        BeanUtils.copyProperties(order, parNativeOrderParam);
        return paymentFeign.refundAli(parNativeOrderParam).getData();
    }

    @Override
    public void queryOrderSuccessStatus(String tradeNo) {
        // 根据订单编号查询订单
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, tradeNo);
        Order order = this.orderMapper.selectOne(wrapper);
        // 修改状态
        this.updateOrderStatus(tradeNo, Constant.TRADE_SUCCESS);
    }

    @Override
    public void checkOrderOffStatus(String tradeNo) {
        Order order = this.getByTradeNo(tradeNo);
        String orderStatus = order.getOrderStatus();
        if (orderStatus.equals(Constant.WAIT_BUYER_PAY)) {
            this.updateOrderStatus(tradeNo, Constant.CANCEL_SUCCESS);
        }
    }

    @Override
    public void checkRefundStatus(String refundNo) {
        this.updateOrderStatus(refundNo, Constant.REFUND_SUCCESS);
    }

    @Override
    public Order getByTradeNo(String outTradeNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, outTradeNo);
        return orderMapper.selectOne(wrapper);
    }

    @Override
    public void updateOrderStatus(String orderNo, String tradeSuccess) {
        UpdateWrapper<Order> wrapper = new UpdateWrapper<>();
        wrapper.eq("order_no", orderNo).set("order_status", tradeSuccess);
        orderMapper.update(null, wrapper);
    }


}
