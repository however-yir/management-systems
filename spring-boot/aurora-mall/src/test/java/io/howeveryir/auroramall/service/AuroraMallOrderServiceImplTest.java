package io.howeveryir.auroramall.service;

import io.howeveryir.auroramall.common.AuroraMallException;
import io.howeveryir.auroramall.common.AuroraMallOrderStatusEnum;
import io.howeveryir.auroramall.common.Constants;
import io.howeveryir.auroramall.common.PayStatusEnum;
import io.howeveryir.auroramall.common.ServiceResultEnum;
import io.howeveryir.auroramall.controller.vo.AuroraMallShoppingCartItemVO;
import io.howeveryir.auroramall.controller.vo.AuroraMallUserVO;
import io.howeveryir.auroramall.dao.AuroraMallGoodsMapper;
import io.howeveryir.auroramall.dao.AuroraMallOrderItemMapper;
import io.howeveryir.auroramall.dao.AuroraMallOrderMapper;
import io.howeveryir.auroramall.dao.AuroraMallShoppingCartItemMapper;
import io.howeveryir.auroramall.entity.AuroraMallGoods;
import io.howeveryir.auroramall.entity.AuroraMallOrder;
import io.howeveryir.auroramall.entity.StockNumDTO;
import io.howeveryir.auroramall.service.impl.AuroraMallOrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuroraMallOrderServiceImplTest {

    @Mock
    private AuroraMallOrderMapper auroraMallOrderMapper;

    @Mock
    private AuroraMallOrderItemMapper auroraMallOrderItemMapper;

    @Mock
    private AuroraMallShoppingCartItemMapper auroraMallShoppingCartItemMapper;

    @Mock
    private AuroraMallGoodsMapper auroraMallGoodsMapper;

    @InjectMocks
    private AuroraMallOrderServiceImpl auroraMallOrderService;

    @Test
    void saveOrderShouldCalculatePriceDeductStockAndCreateOrder() {
        AuroraMallUserVO user = new AuroraMallUserVO();
        user.setUserId(6L);
        user.setAddress("上海浦东新区测试地址");

        AuroraMallShoppingCartItemVO item1 = buildCartItem(1L, 101L, 2, 120);
        AuroraMallShoppingCartItemVO item2 = buildCartItem(2L, 102L, 1, 300);
        List<AuroraMallShoppingCartItemVO> cartItems = Arrays.asList(item1, item2);

        AuroraMallGoods goods1 = buildGoods(101L, 10, Constants.SELL_STATUS_UP);
        AuroraMallGoods goods2 = buildGoods(102L, 10, Constants.SELL_STATUS_UP);

        when(auroraMallGoodsMapper.selectByPrimaryKeys(Arrays.asList(101L, 102L))).thenReturn(Arrays.asList(goods1, goods2));
        when(auroraMallShoppingCartItemMapper.deleteBatch(Arrays.asList(1L, 2L))).thenReturn(2);
        when(auroraMallGoodsMapper.updateStockNum(anyList())).thenReturn(2);
        when(auroraMallOrderMapper.insertSelective(any(AuroraMallOrder.class))).thenAnswer(invocation -> {
            AuroraMallOrder order = invocation.getArgument(0);
            order.setOrderId(88L);
            return 1;
        });
        when(auroraMallOrderItemMapper.insertBatch(anyList())).thenReturn(2);

        String orderNo = auroraMallOrderService.saveOrder(user, cartItems);

        assertNotNull(orderNo);
        assertTrue(orderNo.length() > 10);

        ArgumentCaptor<AuroraMallOrder> orderCaptor = ArgumentCaptor.forClass(AuroraMallOrder.class);
        verify(auroraMallOrderMapper).insertSelective(orderCaptor.capture());
        AuroraMallOrder createdOrder = orderCaptor.getValue();
        assertEquals(Integer.valueOf(540), createdOrder.getTotalPrice());
        assertEquals(user.getAddress(), createdOrder.getUserAddress());

        ArgumentCaptor<List<StockNumDTO>> stockCaptor = ArgumentCaptor.forClass(List.class);
        verify(auroraMallGoodsMapper).updateStockNum(stockCaptor.capture());
        assertEquals(2, stockCaptor.getValue().size());
        assertEquals(Integer.valueOf(2), stockCaptor.getValue().get(0).getGoodsCount());
        assertEquals(Integer.valueOf(1), stockCaptor.getValue().get(1).getGoodsCount());
    }

    @Test
    void saveOrderShouldThrowWhenStockInsufficient() {
        AuroraMallUserVO user = new AuroraMallUserVO();
        user.setUserId(6L);
        user.setAddress("上海浦东新区测试地址");

        AuroraMallShoppingCartItemVO item = buildCartItem(1L, 101L, 3, 100);
        AuroraMallGoods goods = buildGoods(101L, 2, Constants.SELL_STATUS_UP);

        when(auroraMallGoodsMapper.selectByPrimaryKeys(Collections.singletonList(101L))).thenReturn(Collections.singletonList(goods));

        AuroraMallException exception = assertThrows(
                AuroraMallException.class,
                () -> auroraMallOrderService.saveOrder(user, Collections.singletonList(item))
        );

        assertEquals(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult(), exception.getMessage());
    }

    @Test
    void paySuccessShouldUpdatePaidStatusAndPayType() {
        AuroraMallOrder order = new AuroraMallOrder();
        order.setOrderNo("ORDER-1");
        order.setOrderStatus((byte) AuroraMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus());

        when(auroraMallOrderMapper.selectByOrderNo("ORDER-1")).thenReturn(order);
        when(auroraMallOrderMapper.updateByPrimaryKeySelective(order)).thenReturn(1);

        String result = auroraMallOrderService.paySuccess("ORDER-1", 1);

        assertEquals(ServiceResultEnum.SUCCESS.getResult(), result);
        assertEquals(Byte.valueOf((byte) AuroraMallOrderStatusEnum.ORDER_PAID.getOrderStatus()), order.getOrderStatus());
        assertEquals(Byte.valueOf((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus()), order.getPayStatus());
        assertEquals(Byte.valueOf((byte) 1), order.getPayType());
        verify(auroraMallOrderMapper).updateByPrimaryKeySelective(order);
    }

    private AuroraMallShoppingCartItemVO buildCartItem(Long cartId, Long goodsId, Integer count, Integer price) {
        AuroraMallShoppingCartItemVO item = new AuroraMallShoppingCartItemVO();
        item.setCartItemId(cartId);
        item.setGoodsId(goodsId);
        item.setGoodsCount(count);
        item.setSellingPrice(price);
        item.setGoodsName("商品-" + goodsId);
        item.setGoodsCoverImg("/img/" + goodsId + ".png");
        return item;
    }

    private AuroraMallGoods buildGoods(Long goodsId, Integer stock, int sellStatus) {
        AuroraMallGoods goods = new AuroraMallGoods();
        goods.setGoodsId(goodsId);
        goods.setGoodsName("商品-" + goodsId);
        goods.setStockNum(stock);
        goods.setGoodsSellStatus((byte) sellStatus);
        return goods;
    }
}
