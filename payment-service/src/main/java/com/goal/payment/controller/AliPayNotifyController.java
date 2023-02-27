package com.goal.payment.controller;


import com.alipay.api.AlipayApiException;
import com.goal.common.rabbitmq.RabbitmqDelayProducer;
import com.goal.common.rabbitmq.config.RabbitmqConfig;
import com.goal.payment.config.AliPayConfig;
import com.goal.payment.service.AliPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/notify/ali")
public class AliPayNotifyController {

    @Resource
    private AliPayConfig aliPayConfig;

    @Resource
    private AliPayService aliPayService;

    @Resource
    private RabbitmqDelayProducer rabbitmqDelayProducer;

    @PostMapping("/trade")
    public String tradeNotify(@RequestParam Map<String, String> params) throws AlipayApiException {
        boolean signVerified = aliPayService.aliSignature(params);
        if (!signVerified) {
            log.info("付款验签失败");
        }
        String tradeNo = params.get("out_trade_no");
        log.info("付款验签成功。订单编号：{}进入延迟队列", tradeNo);
        rabbitmqDelayProducer.publish(tradeNo, tradeNo,
                RabbitmqConfig.DELAY_EXCHANGE_NAME, RabbitmqConfig.ROUTING_KEY_ORDER_SUCCESS, 100);
        return "redirect:" + aliPayConfig.returnUrl;
    }
}
