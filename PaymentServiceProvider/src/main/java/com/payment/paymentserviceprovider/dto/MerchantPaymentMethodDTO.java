package com.payment.paymentserviceprovider.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantPaymentMethodDTO {

    private int merchantPaymentMethodId;
    private Boolean isEnabled;
    private int paymentMethodId;

    public MerchantPaymentMethodDTO() {}

    public void setMerchantPaymentMethodId(int merchantPaymentMethodId) {
        this.merchantPaymentMethodId = merchantPaymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public int getMerchantPaymentMethodId() {
        return merchantPaymentMethodId;
    }

    public int getPaymentMethodId() {
        return paymentMethodId;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public MerchantPaymentMethodDTO(int merchantPaymentMethodId, Boolean isEnabled, int paymentMethodId) {
        this.merchantPaymentMethodId = merchantPaymentMethodId;
        this.isEnabled = isEnabled;
        this.paymentMethodId = paymentMethodId;
    }

}
