package com.payment.paymentserviceprovider.bank.service;

import com.payment.paymentserviceprovider.bank.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BankService {

    private final RestTemplate restTemplate;
    
    // Privremeno čuvanje transakcija u memoriji (kasnije baza)
    private final Map<String, PaymentTransaction> transactions = new ConcurrentHashMap<>();
    
    // Simulacija računa (kasnije baza)
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    
    public BankService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // Inicijalizacija test računa
        initializeTestAccounts();
    }

    /**
     * Generisanje PAYMENT_URL i PAYMENT_ID
     */
    public PaymentUrlResponse generatePaymentUrl(PaymentUrlRequest request) {
        // Generisanje PAYMENT_ID
        String paymentId = UUID.randomUUID().toString();
        
        // Generisanje PAYMENT_URL
        String paymentUrl = "http://localhost:8081/payment/" + paymentId;
        
        // Čuvanje transakcije
        PaymentTransaction transaction = new PaymentTransaction(
            paymentId,
            request.merchantId(),
            request.amount(),
            request.currency(),
            request.stan(),
            request.pspTimestamp(),
            "PENDING", // status
            null,      // callbackUrl će se dodati kasnije od PSP-a
            null       // orderId će se dodati kasnije od PSP-a
        );
        
        transactions.put(paymentId, transaction);
        
        return new PaymentUrlResponse(paymentId, paymentUrl);
    }
    
    /**
     * Ažuriranje transakcije sa callbackUrl i orderId
     * PSP poziva ovu metodu nakon što dobije PAYMENT_URL
     */
    public void updateTransactionWithCallback(String paymentId, String callbackUrl, Integer orderId) {
        PaymentTransaction transaction = transactions.get(paymentId);
        if (transaction != null) {
            transaction.setCallbackUrl(callbackUrl);
            transaction.setOrderId(orderId);
        }
    }

    /**
     * Procesiranje kartičnog plaćanja
     */
    public PaymentProcessResponse processCardPayment(CardPaymentRequest request) {
        // Pronađi transakciju
        PaymentTransaction transaction = transactions.get(request.paymentId());
        if (transaction == null) {
            return new PaymentProcessResponse(
                false,
                "Transaction not found",
                null,
                null,
                null
            );
        }
        
        // Provera stanja računa (simulacija)
        Account account = findAccountByCardNumber(request.pan());
        if (account == null) {
            return new PaymentProcessResponse(
                false,
                "Account not found",
                null,
                null,
                null
            );
        }
        
        // Provera sredstava
        if (account.getBalance() < transaction.getAmount()) {
            return new PaymentProcessResponse(
                false,
                "Insufficient funds",
                null,
                null,
                null
            );
        }
        
        // Rezervacija sredstava
        account.setBalance(account.getBalance() - transaction.getAmount());
        account.setReservedAmount(account.getReservedAmount() + transaction.getAmount());
        
        // Generisanje GLOBAL_TRANSACTION_ID
        String globalTransactionId = "GTX-" + UUID.randomUUID().toString();
        LocalDateTime acquirerTimestamp = LocalDateTime.now();
        
        // Ažuriranje transakcije
        transaction.setStatus("SUCCESS");
        transaction.setGlobalTransactionId(globalTransactionId);
        transaction.setAcquirerTimestamp(acquirerTimestamp);
        
        // Automatski pozovi callback Web Shop-a ako postoji
        if (transaction.getCallbackUrl() != null) {
            PaymentResultRequest resultRequest = new PaymentResultRequest(
                transaction.getStan(),
                globalTransactionId,
                acquirerTimestamp,
                true,
                transaction.getOrderId() != null ? transaction.getOrderId() : 0
            );
            handlePaymentResult(resultRequest);
        }
        
        return new PaymentProcessResponse(
            true,
            null,
            globalTransactionId,
            acquirerTimestamp,
            transaction.getStan()
        );
    }

    /**
     * Rukovanje rezultatom plaćanja i poziv Web Shop callback-a
     */
    public void handlePaymentResult(PaymentResultRequest request) {
        // Pronađi transakciju po STAN-u
        PaymentTransaction transaction = findTransactionByStan(request.stan());
        if (transaction == null) {
            return;
        }
        
        // Ažuriranje transakcije
        transaction.setStatus(request.success() ? "SUCCESS" : "FAILED");
        transaction.setGlobalTransactionId(request.globalTransactionId());
        transaction.setAcquirerTimestamp(request.acquirerTimestamp());
        
        // Poziv Web Shop callback-a (ako postoji callbackUrl)
        if (transaction.getCallbackUrl() != null) {
            PaymentCallbackRequest callback = new PaymentCallbackRequest(
                request.success(),
                request.globalTransactionId(),
                request.success() ? null : "Payment failed"
            );
            
            try {
                restTemplate.postForEntity(
                    transaction.getCallbackUrl(),
                    callback,
                    Void.class
                );
            } catch (Exception e) {
                // Log grešku, ali ne prekidaj proces
                System.err.println("Failed to call Web Shop callback: " + e.getMessage());
            }
        }
    }

    private Account findAccountByCardNumber(String cardNumber) {
        // Ukloni razmake za pretragu
        String cleaned = cardNumber.replaceAll("\\s+", "");
        return accounts.values().stream()
            .filter(acc -> acc.getCardNumber().replaceAll("\\s+", "").equals(cleaned))
            .findFirst()
            .orElse(null);
    }

    private PaymentTransaction findTransactionByStan(String stan) {
        return transactions.values().stream()
            .filter(t -> t.getStan().equals(stan))
            .findFirst()
            .orElse(null);
    }

    private void initializeTestAccounts() {
        // Test računi za testiranje
        accounts.put("acc1", new Account(
            "acc1",
            "4532015112830366", // Validna kartica (Lunova formula)
            "John Doe",
            "12/25",
            "123",
            10000.0,
            0.0
        ));
        
        accounts.put("acc2", new Account(
            "acc2",
            "5555555555554444", // Mastercard test kartica
            "Jane Smith",
            "06/26",
            "456",
            5000.0,
            0.0
        ));
    }
}
