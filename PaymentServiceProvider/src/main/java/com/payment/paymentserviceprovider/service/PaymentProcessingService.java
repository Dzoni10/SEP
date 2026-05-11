package com.payment.paymentserviceprovider.service;

import com.payment.paymentserviceprovider.config.PayPalTokenUtil;
import com.payment.paymentserviceprovider.config.RestTemplateConfig;
import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.dto.BankWebhookRequest;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import com.payment.paymentserviceprovider.plugins.PaymentPlugin;
import com.payment.paymentserviceprovider.registry.PaymentPluginRegistry;
import com.payment.paymentserviceprovider.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentProcessingService {

    private final PaymentPluginRegistry pluginRegistry;
    private final TransactionRepository transactionRepository;
    private final SubscriptionService subscriptionService;
    private final PayPalTokenUtil payPalTokenUtil;
    private final RestTemplate secureRestTemplate;
    private final RestTemplate publicRestTemplate;

    public PaymentProcessingService(PaymentPluginRegistry pluginRegistry,
                         SubscriptionService subscriptionService,TransactionRepository transactionRepository, PayPalTokenUtil payPalTokenUtil,@Qualifier("publicRestTemplate") RestTemplate publicRestTemplate,@Qualifier("secureRestTemplate") RestTemplate secureRestTemplate) {
        this.pluginRegistry = pluginRegistry;
        this.subscriptionService = subscriptionService;
        this.transactionRepository = transactionRepository;
        this.payPalTokenUtil = payPalTokenUtil;
        this.publicRestTemplate = publicRestTemplate;
        this.secureRestTemplate = secureRestTemplate;
    }

    @Transactional
    public PaymentResponse initiatePayment(int webShopId,
                                           PaymentRequest paymentRequest,
                                           PaymentMethodType methodType)
            throws PaymentPluginException {

         List<PaymentMethodType> available =
                subscriptionService.getAvailableMethodsForWebShop(webShopId);

        if (!available.contains(methodType)) {
            throw new PaymentPluginException("Payment method not available for this web shop");
        }

        PaymentPlugin plugin = pluginRegistry.getPlugin(methodType);

        PaymentResult result = plugin.processPayment(paymentRequest);

        Transaction transaction = new Transaction(
                webShopId,
                (int) paymentRequest.orderId(),
                methodType,
                paymentRequest.amount(),
                paymentRequest.currency(),
                result.success() ? TransactionStatus.PENDING : TransactionStatus.FAILED,
                result.externalTransactionId(),
                LocalDate.now(),
                LocalDate.now(),
                result.errorMessage() == null ? "No error" : result.errorMessage(),
                paymentRequest.successUrl(),
                paymentRequest.failedUrl(),
                paymentRequest.errorUrl()
        );
        transaction.setStan(result.stan());
        transactionRepository.save(transaction);

        return new PaymentResponse(
                result.success(),
                result.redirectUrl(),
                result.externalTransactionId(),
                result.errorMessage()
        );
    }

    @Transactional
    public void handleBankCallback(BankWebhookRequest request){
        Transaction transaction = transactionRepository.findByStan(request.stan())
                .orElseThrow(() -> new RuntimeException("Transaction with STAN " + request.stan() + " nije pronađena"));

        if ("SUCCESS".equalsIgnoreCase(request.status())) {
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setExternalTransactionId(request.globalTransactionId()); // Zamenjujemo ga onim od banke
            transaction.setCompletedAt(LocalDate.now()); // Ili LocalDateTime, zavisno šta je u entitetu
        } else if ("FAILED".equalsIgnoreCase(request.status())) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setErrorMessage("Bank refused transaction");
            transaction.setCompletedAt(LocalDate.now());
        } else {
            transaction.setStatus(TransactionStatus.ERROR);
            transaction.setErrorMessage("Error during communication with bank");
            transaction.setCompletedAt(LocalDate.now());
        }
        transactionRepository.save(transaction);
        notifyWebShop(transaction);
    }

    private void notifyWebShop(Transaction transaction) {
        try {
            String webShopWebhookUrl = "https://localhost:8080/api/orders/" + transaction.getOrderId() + "/status";

            Map<String, String> payload = Map.of(
                    "status", transaction.getStatus().name(),
                    "transactionId", transaction.getExternalTransactionId()
            );
            secureRestTemplate.postForEntity(webShopWebhookUrl, payload, Void.class);
            System.out.println("Web shop successfully notified for order: " + transaction.getOrderId());

        } catch (Exception e) {
            System.err.println("Failed to notify Web Shop: " + e.getMessage());
        }
    }

    public Transaction getTransactionByExternalId(String externalId) {
        return transactionRepository.findByExternalTransactionId(externalId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Transactional
    public boolean capturePayPalOrder(String token) {
        try {
            String accessToken = payPalTokenUtil.getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>("{}", headers);

            String url = "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + token + "/capture";
            ResponseEntity<Map> response = publicRestTemplate.postForEntity(url, entity, Map.class);

            Map<String, Object> body = response.getBody();
            if (body != null && "COMPLETED".equals(body.get("status"))) {
                Transaction transaction = getTransactionByExternalId(token);
                transaction.setStatus(TransactionStatus.SUCCESS);
                transaction.setCompletedAt(LocalDate.now());
                transactionRepository.save(transaction);
                notifyWebShop(transaction);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error during capture-u PayPal transaction: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public Transaction cancelPayPalOrder(String token) {
        Transaction transaction = transactionRepository.findByExternalTransactionId(token).orElse(null);
        if (transaction != null) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setErrorMessage("Korisnik odustao od PayPal plaćanja");
            transaction.setCompletedAt(LocalDate.now());
            transactionRepository.save(transaction);
        }
        return transaction;
    }

    @Transactional
    public void handleCoinGateCallback(String externalId, String status) {
        Transaction transaction = transactionRepository.findByExternalTransactionId(externalId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + externalId));

        if ("paid".equalsIgnoreCase(status)) {
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setCompletedAt(LocalDate.now());
            transactionRepository.save(transaction);
            notifyWebShop(transaction);

        } else if (List.of("canceled", "expired", "invalid").contains(status.toLowerCase())) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setErrorMessage("Crypto payment failed " + status);
            transaction.setCompletedAt(LocalDate.now());
            transactionRepository.save(transaction);
            notifyWebShop(transaction);
        }
    }
}
