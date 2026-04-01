package com.bankservice.bank.repositoryInterface;

import com.bankservice.bank.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepositoryInterface extends JpaRepository<BankAccount,Integer> {
    Optional<BankAccount> findByPan(String pan);
}
