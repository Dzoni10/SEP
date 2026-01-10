package com.payment.paymentserviceprovider.repository;

import com.payment.paymentserviceprovider.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findByExternalTransactionId(String id);
}
