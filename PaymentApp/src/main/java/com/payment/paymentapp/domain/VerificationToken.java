package com.payment.paymentapp.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    private LocalDateTime expiryDate;
    private boolean used;


    public VerificationToken(){}

    public VerificationToken(int id, String token, User user, LocalDateTime expiryDate, boolean used){
        this.id=id;
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
        this.used = used;
    }

    public VerificationToken(String token,User user,LocalDateTime expiryDate, boolean used){
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
        this.used = used;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
