package com.goal.common.core.response;

import java.io.Serializable;


/**
 * @author My.Peng
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 100000000000000L;

    /**
     * 响应体code编码
     */
    private Integer code;

    /**
     * 响应体消息
     */
    private String msg;

    /**
     * 响应体数据
     */
    private T data;

    /**
     * 响应体时间戳
     */
    private long timestamp = System.currentTimeMillis();

    public Result() {
        super();
    }


    /**
     * 成功
     *
     * @param data 数据
     */
    private Result(T data) {
        this.code = ResponseCode.SUCCESS.getCode();
        this.msg = ResponseCode.SUCCESS.getMsg();
        this.data = data;

    }

    /**
     * @param code 状态码
     * @param msg  信息
     */
    private Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     */
    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    /**
     * 成功
     *
     * @return 成功对象
     */
    public static <T> Result<T> ok() {
        return new Result<T>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg());
    }

    /**
     * 设置成功数据
     *
     * @param data 数据
     */
    public static <T> Result<T> ok(T data) {
        return new Result<T>(data);
    }

    /**
     * 设置成功状态码和信息
     *
     * @param responseCode 枚举对象
     * @return 枚举对象
     */
    public static <T> Result<T> ok(ResponseCode responseCode) {
        return new Result<T>(responseCode.getCode(), responseCode.getMsg());
    }

    /**
     * 设置成功状态码、信息和数据
     *
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     */
    public static <T> Result<T> ok(Integer code, String msg, T data) {
        return new Result<T>(code, msg, data);
    }


    /**
     * 失败
     */
    public static <T> Result<T> error() {
        return new Result<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMsg());
    }

    /**
     * 设置失败信息
     *
     * @param msg 信息
     */
    public static <T> Result<T> error(String msg) {
        return new Result<T>(ResponseCode.ERROR.getCode(), msg);
    }

    /**
     * 设置失败状态码和信息
     *
     * @param responseCode 枚举对象
     * @return 枚举对象
     */
    public static <T> Result<T> error(ResponseCode responseCode) {
        return new Result<T>(responseCode.getCode(), responseCode.getMsg());
    }

    /**
     * 设置失败状态码、信息和数据
     *
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     */
    public static <T> Result<T> error(Integer code, String msg, T data) {
        return new Result<T>(code, msg, data);
    }


    /**
     * 判断是否成功
     *
     * @param flag boolean值
     * @return
     */
    public static <T> Result<T> toResult(boolean flag) {
        return result(flag, null);
    }

    /**
     * 判断是否成功
     *
     * @param flag boolean值
     * @param data 数据
     * @return
     */
    public static <T> Result<T> toResult(boolean flag, T data) {
        return result(flag, data);
    }


    private static <T> Result<T> result(boolean flag, T data) {
        return new Result<T>() {{
            setCode(flag ? ResponseCode.SUCCESS.getCode() : ResponseCode.ERROR.getCode());
            setMsg(flag ? ResponseCode.SUCCESS.getMsg() : ResponseCode.ERROR.getMsg());
            setData(data);
        }};
    }


    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
