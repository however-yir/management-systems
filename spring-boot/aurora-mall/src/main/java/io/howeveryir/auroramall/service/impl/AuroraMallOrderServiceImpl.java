/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package io.howeveryir.auroramall.service.impl;

import io.howeveryir.auroramall.common.*;
import io.howeveryir.auroramall.controller.vo.*;
import io.howeveryir.auroramall.dao.AuroraMallGoodsMapper;
import io.howeveryir.auroramall.dao.AuroraMallOrderItemMapper;
import io.howeveryir.auroramall.dao.AuroraMallOrderMapper;
import io.howeveryir.auroramall.dao.AuroraMallShoppingCartItemMapper;
import io.howeveryir.auroramall.entity.AuroraMallGoods;
import io.howeveryir.auroramall.entity.AuroraMallOrder;
import io.howeveryir.auroramall.entity.AuroraMallOrderItem;
import io.howeveryir.auroramall.entity.StockNumDTO;
import io.howeveryir.auroramall.service.AuroraMallOrderService;
import io.howeveryir.auroramall.util.BeanUtil;
import io.howeveryir.auroramall.util.NumberUtil;
import io.howeveryir.auroramall.util.PageQueryUtil;
import io.howeveryir.auroramall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class AuroraMallOrderServiceImpl implements AuroraMallOrderService {

    @Autowired
    private AuroraMallOrderMapper auroraMallOrderMapper;
    @Autowired
    private AuroraMallOrderItemMapper auroraMallOrderItemMapper;
    @Autowired
    private AuroraMallShoppingCartItemMapper auroraMallShoppingCartItemMapper;
    @Autowired
    private AuroraMallGoodsMapper auroraMallGoodsMapper;

    @Override
    public PageResult getAuroraMallOrdersPage(PageQueryUtil pageUtil) {
        List<AuroraMallOrder> auroraMallOrders = auroraMallOrderMapper.findAuroraMallOrderList(pageUtil);
        int total = auroraMallOrderMapper.getTotalAuroraMallOrders(pageUtil);
        PageResult pageResult = new PageResult(auroraMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(AuroraMallOrder auroraMallOrder) {
        AuroraMallOrder temp = auroraMallOrderMapper.selectByPrimaryKey(auroraMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(auroraMallOrder.getTotalPrice());
            temp.setUserAddress(auroraMallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (auroraMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<AuroraMallOrder> orders = auroraMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (AuroraMallOrder auroraMallOrder : orders) {
                if (auroraMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += auroraMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (auroraMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += auroraMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (auroraMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<AuroraMallOrder> orders = auroraMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (AuroraMallOrder auroraMallOrder : orders) {
                if (auroraMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += auroraMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (auroraMallOrder.getOrderStatus() != 1 && auroraMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += auroraMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (auroraMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<AuroraMallOrder> orders = auroraMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (AuroraMallOrder auroraMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (auroraMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += auroraMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (auroraMallOrder.getOrderStatus() == 4 || auroraMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += auroraMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间&&恢复库存
                if (auroraMallOrderMapper.closeOrder(Arrays.asList(ids), AuroraMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0 && recoverStockNum(Arrays.asList(ids))) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(AuroraMallUserVO user, List<AuroraMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(AuroraMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(AuroraMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<AuroraMallGoods> auroraMallGoods = auroraMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<AuroraMallGoods> goodsListNotSelling = auroraMallGoods.stream()
                .filter(auroraMallGoodsTemp -> auroraMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            AuroraMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, AuroraMallGoods> auroraMallGoodsMap = auroraMallGoods.stream().collect(Collectors.toMap(AuroraMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (AuroraMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!auroraMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                AuroraMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > auroraMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                AuroraMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(auroraMallGoods)) {
            if (auroraMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = auroraMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    AuroraMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                AuroraMallOrder auroraMallOrder = new AuroraMallOrder();
                auroraMallOrder.setOrderNo(orderNo);
                auroraMallOrder.setUserId(user.getUserId());
                auroraMallOrder.setUserAddress(user.getAddress());
                //总价
                for (AuroraMallShoppingCartItemVO auroraMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += auroraMallShoppingCartItemVO.getGoodsCount() * auroraMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    AuroraMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                auroraMallOrder.setTotalPrice(priceTotal);
                //订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                auroraMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (auroraMallOrderMapper.insertSelective(auroraMallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<AuroraMallOrderItem> auroraMallOrderItems = new ArrayList<>();
                    for (AuroraMallShoppingCartItemVO auroraMallShoppingCartItemVO : myShoppingCartItems) {
                        AuroraMallOrderItem auroraMallOrderItem = new AuroraMallOrderItem();
                        //使用BeanUtil工具类将auroraMallShoppingCartItemVO中的属性复制到auroraMallOrderItem对象中
                        BeanUtil.copyProperties(auroraMallShoppingCartItemVO, auroraMallOrderItem);
                        //AuroraMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        auroraMallOrderItem.setOrderId(auroraMallOrder.getOrderId());
                        auroraMallOrderItems.add(auroraMallOrderItem);
                    }
                    //保存至数据库
                    if (auroraMallOrderItemMapper.insertBatch(auroraMallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    AuroraMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                AuroraMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            AuroraMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        AuroraMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public AuroraMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        AuroraMallOrder auroraMallOrder = auroraMallOrderMapper.selectByOrderNo(orderNo);
        if (auroraMallOrder == null) {
            AuroraMallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        //验证是否是当前userId下的订单，否则报错
        if (!userId.equals(auroraMallOrder.getUserId())) {
            AuroraMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        List<AuroraMallOrderItem> orderItems = auroraMallOrderItemMapper.selectByOrderId(auroraMallOrder.getOrderId());
        //获取订单项数据
        if (CollectionUtils.isEmpty(orderItems)) {
            AuroraMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<AuroraMallOrderItemVO> auroraMallOrderItemVOS = BeanUtil.copyList(orderItems, AuroraMallOrderItemVO.class);
        AuroraMallOrderDetailVO auroraMallOrderDetailVO = new AuroraMallOrderDetailVO();
        BeanUtil.copyProperties(auroraMallOrder, auroraMallOrderDetailVO);
        auroraMallOrderDetailVO.setOrderStatusString(AuroraMallOrderStatusEnum.getAuroraMallOrderStatusEnumByStatus(auroraMallOrderDetailVO.getOrderStatus()).getName());
        auroraMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(auroraMallOrderDetailVO.getPayType()).getName());
        auroraMallOrderDetailVO.setAuroraMallOrderItemVOS(auroraMallOrderItemVOS);
        return auroraMallOrderDetailVO;
    }

    @Override
    public AuroraMallOrder getAuroraMallOrderByOrderNo(String orderNo) {
        return auroraMallOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = auroraMallOrderMapper.getTotalAuroraMallOrders(pageUtil);
        List<AuroraMallOrder> auroraMallOrders = auroraMallOrderMapper.findAuroraMallOrderList(pageUtil);
        List<AuroraMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(auroraMallOrders, AuroraMallOrderListVO.class);
            //设置订单状态中文显示值
            for (AuroraMallOrderListVO auroraMallOrderListVO : orderListVOS) {
                auroraMallOrderListVO.setOrderStatusString(AuroraMallOrderStatusEnum.getAuroraMallOrderStatusEnumByStatus(auroraMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = auroraMallOrders.stream().map(AuroraMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<AuroraMallOrderItem> orderItems = auroraMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<AuroraMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(AuroraMallOrderItem::getOrderId));
                for (AuroraMallOrderListVO auroraMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(auroraMallOrderListVO.getOrderId())) {
                        List<AuroraMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(auroraMallOrderListVO.getOrderId());
                        //将AuroraMallOrderItem对象列表转换成AuroraMallOrderItemVO对象列表
                        List<AuroraMallOrderItemVO> auroraMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, AuroraMallOrderItemVO.class);
                        auroraMallOrderListVO.setAuroraMallOrderItemVOS(auroraMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String cancelOrder(String orderNo, Long userId) {
        AuroraMallOrder auroraMallOrder = auroraMallOrderMapper.selectByOrderNo(orderNo);
        if (auroraMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(auroraMallOrder.getUserId())) {
                AuroraMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //订单状态判断
            if (auroraMallOrder.getOrderStatus().intValue() == AuroraMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || auroraMallOrder.getOrderStatus().intValue() == AuroraMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || auroraMallOrder.getOrderStatus().intValue() == AuroraMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || auroraMallOrder.getOrderStatus().intValue() == AuroraMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            //修改订单状态&&恢复库存
            if (auroraMallOrderMapper.closeOrder(Collections.singletonList(auroraMallOrder.getOrderId()), AuroraMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0 && recoverStockNum(Collections.singletonList(auroraMallOrder.getOrderId()))) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        AuroraMallOrder auroraMallOrder = auroraMallOrderMapper.selectByOrderNo(orderNo);
        if (auroraMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(auroraMallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //订单状态判断 非出库状态下不进行修改操作
            if (auroraMallOrder.getOrderStatus().intValue() != AuroraMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            auroraMallOrder.setOrderStatus((byte) AuroraMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            auroraMallOrder.setUpdateTime(new Date());
            if (auroraMallOrderMapper.updateByPrimaryKeySelective(auroraMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        AuroraMallOrder auroraMallOrder = auroraMallOrderMapper.selectByOrderNo(orderNo);
        if (auroraMallOrder != null) {
            //订单状态判断 非待支付状态下不进行修改操作
            if (auroraMallOrder.getOrderStatus().intValue() != AuroraMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            auroraMallOrder.setOrderStatus((byte) AuroraMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            auroraMallOrder.setPayType((byte) payType);
            auroraMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            auroraMallOrder.setPayTime(new Date());
            auroraMallOrder.setUpdateTime(new Date());
            if (auroraMallOrderMapper.updateByPrimaryKeySelective(auroraMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<AuroraMallOrderItemVO> getOrderItems(Long id) {
        AuroraMallOrder auroraMallOrder = auroraMallOrderMapper.selectByPrimaryKey(id);
        if (auroraMallOrder != null) {
            List<AuroraMallOrderItem> orderItems = auroraMallOrderItemMapper.selectByOrderId(auroraMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<AuroraMallOrderItemVO> auroraMallOrderItemVOS = BeanUtil.copyList(orderItems, AuroraMallOrderItemVO.class);
                return auroraMallOrderItemVOS;
            }
        }
        return null;
    }

    /**
     * 恢复库存
     * @param orderIds
     * @return
     */
    public Boolean recoverStockNum(List<Long> orderIds) {
        //查询对应的订单项
        List<AuroraMallOrderItem> auroraMallOrderItems = auroraMallOrderItemMapper.selectByOrderIds(orderIds);
        //获取对应的商品id和商品数量并赋值到StockNumDTO对象中
        List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(auroraMallOrderItems, StockNumDTO.class);
        //执行恢复库存的操作
        int updateStockNumResult = auroraMallGoodsMapper.recoverStockNum(stockNumDTOS);
        if (updateStockNumResult < 1) {
            AuroraMallException.fail(ServiceResultEnum.CLOSE_ORDER_ERROR.getResult());
            return false;
        } else {
            return true;
        }
    }
}