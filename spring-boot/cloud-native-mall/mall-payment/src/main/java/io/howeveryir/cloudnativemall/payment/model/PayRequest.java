package io.howeveryir.cloudnativemall.payment.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PayRequest {

    @NotBlank
    private String orderNo;

    @NotNull
    @Positive(message = "amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank
    @Pattern(regexp = "WECHAT_PAY|ALIPAY|UNION_PAY", message = "channel must be WECHAT_PAY, ALIPAY or UNION_PAY")
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
