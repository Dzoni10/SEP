package com.bankservice.bank.repositoryInterface;

import com.bankservice.bank.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepositoryInterface extends JpaRepository<Merchant,Integer>{
    Optional<Merchant> findByMerchantId(String merchantId);
}