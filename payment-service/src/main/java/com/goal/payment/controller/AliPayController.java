package com.goal.payment.controller;


import com.goal.common.core.response.Result;
import com.goal.payment.param.ParNativeOrderParam;
import com.goal.payment.service.AliPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/pay/ali")
public class AliPayController {

    @Resource
    private AliPayService aliPayService;

    /**
     * 创建支付
     */
    @PostMapping("/trade")
    public Result<String> aliPay(@RequestBody ParNativeOrderParam orderParam) {
        return Result.ok(aliPayService.createTrade(orderParam));
    }

    /**
     * 关闭订单
     */
    @PostMapping("/closeTrade/{orderNo}")
    public Result<String> closeTrade(@PathVariable String orderNo) {
        return Result.toResult(aliPayService.closeTrade(orderNo));
    }

    /**
     * 退款
     */
    @PostMapping("/refund")
    public Result<Boolean> refundAli(@RequestBody ParNativeOrderParam orderParam) {
        return Result.ok(aliPayService.refund(orderParam));
    }

    /**
     * 退款查询
     */
    @GetMapping("/refundQuery/{orderNo}")
    public Result<String> queryRefund(@PathVariable String orderNo) {
        return Result.ok(aliPayService.queryRefund(orderNo));
    }
}
