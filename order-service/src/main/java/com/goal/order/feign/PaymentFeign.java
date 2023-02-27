package com.goal.order.feign;

import com.goal.common.core.response.Result;
import com.goal.payment.param.ParNativeOrderParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Mr.Peng
 */
@FeignClient("payment-service")
public interface PaymentFeign {
    @PostMapping("/pay/wechat/nativePay")
    Result<String> nativePay(@RequestBody ParNativeOrderParam orderParam);

    @PostMapping("/pay/wechat/refund")
    Result<Boolean> refunds(@RequestBody ParNativeOrderParam orderParam);

    @PostMapping("/pay/ali/trade")
    Result<String> aliPay(@RequestBody ParNativeOrderParam orderParam);

    @PostMapping("/pay/ali/refund")
    Result<Boolean> refundAli(ParNativeOrderParam orderParam);
}
