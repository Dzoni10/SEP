package com.bankservice.bank.model;

import com.bankservice.bank.config.CryptoConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="bank_accounts")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "card_holder_name", nullable = false)
    private String cardHolderName;

    @Column(name = "expiration_date", nullable = false)
    private String expirationDate;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private Double balance;

    //rezervisana sredstva u toku transakcije
    @Column(name = "reserved_funds")
    private Double reservedFunds = 0.0;

    @Convert(converter = CryptoConverter.class)
    @Column(name="pan",unique = true,nullable = false)
    private String pan;

    @Convert(converter = CryptoConverter.class)
    @Column(name="security_code", nullable = false)
    private String securityCode;

}
