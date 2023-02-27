package com.goal.common.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author My.Peng
 */
@Configuration
public class RabbitmqConfig {

    /**
     * 交换机名称
     */
    public static final String DELAY_EXCHANGE_NAME = "plugin.delay.exchange";

    //消息队列名称 消费者
    /**
     * 支付成功队列
     */
    public static final String DELAY_QUEUE_ORDER_SUCCESS_NAME = "plugin.delay.success.order.queue";
    /**
     * 订单超时/取消处理队列
     */
    public static final String DELAY_QUEUE_ORDER_NAME = "plugin.delay.order.queue";

    /**
     * 退款处理队列
     */
    public static final String DELAY_QUEUE_REFUND_NAME = "plugin.delay.refund.queue";

    //路由名称 生产者
    /**
     * 支付成功队列
     */
    public static final String ROUTING_KEY_ORDER_SUCCESS = "plugin.delay.success.routing_order";
    /**
     * 订单超时/取消处理队列
     */
    public static final String ROUTING_KEY_ORDER = "plugin.delay.routing_order";
    /**
     * 退款路由名称
     */
    public static final String ROUTING_KEY_REFUND = "plugin.delay.routing_refund";


    /**
     * 声明一个交换机
     *
     * @return
     */
    @Bean("DELAY_EXCHANGE")
    CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    /**
     * 声明一个支付成功延迟队列
     */
    @Bean("DELAY_QUEUE_ORDER_SUCCESS_NAME")
    Queue orderSuccessDelayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE_ORDER_SUCCESS_NAME).build();
    }

    /**
     * 声明一个订单延迟队列
     *
     * @return
     */
    @Bean("ORDER_DELAY_QUEUE")
    Queue orderDelayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE_ORDER_NAME).build();
    }

    /**
     * 声明一个退款延迟队列
     *
     * @return
     */
    @Bean("REFUND_DELAY_QUEUE")
    Queue refundDelayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE_REFUND_NAME).build();
    }

    /**
     * 订单支付成功延迟队列绑定
     *
     * @param orderSuccessDelayQueue
     * @param delayExchange
     * @return
     */
    @Bean
    Binding orderSuccessDelayQueueBinding(@Qualifier("DELAY_QUEUE_ORDER_SUCCESS_NAME") Queue orderSuccessDelayQueue, @Qualifier("DELAY_EXCHANGE") CustomExchange delayExchange) {
        return BindingBuilder.bind(orderSuccessDelayQueue).to(delayExchange).with(ROUTING_KEY_ORDER_SUCCESS).noargs();
    }

    /**
     * 订单超时/取消延迟队列绑定
     *
     * @param orderDelayQueue
     * @param delayExchange
     * @return
     */
    @Bean
    Binding orderDelayQueueBinding(@Qualifier("ORDER_DELAY_QUEUE") Queue orderDelayQueue, @Qualifier("DELAY_EXCHANGE") CustomExchange delayExchange) {
        return BindingBuilder.bind(orderDelayQueue).to(delayExchange).with(ROUTING_KEY_ORDER).noargs();
    }

    /**
     * 订单退款延迟队列绑定
     *
     * @param refundDelayQueue
     * @param delayExchange
     * @return
     */
    @Bean
    Binding refundDelayQueueBinding(@Qualifier("REFUND_DELAY_QUEUE") Queue refundDelayQueue, @Qualifier("DELAY_EXCHANGE") CustomExchange delayExchange) {
        return BindingBuilder.bind(refundDelayQueue).to(delayExchange).with(ROUTING_KEY_REFUND).noargs();
    }
}
