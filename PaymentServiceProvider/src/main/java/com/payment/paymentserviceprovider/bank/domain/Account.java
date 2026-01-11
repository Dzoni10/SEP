package com.payment.paymentserviceprovider.bank.domain;

/**
 * Simulacija računa u banci
 * Kasnije će biti entitet u bazi
 */
public class Account {
    private String id;
    private String cardNumber;  // PAN
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    private double balance;
    private double reservedAmount;  // Rezervisana sredstva

    public Account(String id, String cardNumber, String cardHolderName, 
                  String expiryDate, String cvv, double balance, double reservedAmount) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.balance = balance;
        this.reservedAmount = reservedAmount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    
    public double getReservedAmount() { return reservedAmount; }
    public void setReservedAmount(double reservedAmount) { this.reservedAmount = reservedAmount; }
}
