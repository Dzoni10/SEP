package com.payment.paymentapp.dto;

import java.util.List;

public class SubscribeRequest {
    private List<String> methods;

    public SubscribeRequest(List<String> methods) {
        this.methods = methods;
    }

    public SubscribeRequest(){}

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }
}
