package com.payment.paymentserviceprovider.repository;

import com.payment.paymentserviceprovider.domain.WebShopSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<WebShopSubscription,Integer> {

    Optional<WebShopSubscription> findByWebShopId(int webShopId);
}
