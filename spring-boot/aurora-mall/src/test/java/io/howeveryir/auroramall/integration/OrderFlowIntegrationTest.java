package io.howeveryir.auroramall.integration;

import io.howeveryir.auroramall.common.Constants;
import io.howeveryir.auroramall.controller.vo.AuroraMallUserVO;
import io.howeveryir.auroramall.dao.AuroraMallGoodsMapper;
import io.howeveryir.auroramall.dao.AuroraMallOrderMapper;
import io.howeveryir.auroramall.entity.AuroraMallGoods;
import io.howeveryir.auroramall.entity.AuroraMallOrder;
import io.howeveryir.auroramall.support.MysqlContainerSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderFlowIntegrationTest extends MysqlContainerSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuroraMallGoodsMapper auroraMallGoodsMapper;

    @Autowired
    private AuroraMallOrderMapper auroraMallOrderMapper;

    @BeforeEach
    void resetSchema() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("aurora_mall_schema.sql"));
        }
    }

    @Test
    void saveOrderAndPaySuccessShouldDeductStockAndUpdateOrderStatus() throws Exception {
        Long goodsId = jdbcTemplate.queryForObject(
                "select goods_id from tb_aurora_mall_goods_info where goods_sell_status = 0 order by goods_id limit 1",
                Long.class
        );
        Integer sellingPrice = jdbcTemplate.queryForObject(
                "select selling_price from tb_aurora_mall_goods_info where goods_id = ?",
                Integer.class,
                goodsId
        );

        AuroraMallGoods beforeGoods = auroraMallGoodsMapper.selectByPrimaryKey(goodsId);
        Integer stockBefore = beforeGoods.getStockNum();

        jdbcTemplate.update(
                "insert into tb_aurora_mall_shopping_cart_item(user_id, goods_id, goods_count, is_deleted, create_time, update_time) values (?, ?, ?, 0, now(), now())",
                6L,
                goodsId,
                1
        );

        MockHttpSession session = new MockHttpSession();
        AuroraMallUserVO user = new AuroraMallUserVO();
        user.setUserId(6L);
        user.setAddress("上海浦东新区测试路 100 号");
        session.setAttribute(Constants.MALL_USER_SESSION_KEY, user);

        MvcResult saveOrderResult = mockMvc.perform(get("/saveOrder").session(session))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = saveOrderResult.getResponse().getRedirectedUrl();
        assertThat(redirectedUrl).startsWith("/orders/");
        String orderNo = redirectedUrl.substring("/orders/".length());

        AuroraMallOrder createdOrder = auroraMallOrderMapper.selectByOrderNo(orderNo);
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getTotalPrice()).isEqualTo(sellingPrice);

        AuroraMallGoods afterSaveGoods = auroraMallGoodsMapper.selectByPrimaryKey(goodsId);
        assertThat(afterSaveGoods.getStockNum()).isEqualTo(stockBefore - 1);

        mockMvc.perform(get("/paySuccess")
                        .param("orderNo", orderNo)
                        .param("payType", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        AuroraMallOrder paidOrder = auroraMallOrderMapper.selectByOrderNo(orderNo);
        assertThat(paidOrder.getOrderStatus()).isEqualTo((byte) 1);
        assertThat(paidOrder.getPayStatus()).isEqualTo((byte) 1);
        assertThat(paidOrder.getPayType()).isEqualTo((byte) 1);
    }
}
