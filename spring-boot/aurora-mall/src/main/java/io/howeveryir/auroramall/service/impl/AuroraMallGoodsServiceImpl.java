/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package io.howeveryir.auroramall.service.impl;

import io.howeveryir.auroramall.common.AuroraMallCategoryLevelEnum;
import io.howeveryir.auroramall.common.AuroraMallException;
import io.howeveryir.auroramall.common.ServiceResultEnum;
import io.howeveryir.auroramall.controller.vo.AuroraMallSearchGoodsVO;
import io.howeveryir.auroramall.dao.GoodsCategoryMapper;
import io.howeveryir.auroramall.dao.AuroraMallGoodsMapper;
import io.howeveryir.auroramall.entity.GoodsCategory;
import io.howeveryir.auroramall.entity.AuroraMallGoods;
import io.howeveryir.auroramall.service.AuroraMallGoodsService;
import io.howeveryir.auroramall.util.BeanUtil;
import io.howeveryir.auroramall.util.AuroraMallUtils;
import io.howeveryir.auroramall.util.PageQueryUtil;
import io.howeveryir.auroramall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AuroraMallGoodsServiceImpl implements AuroraMallGoodsService {

    @Autowired
    private AuroraMallGoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getAuroraMallGoodsPage(PageQueryUtil pageUtil) {
        List<AuroraMallGoods> goodsList = goodsMapper.findAuroraMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalAuroraMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveAuroraMallGoods(AuroraMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != AuroraMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        if (goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId()) != null) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setGoodsName(AuroraMallUtils.cleanString(goods.getGoodsName()));
        goods.setGoodsIntro(AuroraMallUtils.cleanString(goods.getGoodsIntro()));
        goods.setTag(AuroraMallUtils.cleanString(goods.getTag()));
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveAuroraMallGoods(List<AuroraMallGoods> auroraMallGoodsList) {
        if (!CollectionUtils.isEmpty(auroraMallGoodsList)) {
            goodsMapper.batchInsert(auroraMallGoodsList);
        }
    }

    @Override
    public String updateAuroraMallGoods(AuroraMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != AuroraMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        AuroraMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        AuroraMallGoods temp2 = goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId());
        if (temp2 != null && !temp2.getGoodsId().equals(goods.getGoodsId())) {
            //name和分类id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setGoodsName(AuroraMallUtils.cleanString(goods.getGoodsName()));
        goods.setGoodsIntro(AuroraMallUtils.cleanString(goods.getGoodsIntro()));
        goods.setTag(AuroraMallUtils.cleanString(goods.getTag()));
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public AuroraMallGoods getAuroraMallGoodsById(Long id) {
        AuroraMallGoods auroraMallGoods = goodsMapper.selectByPrimaryKey(id);
        if (auroraMallGoods == null) {
            AuroraMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        return auroraMallGoods;
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchAuroraMallGoods(PageQueryUtil pageUtil) {
        List<AuroraMallGoods> goodsList = goodsMapper.findAuroraMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalAuroraMallGoodsBySearch(pageUtil);
        List<AuroraMallSearchGoodsVO> auroraMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            auroraMallSearchGoodsVOS = BeanUtil.copyList(goodsList, AuroraMallSearchGoodsVO.class);
            for (AuroraMallSearchGoodsVO auroraMallSearchGoodsVO : auroraMallSearchGoodsVOS) {
                String goodsName = auroraMallSearchGoodsVO.getGoodsName();
                String goodsIntro = auroraMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    auroraMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    auroraMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(auroraMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
