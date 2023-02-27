package com.goal.payment.controller;


import com.goal.common.core.response.Result;
import com.goal.payment.param.ParNativeOrderParam;
import com.goal.payment.service.WechatPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 微信的支付控制器
 *
 * @author Administrator
 */
@RequestMapping("/pay/wechat")
@RestController
@Slf4j
public class WechatPayController {

    @Resource
    private WechatPayService wechatPayService;


    /**
     * native下单
     */
    @PostMapping("/nativePay")
    public Result<String> nativePay(@RequestBody ParNativeOrderParam orderParam) {
        return Result.ok(wechatPayService.nativePay(orderParam));
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancelOrder/{orderNo}")
    public Result<Boolean> cancel(@PathVariable String orderNo) {
        return Result.toResult(wechatPayService.cancelOrder(orderNo));
    }

    /**
     * 退款服务
     */
    @PostMapping("/refund")
    public Result<Boolean> refunds(@RequestBody ParNativeOrderParam orderParam) {
        return Result.ok(wechatPayService.refund(orderParam));
    }


}
