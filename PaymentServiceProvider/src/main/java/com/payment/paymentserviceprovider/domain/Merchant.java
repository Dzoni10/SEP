package com.payment.paymentserviceprovider.domain;

import com.payment.paymentserviceprovider.dto.MerchantDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="merchant")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="merchant_id")
    private int id;

    @Column(name = "merchant_email",
            nullable = false,
            unique = true,
            length = 50)
    private String merchantEmail;

    @Column(name = "merchant_password")
    private String merchantPassword;

    @Column(name = "seller_url")
    private String sellerUrl;

    @Column(name = "success_url")
    private String successUrl;

    @Column(name = "failed_url")
    private String failedUrl;

    @Column(name = "error_url")
    private String errorUrl;

    @Column(name = "port")
    private Integer port;

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "merchant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MerchantPaymentMethod> merchantPaymentMethods = new ArrayList<>();

    public Merchant(MerchantDTO merchantDTO) {
        this.id = merchantDTO.getId();
        this.merchantEmail = merchantDTO.getMerchantEmail();
        this.sellerUrl = merchantDTO.getSellerUrl();
        this.port = merchantDTO.getPort();
        this.successUrl = merchantDTO.getSuccessUrl();
        this.failedUrl = merchantDTO.getFailedUrl();
        this.errorUrl = merchantDTO.getErrorUrl();

    }


}
