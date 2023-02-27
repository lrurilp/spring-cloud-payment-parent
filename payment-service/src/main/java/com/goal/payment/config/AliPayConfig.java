package com.goal.payment.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
public class AliPayConfig {

    /**
     * appId
     */
    @Value("${ali-pay.app-id}")
    public String appId;

    /**
     * 商户私钥
     */
    @Value("${ali-pay.merchant-private-key}")
    public String merchantPrivateKey;


    /**
     * 支付宝的公钥
     */
    @Value("${ali-pay.alipay-public-key}")
    public String alipayPublicKey;


    /**
     * 异步通知的地址
     */
    @Value("${ali-pay.notify-url}")
    public String notifyUrl;

    /**
     * 同步跳转的页面
     */
    @Value("${ali-pay.return-url}")
    public String returnUrl;

    /**
     * 签名方式
     */
    @Value("${ali-pay.sign-type}")
    public String signType;

    /**
     * 字符的编码
     */
    @Value("${ali-pay.charset}")
    public String charset;

    /**
     * 网关地址
     */
    @Value("${ali-pay.gateway-url}")
    public String gatewayUrl;

    /**
     * 定义一个方法返回AlipayConfig对象
     */
    @Bean
    public AliPayConfig getAlipayConfig() {
        AliPayConfig alipayConfig = new AliPayConfig();
        alipayConfig.setAppId(appId);
        alipayConfig.setAppId(merchantPrivateKey);
        alipayConfig.setAppId(alipayPublicKey);
        alipayConfig.setAppId(notifyUrl);
        alipayConfig.setAppId(returnUrl);
        alipayConfig.setAppId(signType);
        alipayConfig.setAppId(charset);
        alipayConfig.setAppId(gatewayUrl);
        return alipayConfig;
    }
}
