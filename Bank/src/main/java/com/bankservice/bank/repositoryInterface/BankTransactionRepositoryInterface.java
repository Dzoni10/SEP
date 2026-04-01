package com.bankservice.bank.repositoryInterface;

import com.bankservice.bank.model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankTransactionRepositoryInterface  extends JpaRepository<BankTransaction,Integer> {
    Optional<BankTransaction> findByPaymentId(String paymentId);
}
