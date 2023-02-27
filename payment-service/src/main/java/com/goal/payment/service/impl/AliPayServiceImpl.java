package com.goal.payment.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.goal.common.rabbitmq.RabbitmqDelayProducer;
import com.goal.common.rabbitmq.config.RabbitmqConfig;
import com.goal.payment.config.AliPayConfig;
import com.goal.payment.param.ParNativeOrderParam;
import com.goal.payment.service.AliPayService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author My.Peng
 */
@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {

    @Resource
    private AliPayConfig aliPayConfig;


    @Resource
    private RabbitmqDelayProducer rabbitmqDelayProducer;

    public AlipayClient getClient() {
        return new DefaultAlipayClient(aliPayConfig.gatewayUrl, aliPayConfig.appId, aliPayConfig.merchantPrivateKey,
                "json", aliPayConfig.charset, aliPayConfig.alipayPublicKey, aliPayConfig.signType);
    }

    /**
     * 创建支付
     *
     * @return
     */
    @Override
    @SneakyThrows
    public String createTrade(ParNativeOrderParam orderParam) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //支付宝异步通知地址
        String notifyUrl = aliPayConfig.notifyUrl;
        //同步通知地址
        String returnUrl = aliPayConfig.returnUrl;
        //设置请求的异步通知路径
        request.setNotifyUrl(notifyUrl);
        //设置请求的同步通知路径
        request.setReturnUrl(returnUrl);

        String param = new JSONObject()
                .put("out_trade_no", orderParam.getOrderNo())
                .put("total_amount", orderParam.getTotalAmount())
                .put("subject", orderParam.getSubject())
                .put("product_code", "FAST_INSTANT_TRADE_PAY")
                .toString();

        request.setBizContent(String.valueOf(param));

        //重点: 调用远程的支付宝支付接口
        AlipayTradePagePayResponse response = getClient().pageExecute(request);

        rabbitmqDelayProducer.publish(orderParam.getOrderNo(),
                orderParam.getOrderNo(), RabbitmqConfig.DELAY_EXCHANGE_NAME,
                RabbitmqConfig.ROUTING_KEY_ORDER, 1000 * 60 * 2);

        return response.getBody();
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @return
     */
    @Override
    @SneakyThrows
    public boolean closeTrade(String orderNo) {
        System.out.println(orderNo);

        String param = new JSONObject()
                .put("out_trade_no", orderNo)
                .toString();

        // 创建一个请求对象
        AlipayTradeCloseRequest alipayTradeCloseRequest = new AlipayTradeCloseRequest();
        alipayTradeCloseRequest.setBizContent(param);

        // 发送请求关闭订单
        AlipayTradeCloseResponse response = getClient().execute(alipayTradeCloseRequest);
        if (response.isSuccess()) {
            log.info("ALI 取消订单成功，{}", response.getBody());
            rabbitmqDelayProducer.publish(orderNo, orderNo,
                    RabbitmqConfig.DELAY_EXCHANGE_NAME,
                    RabbitmqConfig.ROUTING_KEY_ORDER, 100);
        } else {
            log.info("ALI 取消订单失败，{}", response.getCode());
        }
        return response.isSuccess();
    }

    /**
     * 申请退款
     *
     * @param orderParam
     * @return
     */
    @Override
    @SneakyThrows
    public boolean refund(ParNativeOrderParam orderParam) {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

        String param = new JSONObject()
                .put("out_trade_no", orderParam.getOrderNo())
                .put("refund_amount", orderParam.getTotalAmount())
                .toString();
        request.setBizContent(param);
        AlipayTradeRefundResponse response = getClient().execute(request);
        if (response.isSuccess()) {
            log.info("退款成功,金额原路返回。{}", response.getBody());
            rabbitmqDelayProducer.publish(orderParam.getOrderNo(), orderParam.getOrderNo(),
                    RabbitmqConfig.DELAY_EXCHANGE_NAME, RabbitmqConfig.ROUTING_KEY_REFUND, 100);
        } else {
            log.info("退款失败" + response.getCode());
        }
        return response.isSuccess();
    }

    /**
     * 退款查询
     *
     * @param orderNo 订单编号
     * @return 响应
     */
    @Override
    @SneakyThrows
    public String queryRefund(String orderNo) {
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        String param = new JSONObject()
                .put("out_trade_no", orderNo)
                .put("refund_amount", orderNo)
                .toString();
        request.setBizContent(param);
        AlipayTradeFastpayRefundQueryResponse response = getClient().execute(request);
        if (response.isSuccess()) {
            log.info("查询成功" + response.getBody());
            return response.getBody();
        } else {
            log.info("查询失败" + response.getCode());
            return response.getCode();
        }
    }

    @Override
    public boolean aliSignature(Map<String, String> params) throws AlipayApiException {
        return AlipaySignature.rsaCheckV1(params, aliPayConfig.alipayPublicKey, aliPayConfig.charset, aliPayConfig.signType);
    }
}
