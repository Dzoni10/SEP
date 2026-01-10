package com.payment.paymentserviceprovider.service;

import com.payment.paymentserviceprovider.domain.PaymentMethodType;
import com.payment.paymentserviceprovider.domain.WebShopSubscription;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import com.payment.paymentserviceprovider.registry.PaymentPluginRegistry;
import com.payment.paymentserviceprovider.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepo;
    private final PaymentPluginRegistry pluginRegistry;

    public SubscriptionService(SubscriptionRepository repo, PaymentPluginRegistry registry) {
        this.subscriptionRepo = repo;
        this.pluginRegistry = registry;
    }

    @Transactional
    public void subscribeToPaymentMethods(int webShopId,
                                          List<PaymentMethodType> methods)
            throws PaymentPluginException {

        // Validacija - sve metode moraju biti dostupne
        for (PaymentMethodType method : methods) {
            pluginRegistry.getPlugin(method);
        }

        WebShopSubscription subscription = subscriptionRepo.findByWebShopId(webShopId)
                .orElse(new WebShopSubscription(
                        null, webShopId, methods, LocalDate.now(),
                        LocalDate.now(), true
                ));

        // Ažuriranje pretplate
        WebShopSubscription updated = new WebShopSubscription(
                subscription.getId(),
                subscription.getWebShopId(),
                new ArrayList<>(new HashSet<>(methods)), // Unikati
                subscription.getCreatedAt(),
                LocalDate.now(),
                true
        );

        subscriptionRepo.save(updated);
    }


    @Transactional
    public void unsubscribeFromPaymentMethod(int webShopId,
                                             PaymentMethodType method)
            throws PaymentPluginException {

        WebShopSubscription subscription = subscriptionRepo.findByWebShopId(webShopId)
                .orElseThrow(() -> new PaymentPluginException("Subscription not found"));

        List<PaymentMethodType> updated = new ArrayList<>(subscription.getSubscribedMethods());
        updated.remove(method);

        if (updated.isEmpty()) {
            throw new PaymentPluginException("Web shop must have at least one payment method");
        }

        WebShopSubscription newSub = new WebShopSubscription(
                subscription.getId(),
                subscription.getWebShopId(),
                updated,
                subscription.getCreatedAt(),
                LocalDate.now(),
                subscription.isActive()
        );

        subscriptionRepo.save(newSub);
    }

    /**
     * Preuzimanje dostupnih metoda za web shop
     */
    public List<PaymentMethodType> getAvailableMethodsForWebShop(int webShopId)
            throws PaymentPluginException {

        WebShopSubscription subscription = subscriptionRepo.findByWebShopId(webShopId)
                .orElseThrow(() -> new PaymentPluginException("Web shop not subscribed"));

        if (!subscription.isActive()) {
            throw new PaymentPluginException("Subscription is not active");
        }

        return subscription.getSubscribedMethods();
    }

}
