package com.bankservice.bank.service;


import com.bankservice.bank.config.CardValidator;
import com.bankservice.bank.config.IpsQrUtil;
import com.bankservice.bank.dto.PaymentSubmitRequest;
import com.bankservice.bank.model.*;
import com.bankservice.bank.repositoryInterface.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BankPaymentService {

    private final BankTransactionRepositoryInterface bankTransactionRepository;
    private final BankAccountRepositoryInterface bankAccountRepository;
    private final CardValidator cardValidator;
    private final IpsQrUtil ipsQrUtil;
    private final MerchantRepositoryInterface merchantRepositoryInterface;

    public BankPaymentService(BankTransactionRepositoryInterface bankTransactionRepository,BankAccountRepositoryInterface bankAccountRepository,CardValidator cardValidator, IpsQrUtil ipsQrUtil, MerchantRepositoryInterface merchantRepositoryInterface) {
        this.bankTransactionRepository = bankTransactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.cardValidator = cardValidator;
        this.ipsQrUtil = ipsQrUtil;
        this.merchantRepositoryInterface = merchantRepositoryInterface;
    }

    @Transactional
    public BankTransaction processPayment(String paymentId, PaymentSubmitRequest request) throws Exception {

        BankTransaction transaction = bankTransactionRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new Exception("Transaction not found"));

        if (LocalDateTime.now().isAfter(transaction.getExpiresAt())) {
            transaction.setStatus(PaymentStatus.FAILED);
            bankTransactionRepository.save(transaction);
            throw new Exception("Payment link expired");
        }

        if (transaction.getAttemptsCount() > 0) {
            throw new Exception("This form is already used for payment");
        }

        transaction.setAttemptsCount(1);

        if (!cardValidator.isValidLuhn(request.getPan())) {
            failTransaction(transaction);
            throw new Exception("Invalid card number (Luhn check)");
        }
        if (!cardValidator.isValidExpirationDate(request.getExpirationDate())) {
            failTransaction(transaction);
            throw new Exception("Invalid or expiry card date");
        }

        BankAccount account = bankAccountRepository.findByPan(request.getPan())
                .orElseThrow(() -> {
                    failTransaction(transaction);
                    return new Exception("Card not in bank system");
                });

        if (!account.getSecurityCode().equals(request.getSecurityCode()) ||
                !account.getCardHolderName().equalsIgnoreCase(request.getCardHolderName().trim())) {
            failTransaction(transaction);
            throw new Exception("Owner data or security code does not match.");
        }

        if (account.getBalance() < transaction.getAmount()) {
            failTransaction(transaction);
            throw new Exception("Not enough amount on account");
        }

        account.setBalance(account.getBalance() - transaction.getAmount());
        account.setReservedFunds(account.getReservedFunds() + transaction.getAmount());
        bankAccountRepository.save(account);

        transaction.setStatus(PaymentStatus.SUCCESS);
        transaction.setGlobalTransactionId(UUID.randomUUID().toString());
        transaction.setAcquirerTimestamp(LocalDateTime.now());
        return bankTransactionRepository.save(transaction);
    }

    @Transactional
    public BankTransaction processQrPayment(String paymentId, String scannedQrString) throws Exception {
        BankTransaction transaction = bankTransactionRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new Exception("Transaction not found"));

        if (LocalDateTime.now().isAfter(transaction.getExpiresAt())) {
            failTransaction(transaction);
            throw new Exception("Payment link expired");
        }

        if (transaction.getAttemptsCount() > 0) {
            throw new Exception("This form is already used for payment");
        }
        transaction.setAttemptsCount(1);

        Merchant merchant = merchantRepositoryInterface.findByMerchantId(transaction.getMerchantId())
                .orElseThrow(() -> new Exception("Merchant not found in Bank system"));

        boolean isValidQr = ipsQrUtil.validateScannedIpsString(scannedQrString, transaction, merchant);
        if (!isValidQr) {
            failTransaction(transaction);
            throw new Exception("Invalid QR code or data mismatch");
        }

        transaction.setStatus(PaymentStatus.SUCCESS);
        transaction.setGlobalTransactionId(UUID.randomUUID().toString());
        transaction.setAcquirerTimestamp(LocalDateTime.now());

        return bankTransactionRepository.save(transaction);
    }

    private void failTransaction(BankTransaction transaction) {
        transaction.setStatus(PaymentStatus.FAILED);
        transaction.setAcquirerTimestamp(LocalDateTime.now());
        bankTransactionRepository.save(transaction);
    }
}
