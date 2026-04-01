package com.payment.paymentserviceprovider.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="payment_method")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_method_id")
    private int id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodType type;

    @Column(name = "image")
    private String image;

    @Column(name = "description", length = 255)
    private String description;

}
