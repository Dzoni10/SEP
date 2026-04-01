package com.bankservice.bank.config;


import com.bankservice.bank.model.BankAccount;
import com.bankservice.bank.model.Merchant;
import com.bankservice.bank.repositoryInterface.BankAccountRepositoryInterface;
import com.bankservice.bank.repositoryInterface.MerchantRepositoryInterface;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class MerchantInitializer {

    @Bean
    CommandLineRunner initMerchant(MerchantRepositoryInterface merchantRepo, BankAccountRepositoryInterface bankAccountRepositoryInterface){
        return args -> {
            if(merchantRepo.findByMerchantId("MERCHANT_BANK_001").isEmpty()){
                Merchant m = new Merchant();

                m.setMerchantId("MERCHANT_BANK_001");
                m.setName("Test Web Shop d.o.o");
                m.setAccountNumber("845-0000000404849-87");
                merchantRepo.save(m);
                System.out.println(" -- Succesfull created merchant---");
            }

            String testPan = "4242424242424242";
            if(bankAccountRepositoryInterface.findByPan(testPan).isEmpty()){
                BankAccount bankAccount = new BankAccount();
                bankAccount.setPan(testPan);
                bankAccount.setCardHolderName("Petar Petrovic");
                bankAccount.setSecurityCode("1234");
                bankAccount.setBalance(50000.0);
                bankAccount.setReservedFunds(0.0);
                bankAccount.setAccountNumber("845-0000000404849-87");
                bankAccount.setExpirationDate("12/27");
                bankAccountRepositoryInterface.save(bankAccount);
                System.out.println(" -- Succesfull created test account---");
            }
        };
    }
}
