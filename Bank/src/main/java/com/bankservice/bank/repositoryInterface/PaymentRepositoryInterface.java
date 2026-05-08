package com.bankservice.bank.repositoryInterface;

import com.bankservice.bank.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepositoryInterface extends JpaRepository<Payment,Integer> {
    boolean existsByStan(String stan);
}
