package com.payment.paymentserviceprovider.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class StanBuilder {

    private final AtomicInteger sequence = new AtomicInteger(1);
    private final int MAX_STAN = 999999;

    public String generateStan() {
        int nextSeq = sequence.getAndUpdate(current -> {
            if (current >= MAX_STAN) {
                return 1;
            }
            return current + 1;
        });

        return String.format("%06d", nextSeq);
    }
}
