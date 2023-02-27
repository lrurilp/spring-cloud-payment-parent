package com.goal.payment.controller;


import com.goal.common.core.response.Result;
import com.goal.common.rabbitmq.RabbitmqDelayProducer;
import com.goal.common.rabbitmq.config.RabbitmqConfig;
import com.goal.payment.service.WechatPayService;
import com.goal.payment.utils.HttpUtils;
import com.goal.payment.utils.JsonUtil;
import com.goal.payment.utils.WechatPay2ValidatorForRequest;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author Mr.Peng
 */
@Slf4j
@RestController
@RequestMapping("/notify/wechat")
public class WechatPayNotifyController {

    @Resource
    private WechatPayService wechatPayService;

    @Resource
    private Verifier verifier;

    @Resource
    private RabbitmqDelayProducer rabbitmqDelayProducer;

    /**
     * 接收微信付款成功回调方法
     *
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/pay")
    public Result<String> nativeNotify(HttpServletRequest request) throws IOException {
        String data = HttpUtils.getRequestBody(request);
        Map<String, Object> bodyMap = JsonUtil.jsonToMap(data);
        // 进行签名验证
        WechatPay2ValidatorForRequest validator = new WechatPay2ValidatorForRequest(verifier, data, (String) bodyMap.get("id"));
        if (!validator.validate(request)) {
            log.info("付款验签失败，验签数据：{}", bodyMap);
            return Result.error("微信验签失败");
        }
        // 解密验签数据
        Map<String, Object> plaintMap = wechatPayService.processOrderData(bodyMap);
        String tradeNo = (String) plaintMap.get("out_trade_no");
        log.info("付款验签成功。订单编号：{}进入延迟队列", tradeNo);
        rabbitmqDelayProducer.publish(tradeNo, tradeNo,
                RabbitmqConfig.DELAY_EXCHANGE_NAME, RabbitmqConfig.ROUTING_KEY_ORDER_SUCCESS, 100);
        return Result.ok();
    }

    /**
     * 退款通知
     *
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/refunds")
    public Result<String> refundsNotify(HttpServletRequest request) throws IOException {
        String data = HttpUtils.getRequestBody(request);
        Map<String, Object> dataMap = JsonUtil.jsonToMap(data);
        // 签名校验
        String requestId = (String) dataMap.get("id");
        WechatPay2ValidatorForRequest wechatPay2ValidatorForRequest = new WechatPay2ValidatorForRequest(verifier, data, requestId);
        if (!wechatPay2ValidatorForRequest.validate(request)) {
            log.info("退款验签失败，验签数据：{}", dataMap);
            return Result.error("微信验签失败");
        }
        // 解密验签数据
        Map<String, Object> plaintTextMap = wechatPayService.processOrderData(dataMap);
        // 获取明文中的订单号
        String tradeNo = (String) plaintTextMap.get("out_trade_no");
        log.info("退款验签成功，订单号：{}进入延迟队列", tradeNo);
        rabbitmqDelayProducer.publish(tradeNo,tradeNo,
                RabbitmqConfig.DELAY_EXCHANGE_NAME, RabbitmqConfig.ROUTING_KEY_REFUND, 100);
        return Result.ok();
    }
}
