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
import io.howeveryir.auroramall.common.ServiceResultEnum;
import io.howeveryir.auroramall.controller.vo.AuroraMallShoppingCartItemVO;
import io.howeveryir.auroramall.controller.vo.AuroraMallUserVO;
import io.howeveryir.auroramall.entity.AuroraMallShoppingCartItem;
import io.howeveryir.auroramall.service.AuroraMallShoppingCartService;
import io.howeveryir.auroramall.util.Result;
import io.howeveryir.auroramall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private AuroraMallShoppingCartService auroraMallShoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<AuroraMallShoppingCartItemVO> myShoppingCartItems = auroraMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(AuroraMallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                AuroraMallException.fail("购物项不能为空");
            }
            //总价
            for (AuroraMallShoppingCartItemVO auroraMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += auroraMallShoppingCartItemVO.getGoodsCount() * auroraMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                AuroraMallException.fail("购物项价格异常");
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveAuroraMallShoppingCartItem(@RequestBody AuroraMallShoppingCartItem auroraMallShoppingCartItem,
                                                 HttpSession httpSession) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        auroraMallShoppingCartItem.setUserId(user.getUserId());
        String saveResult = auroraMallShoppingCartService.saveAuroraMallCartItem(auroraMallShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateAuroraMallShoppingCartItem(@RequestBody AuroraMallShoppingCartItem auroraMallShoppingCartItem,
                                                   HttpSession httpSession) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        auroraMallShoppingCartItem.setUserId(user.getUserId());
        String updateResult = auroraMallShoppingCartService.updateAuroraMallCartItem(auroraMallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{auroraMallShoppingCartItemId}")
    @ResponseBody
    public Result updateAuroraMallShoppingCartItem(@PathVariable("auroraMallShoppingCartItemId") Long auroraMallShoppingCartItemId,
                                                   HttpSession httpSession) {
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = auroraMallShoppingCartService.deleteById(auroraMallShoppingCartItemId,user.getUserId());
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        AuroraMallUserVO user = (AuroraMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<AuroraMallShoppingCartItemVO> myShoppingCartItems = auroraMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (AuroraMallShoppingCartItemVO auroraMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += auroraMallShoppingCartItemVO.getGoodsCount() * auroraMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                AuroraMallException.fail("购物项价格异常");
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}
