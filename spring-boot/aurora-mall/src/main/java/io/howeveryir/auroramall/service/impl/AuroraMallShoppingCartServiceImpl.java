/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package io.howeveryir.auroramall.service.impl;

import io.howeveryir.auroramall.common.Constants;
import io.howeveryir.auroramall.common.ServiceResultEnum;
import io.howeveryir.auroramall.controller.vo.AuroraMallShoppingCartItemVO;
import io.howeveryir.auroramall.dao.AuroraMallGoodsMapper;
import io.howeveryir.auroramall.dao.AuroraMallShoppingCartItemMapper;
import io.howeveryir.auroramall.entity.AuroraMallGoods;
import io.howeveryir.auroramall.entity.AuroraMallShoppingCartItem;
import io.howeveryir.auroramall.service.AuroraMallShoppingCartService;
import io.howeveryir.auroramall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AuroraMallShoppingCartServiceImpl implements AuroraMallShoppingCartService {

    @Autowired
    private AuroraMallShoppingCartItemMapper auroraMallShoppingCartItemMapper;

    @Autowired
    private AuroraMallGoodsMapper auroraMallGoodsMapper;

    @Override
    public String saveAuroraMallCartItem(AuroraMallShoppingCartItem auroraMallShoppingCartItem) {
        AuroraMallShoppingCartItem temp = auroraMallShoppingCartItemMapper.selectByUserIdAndGoodsId(auroraMallShoppingCartItem.getUserId(), auroraMallShoppingCartItem.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            temp.setGoodsCount(auroraMallShoppingCartItem.getGoodsCount());
            return updateAuroraMallCartItem(temp);
        }
        AuroraMallGoods auroraMallGoods = auroraMallGoodsMapper.selectByPrimaryKey(auroraMallShoppingCartItem.getGoodsId());
        //商品为空
        if (auroraMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = auroraMallShoppingCartItemMapper.selectCountByUserId(auroraMallShoppingCartItem.getUserId()) + 1;
        //超出单个商品的最大数量
        if (auroraMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (auroraMallShoppingCartItemMapper.insertSelective(auroraMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateAuroraMallCartItem(AuroraMallShoppingCartItem auroraMallShoppingCartItem) {
        AuroraMallShoppingCartItem auroraMallShoppingCartItemUpdate = auroraMallShoppingCartItemMapper.selectByPrimaryKey(auroraMallShoppingCartItem.getCartItemId());
        if (auroraMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (auroraMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //当前登录账号的userId与待修改的cartItem中userId不同，返回错误
        if (!auroraMallShoppingCartItemUpdate.getUserId().equals(auroraMallShoppingCartItem.getUserId())) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        //数值相同，则不执行数据操作
        if (auroraMallShoppingCartItem.getGoodsCount().equals(auroraMallShoppingCartItemUpdate.getGoodsCount())) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        auroraMallShoppingCartItemUpdate.setGoodsCount(auroraMallShoppingCartItem.getGoodsCount());
        auroraMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (auroraMallShoppingCartItemMapper.updateByPrimaryKeySelective(auroraMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public AuroraMallShoppingCartItem getAuroraMallCartItemById(Long auroraMallShoppingCartItemId) {
        return auroraMallShoppingCartItemMapper.selectByPrimaryKey(auroraMallShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId, Long userId) {
        AuroraMallShoppingCartItem auroraMallShoppingCartItem = auroraMallShoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
        if (auroraMallShoppingCartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(auroraMallShoppingCartItem.getUserId())) {
            return false;
        }
        return auroraMallShoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<AuroraMallShoppingCartItemVO> getMyShoppingCartItems(Long auroraMallUserId) {
        List<AuroraMallShoppingCartItemVO> auroraMallShoppingCartItemVOS = new ArrayList<>();
        List<AuroraMallShoppingCartItem> auroraMallShoppingCartItems = auroraMallShoppingCartItemMapper.selectByUserId(auroraMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(auroraMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> auroraMallGoodsIds = auroraMallShoppingCartItems.stream().map(AuroraMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<AuroraMallGoods> auroraMallGoods = auroraMallGoodsMapper.selectByPrimaryKeys(auroraMallGoodsIds);
            Map<Long, AuroraMallGoods> auroraMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(auroraMallGoods)) {
                auroraMallGoodsMap = auroraMallGoods.stream().collect(Collectors.toMap(AuroraMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (AuroraMallShoppingCartItem auroraMallShoppingCartItem : auroraMallShoppingCartItems) {
                AuroraMallShoppingCartItemVO auroraMallShoppingCartItemVO = new AuroraMallShoppingCartItemVO();
                BeanUtil.copyProperties(auroraMallShoppingCartItem, auroraMallShoppingCartItemVO);
                if (auroraMallGoodsMap.containsKey(auroraMallShoppingCartItem.getGoodsId())) {
                    AuroraMallGoods auroraMallGoodsTemp = auroraMallGoodsMap.get(auroraMallShoppingCartItem.getGoodsId());
                    auroraMallShoppingCartItemVO.setGoodsCoverImg(auroraMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = auroraMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    auroraMallShoppingCartItemVO.setGoodsName(goodsName);
                    auroraMallShoppingCartItemVO.setSellingPrice(auroraMallGoodsTemp.getSellingPrice());
                    auroraMallShoppingCartItemVOS.add(auroraMallShoppingCartItemVO);
                }
            }
        }
        return auroraMallShoppingCartItemVOS;
    }
}
