package com.payment.paymentserviceprovider.bank.domain;

/**
 * Zahtev za procesiranje plaćanja
 * Podaci koje korisnik unosi na formi
 */
public record CardPaymentRequest(
    String paymentId,       // PAYMENT_ID (vezuje formu za transakciju)
    String pan,             // PAN - broj kartice
    String securityCode,     // SECURITY_CODE - CVV
    String cardHolderName,   // CARD_HOLDER_NAME
    String expiryDate        // Datum važenja (MM/YY format)
) {}
