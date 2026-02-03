package com.bankservice.bank.domain;

/**
 * Račun prodavca (merchant) u banci.
 * Prodavac prima uplate od kupaca - novac ide sa kupčevog računa na ovaj račun.
 */
public class MerchantAccount {
    private String merchantId;
    private String accountNumber;
    private String ownerName;
    private double balance;

    public MerchantAccount(String merchantId, String accountNumber, String ownerName, double balance) {
        this.merchantId = merchantId;
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
