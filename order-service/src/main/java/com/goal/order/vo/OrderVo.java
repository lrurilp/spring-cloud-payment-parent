package com.goal.order.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author Mr.Peng
 */
@Data
@Builder
public class OrderVo {
    private String codeUrl;
    private String orderNo;
}
