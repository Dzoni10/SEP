package com.payment.paymentserviceprovider.dto;


import com.payment.paymentserviceprovider.domain.Merchant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MerchantDTO {

    private int id;
    private String merchantEmail;
    private String sellerUrl;
    private Integer port;
    private String successUrl;
    private String failedUrl;
    private String errorUrl;
    private List<MerchantPaymentMethodDTO> merchantPaymentMethods;

    public MerchantDTO() {}

    public MerchantDTO(Merchant merchant) {
        this.id = merchant.getId();
        this.merchantEmail = merchant.getMerchantEmail();
        this.sellerUrl = merchant.getSellerUrl();
        this.port = merchant.getPort();
        this.successUrl = merchant.getSuccessUrl();
        this.failedUrl = merchant.getFailedUrl();
        this.errorUrl = merchant.getErrorUrl();

        this.merchantPaymentMethods = merchant.getMerchantPaymentMethods()
                .stream()
                .map(mpm -> new MerchantPaymentMethodDTO(
                        mpm.getId(),
                        mpm.getIsEnabled(),
                        mpm.getPaymentMethod().getId()
                ))
                .toList();
    }

    public int getMerchantId() {
        return id;
    }

    public String getMerchantEmail() {
        return merchantEmail;
    }

    public String getSellerUrl() {
        return sellerUrl;
    }

    public Integer getPort() {
        return port;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public String getFailedUrl() {
        return failedUrl;
    }

    public List<MerchantPaymentMethodDTO> getMerchantPaymentMethods() {
        return merchantPaymentMethods;
    }
}
