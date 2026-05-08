package com.bankservice.bank.service;

import com.bankservice.bank.model.BankTransaction;
import com.bankservice.bank.model.PaymentStatus;
import com.bankservice.bank.model.PaymentUrlRequest;
import com.bankservice.bank.model.UpdateCallbackRequest;
import com.bankservice.bank.repositoryInterface.BankTransactionRepositoryInterface;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final BankTransactionRepositoryInterface transactionRepositoryInterface;

    public TransactionService(BankTransactionRepositoryInterface transactionRepositoryInterface) {
        this.transactionRepositoryInterface = transactionRepositoryInterface;
    }

    @Transactional
    public void createTransaction(PaymentUrlRequest request, String paymentId){
        BankTransaction transaction = new BankTransaction();
        transaction.setPaymentId(paymentId);
        transaction.setMerchantId(request.getMerchantId()); // ID prodavca dobijen od PSP-a [cite: 68]
        transaction.setAmount(request.getAmount()); // Iznos transakcije [cite: 68]
        transaction.setCurrency(request.getCurrency());
        transaction.setStan(request.getStan()); // ID za praćenje između PSP-a i banke [cite: 68]
        transaction.setPspTimestamp(request.getPspTimestamp());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        transaction.setStatus(PaymentStatus.CREATED);
        transaction.setAttemptsCount(0);
        transactionRepositoryInterface.save(transaction);
    }

    @Transactional
    public void updateCallbackUrls(String paymentId, UpdateCallbackRequest request) {
        BankTransaction transaction = transactionRepositoryInterface.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Transaction with paymentId " + paymentId + " not found"));
        transaction.setCallbackUrl(request.getCallbackUrl());
        transaction.setSuccessUrl(request.getSuccessUrl());
        transaction.setFailedUrl(request.getFailedUrl());
        transaction.setErrorUrl(request.getErrorUrl());
        transactionRepositoryInterface.save(transaction);
    }

    public BankTransaction getTransaction(String paymentId) {
        return transactionRepositoryInterface.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Transaction with ID " + paymentId + " not founded."));
    }
}
