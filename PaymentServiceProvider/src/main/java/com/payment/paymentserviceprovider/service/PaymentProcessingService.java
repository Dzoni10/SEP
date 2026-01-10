package com.payment.paymentserviceprovider.service;

import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import com.payment.paymentserviceprovider.plugins.PaymentPlugin;
import com.payment.paymentserviceprovider.registry.PaymentPluginRegistry;
import com.payment.paymentserviceprovider.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentProcessingService {

    private final PaymentPluginRegistry pluginRegistry;
    private final TransactionRepository transactionRepository;
    private final SubscriptionService subscriptionService;

    public PaymentProcessingService(PaymentPluginRegistry pluginRegistry,
                         SubscriptionService subscriptionService,TransactionRepository transactionRepository) {
        this.pluginRegistry = pluginRegistry;
        this.subscriptionService = subscriptionService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public PaymentResponse initiatePayment(int webShopId,
                                           PaymentRequest paymentRequest,
                                           PaymentMethodType methodType)
            throws PaymentPluginException {

        // 1. Validacija - web shop ima li pristup ovoj metodi
        List<PaymentMethodType> available =
                subscriptionService.getAvailableMethodsForWebShop(webShopId);

        if (!available.contains(methodType)) {
            throw new PaymentPluginException("Payment method not available for this web shop");
        }

        // 2. Preuzimanje odgovarajućeg plug-ina
        PaymentPlugin plugin = pluginRegistry.getPlugin(methodType);

        // 3. Procesiranje
        PaymentResult result = plugin.processPayment(paymentRequest);

        // 4. Čuvanje transakcije
        Transaction transaction = new Transaction(
                webShopId,
                (int) paymentRequest.orderId(),
                methodType,
                paymentRequest.amount(),
                paymentRequest.currency(),
                result.success() ? TransactionStatus.PENDING : TransactionStatus.FAILED,
                result.externalTransactionId(),
                LocalDate.now(),
                result.success() ? null : LocalDate.now(),
                result.errorMessage()
        );

        transactionRepository.save(transaction);

        return new PaymentResponse(
                result.success(),
                result.redirectUrl(),
                result.externalTransactionId(),
                result.errorMessage()
        );
    }
}
