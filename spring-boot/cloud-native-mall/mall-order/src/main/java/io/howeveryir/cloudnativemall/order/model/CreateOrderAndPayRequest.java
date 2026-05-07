package io.howeveryir.cloudnativemall.order.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class CreateOrderAndPayRequest {

    @NotNull
    @Positive(message = "userId must be positive")
    private Long userId;

    @NotNull
    @Positive(message = "productId must be positive")
    private Long productId;

    @NotNull
    @Min(value = 1, message = "quantity must >= 1")
    private Integer quantity;

    @NotNull
    @Positive(message = "amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank
    @Pattern(regexp = "WECHAT_PAY|ALIPAY|UNION_PAY", message = "channel must be WECHAT_PAY, ALIPAY or UNION_PAY")
    private String channel;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
