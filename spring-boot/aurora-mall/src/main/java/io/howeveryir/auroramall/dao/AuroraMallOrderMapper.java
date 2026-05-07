/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package io.howeveryir.auroramall.dao;

import io.howeveryir.auroramall.entity.AuroraMallOrder;
import io.howeveryir.auroramall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuroraMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(AuroraMallOrder record);

    int insertSelective(AuroraMallOrder record);

    AuroraMallOrder selectByPrimaryKey(Long orderId);

    AuroraMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(AuroraMallOrder record);

    int updateByPrimaryKey(AuroraMallOrder record);

    List<AuroraMallOrder> findAuroraMallOrderList(PageQueryUtil pageUtil);

    int getTotalAuroraMallOrders(PageQueryUtil pageUtil);

    List<AuroraMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}