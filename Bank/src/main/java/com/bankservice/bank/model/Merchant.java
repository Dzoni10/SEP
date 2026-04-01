package com.bankservice.bank.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Id koji PSP salje banci
    @Column(name="merchant_id",unique = true,nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, name="account_number",nullable = false)
    private String accountNumber;

    private Boolean active;





}
