package com.payment.paymentserviceprovider.exception;

public class PaymentPluginException extends Exception{
    public PaymentPluginException(String message) {super(message);}
    public PaymentPluginException(String message, Throwable cause) {super(message, cause);}
}
