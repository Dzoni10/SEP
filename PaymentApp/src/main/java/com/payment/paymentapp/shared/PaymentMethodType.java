package com.payment.paymentapp.shared;

public enum PaymentMethodType {
    CARD("CARD"),
    QR("QR"),
    CRYPTO("CRYPTO"),
    PAYPAL("PAYPAL");

    private final String value;

    PaymentMethodType(String value) {
        this.value = value;
    }


}
