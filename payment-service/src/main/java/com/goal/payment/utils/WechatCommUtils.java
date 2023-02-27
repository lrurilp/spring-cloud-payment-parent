package com.goal.payment.utils;

import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.util.Asserts;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author My.Peng
 */
public class WechatCommUtils {


    /**
     * 加载商户私钥pem文件
     *
     * @param privateKeyPath
     * @return
     * @throws IOException
     */
    public static PrivateKey loadMerchantPrivateKeyFile(String privateKeyPath) {
        try {
            Asserts.notEmpty(privateKeyPath, "密钥文件路径为空");
            return PemUtil.loadPrivateKey(new ByteArrayInputStream(Files.readAllBytes(Paths.get(privateKeyPath))));
        } catch (Throwable e) {
            throw new IllegalStateException("密钥错误", e);
        }
    }

    /**
     * 加载商户私钥
     *
     * @param privateKeyStr
     * @return
     * @throws IOException
     */
    public static PrivateKey loadMerchantPrivateKey(String privateKeyStr) {
        try {
            Asserts.notEmpty(privateKeyStr, "密钥不能为空，privateKeyPath");
            privateKeyStr = privateKeyStr
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr)));
        } catch (Throwable e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static PrivateKey loadCertificatePath(String certificatePath) {
        try {
            Asserts.notEmpty(certificatePath, "证书文件路径为空");
            return PemUtil.loadPrivateKey(new ByteArrayInputStream(Files.readAllBytes(Paths.get(certificatePath))));
        } catch (Throwable e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * 构造验签名串
     *
     * @param timestamp 微信请求头：Wechatpay-Timestamp
     * @param nonce     微信请求头：Wechatpay-Nonce
     * @param body      微信请求体：body
     * @return 签名字符串
     */
    public static String responseSign(String timestamp, String nonce, String body) {
        return Stream.of(timestamp, nonce, body)
                .collect(Collectors.joining("\n", "", "\n"));
    }

    public static String toParamString(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length() > 0) {
                sb.append(entry.getKey()).append("=").append(entry.getValue())
                        .append("&");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
