package io.howeveryir.auroramall.persistence;

import io.howeveryir.auroramall.common.AuroraMallOrderStatusEnum;
import io.howeveryir.auroramall.dao.AuroraMallGoodsMapper;
import io.howeveryir.auroramall.dao.AuroraMallOrderMapper;
import io.howeveryir.auroramall.entity.AuroraMallGoods;
import io.howeveryir.auroramall.entity.AuroraMallOrder;
import io.howeveryir.auroramall.entity.StockNumDTO;
import io.howeveryir.auroramall.support.MysqlContainerSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OrderMapperTransactionTest extends MysqlContainerSupport {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuroraMallOrderMapper auroraMallOrderMapper;

    @Autowired
    private AuroraMallGoodsMapper auroraMallGoodsMapper;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void resetSchema() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("aurora_mall_schema.sql"));
        }
    }

    @Test
    void orderMapperShouldInsertAndQueryOrder() {
        AuroraMallOrder order = new AuroraMallOrder();
        order.setOrderNo("UT-" + System.currentTimeMillis());
        order.setUserId(6L);
        order.setTotalPrice(1999);
        order.setPayStatus((byte) 0);
        order.setPayType((byte) 0);
        order.setOrderStatus((byte) AuroraMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus());
        order.setExtraInfo("unit-test");
        order.setUserAddress("上海测试地址");
        order.setIsDeleted((byte) 0);

        int inserted = auroraMallOrderMapper.insertSelective(order);

        assertThat(inserted).isEqualTo(1);
        assertThat(order.getOrderId()).isNotNull();

        AuroraMallOrder fromDb = auroraMallOrderMapper.selectByOrderNo(order.getOrderNo());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getTotalPrice()).isEqualTo(1999);
        assertThat(fromDb.getUserId()).isEqualTo(6L);
    }

    @Test
    void goodsStockUpdateShouldRollbackWhenTransactionFails() {
        Long goodsId = jdbcTemplate.queryForObject(
                "select goods_id from tb_aurora_mall_goods_info where goods_sell_status = 0 order by goods_id limit 1",
                Long.class
        );

        AuroraMallGoods before = auroraMallGoodsMapper.selectByPrimaryKey(goodsId);
        Integer stockBefore = before.getStockNum();

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        assertThrows(RuntimeException.class, () ->
                transactionTemplate.execute(status -> {
                    StockNumDTO stockNumDTO = new StockNumDTO();
                    stockNumDTO.setGoodsId(goodsId);
                    stockNumDTO.setGoodsCount(1);
                    auroraMallGoodsMapper.updateStockNum(Collections.singletonList(stockNumDTO));
                    throw new RuntimeException("force rollback");
                })
        );

        AuroraMallGoods after = auroraMallGoodsMapper.selectByPrimaryKey(goodsId);
        assertThat(after.getStockNum()).isEqualTo(stockBefore);
    }
}
