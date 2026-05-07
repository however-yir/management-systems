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
import io.howeveryir.auroramall.common.IndexConfigTypeEnum;
import io.howeveryir.auroramall.common.AuroraMallException;
import io.howeveryir.auroramall.controller.vo.AuroraMallIndexCarouselVO;
import io.howeveryir.auroramall.controller.vo.AuroraMallIndexCategoryVO;
import io.howeveryir.auroramall.controller.vo.AuroraMallIndexConfigGoodsVO;
import io.howeveryir.auroramall.service.AuroraMallCarouselService;
import io.howeveryir.auroramall.service.AuroraMallCategoryService;
import io.howeveryir.auroramall.service.AuroraMallIndexConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private AuroraMallCarouselService auroraMallCarouselService;

    @Resource
    private AuroraMallIndexConfigService auroraMallIndexConfigService;

    @Resource
    private AuroraMallCategoryService auroraMallCategoryService;

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        List<AuroraMallIndexCategoryVO> categories = auroraMallCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            AuroraMallException.fail("分类数据不完善");
        }
        List<AuroraMallIndexCarouselVO> carousels = auroraMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<AuroraMallIndexConfigGoodsVO> hotGoodses = auroraMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<AuroraMallIndexConfigGoodsVO> newGoodses = auroraMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<AuroraMallIndexConfigGoodsVO> recommendGoodses = auroraMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品
        return "mall/index";
    }
}
