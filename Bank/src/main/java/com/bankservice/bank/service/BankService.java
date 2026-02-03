package com.bankservice.bank.service;

import com.bankservice.bank.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BankService {

    private final RestTemplate restTemplate;
    private final Map<String, PaymentTransaction> transactions = new ConcurrentHashMap<>();
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public BankService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        initializeTestAccounts();
    }

    public PaymentUrlResponse generatePaymentUrl(PaymentUrlRequest request) {
        String paymentId = UUID.randomUUID().toString();
        String paymentUrl = String.format("http://localhost:4400/payment/card/%s?amount=%.2f&currency=%s",
            paymentId, request.amount(), request.currency());

        PaymentTransaction transaction = new PaymentTransaction(
            paymentId,
            request.merchantId(),
            request.amount(),
            request.currency(),
            request.stan(),
            request.pspTimestamp(),
            "PENDING",
            null, null, null, null, null
        );
        transactions.put(paymentId, transaction);

        return new PaymentUrlResponse(paymentId, paymentUrl);
    }

    public void updateTransactionWithCallback(String paymentId, String callbackUrl, Integer orderId,
                                               String successUrl, String failedUrl, String errorUrl) {
        PaymentTransaction transaction = transactions.get(paymentId);
        if (transaction != null) {
            transaction.setCallbackUrl(callbackUrl);
            transaction.setOrderId(orderId);
            transaction.setSuccessUrl(successUrl);
            transaction.setFailedUrl(failedUrl);
            transaction.setErrorUrl(errorUrl);
        }
    }

    public PaymentProcessResponse processCardPayment(CardPaymentRequest request) {
        PaymentTransaction transaction = transactions.get(request.paymentId());
        if (transaction == null) {
            return new PaymentProcessResponse(
                false, "Transaction not found", null, null, null, null
            );
        }

        Account account = findAccountByCardNumber(request.pan());
        if (account == null) {
            String errorUrl = transaction.getErrorUrl() != null ?
                transaction.getErrorUrl() + "?error=Account not found" : null;
            return new PaymentProcessResponse(
                false, "Account not found", null, null, null, errorUrl
            );
        }

        if (account.getBalance() < transaction.getAmount()) {
            String failedUrl = transaction.getFailedUrl() != null ?
                transaction.getFailedUrl() + "?error=Insufficient funds" : null;
            return new PaymentProcessResponse(
                false, "Insufficient funds", null, null, null, failedUrl
            );
        }

        account.setBalance(account.getBalance() - transaction.getAmount());
        account.setReservedAmount(account.getReservedAmount() + transaction.getAmount());

        String globalTransactionId = "GTX-" + UUID.randomUUID().toString();
        LocalDateTime acquirerTimestamp = LocalDateTime.now();

        transaction.setStatus("SUCCESS");
        transaction.setGlobalTransactionId(globalTransactionId);
        transaction.setAcquirerTimestamp(acquirerTimestamp);

        String redirectUrl = transaction.getSuccessUrl() != null ?
            transaction.getSuccessUrl() + (transaction.getSuccessUrl().contains("?") ? "&" : "?") + "transactionId=" + globalTransactionId : null;

        if (transaction.getCallbackUrl() != null) {
            callWebShopCallback(transaction.getCallbackUrl(), true, globalTransactionId, null);
        }

        return new PaymentProcessResponse(
            true, null, globalTransactionId, acquirerTimestamp, transaction.getStan(), redirectUrl
        );
    }

    private void callWebShopCallback(String callbackUrl, boolean success, String transactionId, String errorMessage) {
        try {
            Map<String, Object> body = Map.of(
                "success", success,
                "transactionId", transactionId != null ? transactionId : "",
                "errorMessage", errorMessage != null ? errorMessage : ""
            );
            restTemplate.postForEntity(callbackUrl, body, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to call Web Shop callback: " + e.getMessage());
        }
    }

    private Account findAccountByCardNumber(String cardNumber) {
        String cleaned = cardNumber.replaceAll("\\s+", "");
        return accounts.values().stream()
            .filter(acc -> acc.getCardNumber().replaceAll("\\s+", "").equals(cleaned))
            .findFirst()
            .orElse(null);
    }

    public PaymentTransaction getTransaction(String paymentId) {
        return transactions.get(paymentId);
    }

    private void initializeTestAccounts() {
        accounts.put("acc1", new Account(
            "acc1", "4532015112830366", "John Doe", "12/25", "123", 10000.0, 0.0
        ));
        accounts.put("acc2", new Account(
            "acc2", "5555555555554444", "Jane Smith", "06/26", "456", 5000.0, 0.0
        ));
    }
}
