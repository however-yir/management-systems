/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package io.howeveryir.auroramall.dao;

import io.howeveryir.auroramall.entity.AuroraMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuroraMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(AuroraMallShoppingCartItem record);

    int insertSelective(AuroraMallShoppingCartItem record);

    AuroraMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    AuroraMallShoppingCartItem selectByUserIdAndGoodsId(@Param("auroraMallUserId") Long auroraMallUserId, @Param("goodsId") Long goodsId);

    List<AuroraMallShoppingCartItem> selectByUserId(@Param("auroraMallUserId") Long auroraMallUserId, @Param("number") int number);

    int selectCountByUserId(Long auroraMallUserId);

    int updateByPrimaryKeySelective(AuroraMallShoppingCartItem record);

    int updateByPrimaryKey(AuroraMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}