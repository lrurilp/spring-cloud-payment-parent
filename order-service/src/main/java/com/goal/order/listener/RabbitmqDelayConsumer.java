package com.goal.order.listener;

import com.goal.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author My.Peng
 */
@Component
@Slf4j
public class RabbitmqDelayConsumer {

    @Resource
    private OrderService orderService;

    /**
     * 监听订单支付成功延迟队列
     *
     * @param tradeNo 订单编号
     */
    @RabbitListener(queues = {"plugin.delay.success.order.queue"})
    public void orderDelaySuccessQueue(String tradeNo, Message message, Channel channel) throws Exception {
        log.info("订单支付成功队列 接收订单{}", tradeNo);
        try {
            //处理订单支付成功消息
            orderService.queryOrderSuccessStatus(tradeNo);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("订单{}处理成功", tradeNo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("订单{}处理失败。进行重新入队等待处理", tradeNo);
            //消息重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    /**
     * 监听订单取消/超时延迟队列
     *
     * @param orderNo 订单编号
     */
    @RabbitListener(queues = {"plugin.delay.order.queue"})
    public void orderDelayQueue(String orderNo, Message message, Channel channel) throws Exception {
        log.info("订单取消/超时队列 接收订单{}", orderNo);
        try {
            //处理订单取消/超时消息
            orderService.checkOrderOffStatus(orderNo);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("订单{}处理成功", orderNo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("订单{}处理失败。进行重新入队等待处理", orderNo);
            //消息重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    /**
     * 监听退款延迟队列
     *
     * @param refundNo 退款编号
     */
    @RabbitListener(queues = {"plugin.delay.refund.queue"})
    public void refundDelayQueue(String refundNo, Message message, Channel channel) throws Exception {
        log.info("订单退款队列 接收订单{}", refundNo);
        try {
            //处理订单退款信息
            orderService.checkRefundStatus(refundNo);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("订单{}退款成功", refundNo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("订单{}退款失败。进行重新入队等待退款", refundNo);
            //消息重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
