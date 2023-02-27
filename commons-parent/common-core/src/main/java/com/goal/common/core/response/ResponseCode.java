package com.goal.common.core.response;

import lombok.Getter;

/**
 * @author My.Lp
 */
@Getter
public enum ResponseCode {
    /**
     * 请求成功
     */
    SUCCESS(200, "Ok"),
    /**
     * 业务内部错误   （2开头）
     */
    UESR_OR_PWD_ERROR(2001, "用户名或密码错误"),

    CLIENT_AUTHENTICATION_FAILED(2002, "客户端认证失败"),

    TOKEN_INVALID_OR_EXPIRED(2003, "Token无效或已过期"),

    TOKEN_ACCESS_FORBIDDEN(2004, "Token已被禁止访问"),

    TICKET_INVALID_OR_EXPIRED(2005, "Ticket无效"),


    DATA_EXIST(2101, "数据已存在"),
    /**
     * 客户端异常   （4开头）
     * 如：参数检验失败、用户信息获取异常
     */
    FAILED(4000, "Client Exception"),
    /**
     * 服务端错误   （5开头）
     */
    EXCEPTION(5000, "Something Exception"),
    /**
     * 其他错误
     */
    ERROR(9000, "Other Error");


    private final int code;
    private final String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
