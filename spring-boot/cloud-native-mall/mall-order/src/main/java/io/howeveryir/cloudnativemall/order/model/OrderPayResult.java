package io.howeveryir.cloudnativemall.order.model;

public class OrderPayResult {

    private OrderInfo order;
    private String paymentNo;

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }
}
