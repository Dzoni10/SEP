package com.payment.paymentserviceprovider.bank.domain;

import java.time.LocalDateTime;

/**
 * Privremena klasa za čuvanje transakcija u memoriji
 * Kasnije će biti entitet u bazi
 */
public class PaymentTransaction {
    private String paymentId;
    private String merchantId;
    private double amount;
    private String currency;
    private String stan;
    private LocalDateTime pspTimestamp;
    private String status; // PENDING, SUCCESS, FAILED
    private String globalTransactionId;
    private LocalDateTime acquirerTimestamp;
    private String callbackUrl;
    private Integer orderId;
    private String successUrl;
    private String failedUrl;
    private String errorUrl;

    public PaymentTransaction(String paymentId, String merchantId, double amount, 
                             String currency, String stan, LocalDateTime pspTimestamp,
                             String status, String callbackUrl, Integer orderId,
                             String successUrl, String failedUrl, String errorUrl) {
        this.paymentId = paymentId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
        this.stan = stan;
        this.pspTimestamp = pspTimestamp;
        this.status = status;
        this.callbackUrl = callbackUrl;
        this.orderId = orderId;
        this.successUrl = successUrl;
        this.failedUrl = failedUrl;
        this.errorUrl = errorUrl;
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getStan() { return stan; }
    public void setStan(String stan) { this.stan = stan; }
    
    public LocalDateTime getPspTimestamp() { return pspTimestamp; }
    public void setPspTimestamp(LocalDateTime pspTimestamp) { this.pspTimestamp = pspTimestamp; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getGlobalTransactionId() { return globalTransactionId; }
    public void setGlobalTransactionId(String globalTransactionId) { this.globalTransactionId = globalTransactionId; }
    
    public LocalDateTime getAcquirerTimestamp() { return acquirerTimestamp; }
    public void setAcquirerTimestamp(LocalDateTime acquirerTimestamp) { this.acquirerTimestamp = acquirerTimestamp; }
    
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    
    public String getSuccessUrl() { return successUrl; }
    public void setSuccessUrl(String successUrl) { this.successUrl = successUrl; }
    
    public String getFailedUrl() { return failedUrl; }
    public void setFailedUrl(String failedUrl) { this.failedUrl = failedUrl; }
    
    public String getErrorUrl() { return errorUrl; }
    public void setErrorUrl(String errorUrl) { this.errorUrl = errorUrl; }
}
