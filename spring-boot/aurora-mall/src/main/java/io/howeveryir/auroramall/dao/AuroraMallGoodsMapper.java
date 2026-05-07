/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package io.howeveryir.auroramall.dao;

import io.howeveryir.auroramall.entity.AuroraMallGoods;
import io.howeveryir.auroramall.entity.StockNumDTO;
import io.howeveryir.auroramall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuroraMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(AuroraMallGoods record);

    int insertSelective(AuroraMallGoods record);

    AuroraMallGoods selectByPrimaryKey(Long goodsId);

    AuroraMallGoods selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    int updateByPrimaryKeySelective(AuroraMallGoods record);

    int updateByPrimaryKeyWithBLOBs(AuroraMallGoods record);

    int updateByPrimaryKey(AuroraMallGoods record);

    List<AuroraMallGoods> findAuroraMallGoodsList(PageQueryUtil pageUtil);

    int getTotalAuroraMallGoods(PageQueryUtil pageUtil);

    List<AuroraMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<AuroraMallGoods> findAuroraMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalAuroraMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("auroraMallGoodsList") List<AuroraMallGoods> auroraMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int recoverStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}