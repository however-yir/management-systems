package io.howeveryir.cloudnativemall.order.model;

import java.math.BigDecimal;

public class PaymentConfirmRequest {

    private String orderNo;
    private BigDecimal amount;
    private String channel;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
