package com.goal.order.controller;

import com.goal.common.core.response.Result;
import com.goal.order.entiy.Order;
import com.goal.order.service.OrderService;
import com.goal.order.vo.OrderVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mr.Peng
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * WeChat Pay
     *
     * @param order
     * @return
     */
    @ResponseBody
    @PostMapping("/wechat")
    public Result<OrderVo> wechatPay(Order order) {
        return Result.ok(orderService.createWechatOrder(order));
    }

    /**
     * WeChat Refund
     *
     * @param refundNo
     * @return
     */
    @ResponseBody
    @PostMapping("/wechat/refund/{refundNo}")
    public Result<Boolean> wechatRefund(@PathVariable String refundNo) {
        return Result.ok(orderService.wechatRefund(refundNo));
    }

    /**
     * Ali Pay
     *
     * @param order
     * @return
     */
    @GetMapping("/ali")
    public void aliPay(Order order, HttpServletResponse response) throws IOException {
        String trade = orderService.createAliOrder(order);
        response.setHeader("Accept", "text/html");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(trade);
    }

    /**
     * Ali Refund
     *
     * @param refundNo
     * @return
     */
    @ResponseBody
    @PostMapping("/ali/refund/{refundNo}")
    public Result<Boolean> aliRefund(@PathVariable String refundNo) {
        return Result.ok(orderService.aliRefund(refundNo));
    }
}
