/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package io.howeveryir.auroramall.controller.admin;

import io.howeveryir.auroramall.common.Constants;
import io.howeveryir.auroramall.common.AuroraMallCategoryLevelEnum;
import io.howeveryir.auroramall.common.AuroraMallException;
import io.howeveryir.auroramall.common.ServiceResultEnum;
import io.howeveryir.auroramall.entity.GoodsCategory;
import io.howeveryir.auroramall.entity.AuroraMallGoods;
import io.howeveryir.auroramall.service.AuroraMallCategoryService;
import io.howeveryir.auroramall.service.AuroraMallGoodsService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 13
 * @qq交流群 796794009
 * @email opensource@aurora-mall.dev
 * @link https://github.com/however-yir
 */
@Controller
@RequestMapping("/admin")
public class AuroraMallGoodsController {

    @Resource
    private AuroraMallGoodsService auroraMallGoodsService;
    @Resource
    private AuroraMallCategoryService auroraMallCategoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "aurora_mall_goods");
        return "admin/aurora_mall_goods";
    }

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = auroraMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), AuroraMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = auroraMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), AuroraMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = auroraMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), AuroraMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/aurora_mall_goods_edit";
            }
        }
        AuroraMallException.fail("分类数据不完善");
        return null;
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        request.setAttribute("path", "edit");
        AuroraMallGoods auroraMallGoods = auroraMallGoodsService.getAuroraMallGoodsById(goodsId);
        if (auroraMallGoods.getGoodsCategoryId() > 0) {
            if (auroraMallGoods.getGoodsCategoryId() != null || auroraMallGoods.getGoodsCategoryId() > 0) {
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory currentGoodsCategory = auroraMallCategoryService.getGoodsCategoryById(auroraMallGoods.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == AuroraMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    //查询所有的一级分类
                    List<GoodsCategory> firstLevelCategories = auroraMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), AuroraMallCategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = auroraMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), AuroraMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategory secondCategory = auroraMallCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategory> secondLevelCategories = auroraMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), AuroraMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategory firstCategory = auroraMallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firstCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firstCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        if (auroraMallGoods.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = auroraMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), AuroraMallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = auroraMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), AuroraMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = auroraMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), AuroraMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", auroraMallGoods);
        request.setAttribute("path", "goods-edit");
        return "admin/aurora_mall_goods_edit";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (ObjectUtils.isEmpty(params.get("page")) || ObjectUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(auroraMallGoodsService.getAuroraMallGoodsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody AuroraMallGoods auroraMallGoods) {
        if (!StringUtils.hasText(auroraMallGoods.getGoodsName())
                || !StringUtils.hasText(auroraMallGoods.getGoodsIntro())
                || !StringUtils.hasText(auroraMallGoods.getTag())
                || Objects.isNull(auroraMallGoods.getOriginalPrice())
                || Objects.isNull(auroraMallGoods.getGoodsCategoryId())
                || Objects.isNull(auroraMallGoods.getSellingPrice())
                || Objects.isNull(auroraMallGoods.getStockNum())
                || Objects.isNull(auroraMallGoods.getGoodsSellStatus())
                || !StringUtils.hasText(auroraMallGoods.getGoodsCoverImg())
                || !StringUtils.hasText(auroraMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = auroraMallGoodsService.saveAuroraMallGoods(auroraMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody AuroraMallGoods auroraMallGoods) {
        if (Objects.isNull(auroraMallGoods.getGoodsId())
                || !StringUtils.hasText(auroraMallGoods.getGoodsName())
                || !StringUtils.hasText(auroraMallGoods.getGoodsIntro())
                || !StringUtils.hasText(auroraMallGoods.getTag())
                || Objects.isNull(auroraMallGoods.getOriginalPrice())
                || Objects.isNull(auroraMallGoods.getSellingPrice())
                || Objects.isNull(auroraMallGoods.getGoodsCategoryId())
                || Objects.isNull(auroraMallGoods.getStockNum())
                || Objects.isNull(auroraMallGoods.getGoodsSellStatus())
                || !StringUtils.hasText(auroraMallGoods.getGoodsCoverImg())
                || !StringUtils.hasText(auroraMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = auroraMallGoodsService.updateAuroraMallGoods(auroraMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        AuroraMallGoods goods = auroraMallGoodsService.getAuroraMallGoodsById(id);
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (auroraMallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

}