package com.goal.payment.service.impl;


import com.goal.common.rabbitmq.RabbitmqDelayProducer;
import com.goal.common.rabbitmq.config.RabbitmqConfig;
import com.goal.payment.common.Constant;
import com.goal.payment.config.WechatPayConfig;
import com.goal.payment.param.ParNativeOrderParam;
import com.goal.payment.service.WechatPayService;
import com.goal.payment.utils.JsonUtil;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * 微信支付的服务实现
 *
 * @author My.Peng
 */
@Slf4j
@Service("wechatPayService")
public class WechatPayServiceImpl implements WechatPayService {

    @Resource
    private CloseableHttpClient wechatPayClient;

    @Resource
    private WechatPayConfig wechatPayConfig;

    @Resource
    private RabbitmqDelayProducer rabbitmqDelayProducer;


    /**
     * 微信支付
     *
     * @return code_url
     */
    @Override
    @SneakyThrows
    public String nativePay(ParNativeOrderParam orderParam) {
        String url = wechatPayConfig.getDomain().concat(Constant.WECHAT_NATIVE_PAY);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");

        String param = new JSONObject()
                .put("appid", wechatPayConfig.getAppid())
                .put("mchid", wechatPayConfig.getMchId())
                .put("description", orderParam.getSubject())
                .put("out_trade_no", orderParam.getOrderNo())
                .put("notify_url", wechatPayConfig.getNotifyDomain().concat(Constant.NATIVE_NOTIFY))
                .put("amount", new JSONObject()
                        .put("total", orderParam.getTotalAmount())
                        .put("currency", "CNY"))
                .toString();

        httpPost.setEntity(new StringEntity(param, "utf-8"));

        CloseableHttpResponse response = wechatPayClient.execute(httpPost);

        String bodyString = EntityUtils.toString(response.getEntity());

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode == 200 || statusCode == 204) {
            log.info("订单{}请求二维码成功", orderParam.getOrderNo());

            rabbitmqDelayProducer.publish(orderParam.getOrderNo(),
                    orderParam.getOrderNo(), RabbitmqConfig.DELAY_EXCHANGE_NAME,
                    RabbitmqConfig.ROUTING_KEY_ORDER, 1000 * 60 * 2);
            //2分钟
        } else {
            log.info("订单{}请求二维码失败!响应码为{};返回的结果{}", orderParam.getOrderNo(), statusCode, bodyString);
        }
        response.close();
        return JsonUtil.jsonToMapString(bodyString).get("code_url");
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     */
    @Override
    @SneakyThrows
    public boolean cancelOrder(String orderNo) {
        log.info("接受订单{}取消通知", orderNo);
        String url = wechatPayConfig.getDomain()
                .concat(String.format(Constant.CLOSE_ORDER_BY_NO, orderNo));

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");

        String param = new JSONObject()
                .put("mchid", wechatPayConfig.getMchId())
                .toString();

        httpPost.setEntity(new StringEntity(param, "utf-8"));

        CloseableHttpResponse response = wechatPayClient.execute(httpPost);

        //获取相应信息
        int statusCode = response.getStatusLine().getStatusCode();
        response.close();
        if (statusCode == 200 || statusCode == 204) {
            log.info("订单{}取消成功", orderNo);
            log.info("取消编号：{}进入延迟队列", orderNo);
            // 将取消编号存入取消/过期延迟队列，延迟时间为0.1秒
            rabbitmqDelayProducer.publish(orderNo, orderNo,
                    RabbitmqConfig.DELAY_EXCHANGE_NAME,
                    RabbitmqConfig.ROUTING_KEY_ORDER, 100);
            return true;
        } else {
            log.info("取消订单{}失败, 状态码为{}", orderNo, statusCode);

            return false;
        }

    }

    /**
     * 申请退款
     *
     * @param orderParam 订单
     */
    @Override
    @SneakyThrows
    public boolean refund(ParNativeOrderParam orderParam) {
        String url = wechatPayConfig.getDomain().concat(Constant.DOMESTIC_REFUNDS);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");

        String param = new JSONObject()
                .put("out_trade_no", orderParam.getOrderNo())
                .put("out_refund_no", orderParam.getOrderNo())
                .put("notify_url", wechatPayConfig.getNotifyDomain().concat(Constant.REFUND_NOTIFY))
                .put("amount", new JSONObject()
                        .put("refund", orderParam.getTotalAmount())
                        .put("total", orderParam.getTotalAmount())
                        .put("currency", "CNY"))
                .toString();

        httpPost.setEntity(new StringEntity(param, "utf-8"));

        //发起退款请求,对请求的内容进行签名验证
        CloseableHttpResponse response = wechatPayClient.execute(httpPost);
        //获取相应信息
        int statusCode = response.getStatusLine().getStatusCode();
        response.close();
        if (statusCode == 200 || statusCode == 204) {
            log.info("订单{}退款成功", orderParam.getOrderNo());
            return true;
        } else {
            log.info("退款订单{}失败, 状态码为{}", orderParam.getOrderNo(), statusCode);
            return false;
        }
    }

    /**
     * 解密验签数据 | AES方式
     *
     * @param map 数据
     * @return 解密数据
     */
    @Override
    public Map<String, Object> processOrderData(Map<String, Object> map) {
        // 获取通知中的resource这部分加密数据
        Map<String, String> resourceMap = (Map<String, String>) map.get("resource");
        // 获取密文
        String ciphertext = resourceMap.get("ciphertext");
        // 随机串获取
        String nonce = resourceMap.get("nonce");
        // 获取附加数据
        String associatedData = resourceMap.get("associated_data");
        // 使用API V3秘钥进行解密
        AesUtil aesUtil = new AesUtil(wechatPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        String decryptData = null;
        try {
            decryptData = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8), nonce.getBytes(StandardCharsets.UTF_8), ciphertext);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return JsonUtil.jsonToMap(decryptData);
    }


}
