package com.bankservice.bank.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="bank_transactions")
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Podaci dobijeni od PSP-a
    @Column(name = "payment_id", unique = true, nullable = false)
    private String paymentId;

    // Podaci dobijeni od PSP-a prilikom inicijalizacije
    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(nullable = false, updatable = false)
    private Double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String stan;

    @Column(name="psp_timestamp")
    private LocalDateTime pspTimestamp;

    @Column(name="created_at")
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(name="expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "attempts_count")
    private Integer attemptsCount;

    // Status transakcije (npr. CREATED, SUCCESS, FAILED, ERROR)
    @Column(nullable = false)
    private PaymentStatus status;

    // Ovi podaci se generišu tek kada kupac uspešno plati
    @Column(name = "global_transaction_id")
    private String globalTransactionId; // Vraća se PSP-u u odgovoru

    @Column(name = "acquirer_timestamp")
    private LocalDateTime acquirerTimestamp;

    // Callback URL-ovi za redirekciju (popunjavaju se PUT zahtevom od PSP-a)
    @Column(name = "callback_url")
    private String callbackUrl;

    @Column(name = "success_url")
    private String successUrl;

    @Column(name = "failed_url")
    private String failedUrl;

    @Column(name = "error_url")
    private String errorUrl;
}
