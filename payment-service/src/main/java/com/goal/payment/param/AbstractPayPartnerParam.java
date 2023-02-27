package com.goal.payment.param;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author My.Peng
 */
public abstract class AbstractPayPartnerParam {
    private static final long serialVersionUID = -4225155005606589070L;

    /**
     * 应用ID
     */
    @JSONField(name = "appid")
    private String appId;
    /**
     * 直连商户号
     */
    @JSONField(name = "mchid")
    private String mchId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }
}
