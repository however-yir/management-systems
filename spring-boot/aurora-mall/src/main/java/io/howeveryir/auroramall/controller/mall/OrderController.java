/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package io.howeveryir.auroramall.controller.mall;

import io.howeveryir.auroramall.common.Constants;
import io.howeveryir.auroramall.common.AuroraMallException;
import io.howeveryir.auroramall.common.AuroraMallOrderStatusEnum;
import io.howeveryir.auroramall.common.ServiceResultEnum;
import io.howeveryir.auroramall.controller.vo.AuroraMallOrderDetailVO;
import io.howeveryir.auroramall.controller.vo.AuroraMallShoppingCartItemVO;
import io.howeveryir.auroramall.controller.vo.AuroraMallUserVO;
import io.howeveryir.auroramall.entity.AuroraMallOrder;
import io.howeveryir.auroramall.service.AuroraMallOrderService;
import io.howeveryir.auroramall.service.AuroraMallShoppingCartService;
import io.howeveryir.auroramall.util.PageQueryUtil;
import io.howeveryir.auroramall.util.Result;
import io.howeveryir.auroramall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Resource
    private AuroraMallShoppingCartService auroraMallShoppingCartService;
    @Resource
    private AuroraMallOrderService auroraMallOrderService;

    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        AuroraMallOrderDetailVO orderDetailVO = auroraMallOrderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (ObjectUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", auroraMallOrderService.getMyOrders(pageUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }

    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<AuroraMallShoppingCartItemVO> myShoppingCartItems = auroraMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!StringUtils.hasText(user.getAddress().trim())) {
            //无收货地址
            AuroraMallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物车中无数据则跳转至错误页
            AuroraMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = auroraMallOrderService.saveOrder(user, myShoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/" + saveOrderResult;
    }

    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = auroraMallOrderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = auroraMallOrderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        AuroraMallOrder auroraMallOrder = auroraMallOrderService.getAuroraMallOrderByOrderNo(orderNo);
        //判断订单userId
        if (!user.getUserId().equals(auroraMallOrder.getUserId())) {
            AuroraMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        //判断订单状态
        if (auroraMallOrder.getOrderStatus().intValue() != AuroraMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            AuroraMallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        }
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", auroraMallOrder.getTotalPrice());
        return "mall/pay-select";
    }

    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        AuroraMallOrder auroraMallOrder = auroraMallOrderService.getAuroraMallOrderByOrderNo(orderNo);
        //判断订单userId
        if (!user.getUserId().equals(auroraMallOrder.getUserId())) {
            AuroraMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        //判断订单状态
        if (auroraMallOrder.getOrderStatus().intValue() != AuroraMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            AuroraMallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        }
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", auroraMallOrder.getTotalPrice());
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }

    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = auroraMallOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

}
