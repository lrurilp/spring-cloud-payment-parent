package com.goal.payment.param.model;

import lombok.Data;

/**
 * 订单金额
 *
 * @author My.Peng
 */
@Data
public class AmountModel {
    /**
     * 总金额	total	int     是   	订单总金额，单位为分。
     * 示例值：100
     */
    private Long total;
    /**
     * 货币类型	currency	string[1,16]	否	CNY：人民币，境内商户号仅支持人民币。
     * 示例值：CNY
     */
    private String currency;

    public AmountModel() {}

    public AmountModel(Long total) {
        this.total = total;
    }

    public AmountModel(Long total, String currency) {
        this.total = total;
        this.currency = currency;
    }

}
