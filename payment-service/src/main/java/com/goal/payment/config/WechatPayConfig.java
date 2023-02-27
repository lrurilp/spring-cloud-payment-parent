package com.goal.payment.config;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.ScheduledUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;


/**
 * @author My.Peng
 */
@Data
@Configuration
public class WechatPayConfig {

    /**
     * 商户号
     */
    @Value("${wechat-pay.mch-id}")
    public String mchId;

    /**
     * 商户API证书序列号
     */
    @Value("${wechat-pay.mch-serial-no}")
    public String mchSerialNo;

    /**
     * 商户的私钥文件
     */
    @Value("${wechat-pay.private-key-path}")
    public String privateKeyPath;

    /**
     * API V3秘钥
     */
    @Value("${wechat-pay.api-v3-key}")
    public String apiV3Key;

    /**
     * 微信支付服务器地址
     */
    @Value("${wechat-pay.domain}")
    public String domain;

    /**
     * 接收结果的通知地址
     */
    @Value("${wechat-pay.notify-domain}")
    public String notifyDomain;

    /**
     * APPID
     */
    @Value("${wechat-pay.appid}")
    public String appid;


    /**
     * 获取签名的验证器
     * 签名验证器会帮我们进行验签操作
     */
    @Bean
    @SneakyThrows
    public ScheduledUpdateCertificatesVerifier getVerifier() {
        // 获取商户的私钥
        PrivateKey privateKey = PemUtil.loadPrivateKey(Files.newInputStream(Paths.get(privateKeyPath)));
        // 获取私钥签名的对象 (签名)
        PrivateKeySigner privateKeySigner = new PrivateKeySigner(mchSerialNo, privateKey);
        // 身份认证对象 (验签)
        WechatPay2Credentials wechatPay2Credentials = new WechatPay2Credentials(mchId, privateKeySigner);
        // 使用定时更新签名验证器
        return new ScheduledUpdateCertificatesVerifier(wechatPay2Credentials, apiV3Key.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 获取httpClient对象: 建立远程链接的基础,我们通过SDK创建该对象
     * 带验证的方式
     */
    @Bean(name = "wechatPayClient")
    @SneakyThrows
    public CloseableHttpClient getWxPayClient(ScheduledUpdateCertificatesVerifier verifier) {
        //获取商户的私钥
        PrivateKey privateKey = PemUtil.loadPrivateKey(Files.newInputStream(Paths.get(privateKeyPath)));
        //构建HttpClient
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create().withMerchant(mchId, mchSerialNo, privateKey).withValidator(new WechatPay2Validator(verifier));
        //使用builder对象构建httpClient
        return builder.build();
    }


    /**
     * 获取httpClient对象: 建立远程链接的基础,我们通过SDK创建该对象
     * 无需验证方式
     */
    @Bean(name = "wechatPayNoSignClient")
    @SneakyThrows
    public CloseableHttpClient getWxPaySignClient() {
        //获取商户的私钥
        PrivateKey privateKey = PemUtil.loadPrivateKey(Files.newInputStream(Paths.get(privateKeyPath)));
        //构建HttpClient
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create().withMerchant(mchId, mchSerialNo, privateKey).withValidator((response) -> true);
        //使用builder对象构建httpClient
        return builder.build();
    }

}
