package com.payment.paymentserviceprovider.config;

import com.payment.paymentserviceprovider.domain.PaymentMethodType;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import com.payment.paymentserviceprovider.plugins.CardPaymentPlugin;
import com.payment.paymentserviceprovider.registry.PaymentPluginRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Map;

/**
 * Registers payment plugins in the PaymentPluginRegistry on application startup.
 * Must run before SubscriptionInitializer so that CARD is available when subscribing.
 */
@Configuration
public class PluginRegistrationConfig {

    private static final Logger log = LoggerFactory.getLogger(PluginRegistrationConfig.class);

    private final CardPaymentPlugin cardPaymentPlugin;
    private final PaymentPluginRegistry pluginRegistry;

    public PluginRegistrationConfig(CardPaymentPlugin cardPaymentPlugin,
                                    PaymentPluginRegistry pluginRegistry) {
        this.cardPaymentPlugin = cardPaymentPlugin;
        this.pluginRegistry = pluginRegistry;
    }

    @PostConstruct
    public void registerPlugins() throws PaymentPluginException {
        PaymentMethodConfig cardConfig = new PaymentMethodConfig(
                1,
                PaymentMethodType.CARD,
                "Bank",
                Map.of("bank.url", "http://localhost:8082/api/v1/bank"),
                true,
                LocalDate.now()
        );
        pluginRegistry.registerPlugin(cardPaymentPlugin, cardConfig);
        log.info("Registered CARD payment plugin");
    }
}
