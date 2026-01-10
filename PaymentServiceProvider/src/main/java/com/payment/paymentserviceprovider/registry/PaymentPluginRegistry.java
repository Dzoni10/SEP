package com.payment.paymentserviceprovider.registry;

import com.payment.paymentserviceprovider.config.PaymentMethodConfig;
import com.payment.paymentserviceprovider.domain.PaymentMethodType;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import com.payment.paymentserviceprovider.plugins.PaymentPlugin;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PaymentPluginRegistry {
    private final Map<PaymentMethodType, PaymentPlugin> plugins = new ConcurrentHashMap<>();
    private final Map<String, PaymentMethodConfig> configurations = new ConcurrentHashMap<>();

    /**
     * Registracija plug-ina
     */
    public void registerPlugin(PaymentPlugin plugin, PaymentMethodConfig config)
            throws PaymentPluginException {

        if (!plugin.validateConfiguration(config.config())) {
            throw new PaymentPluginException("Invalid configuration for " + plugin.getPluginId());
        }

        plugin.initialize(config.config());
        plugins.put(plugin.getPaymentMethodType(), plugin);
        configurations.put(plugin.getPluginId(), config);
    }

    /**
     * Deregistracija plug-ina sa zaštitom (mora ostati bar jedan)
     */
    public void unregisterPlugin(PaymentMethodType type) throws PaymentPluginException {
        if (plugins.size() <= 1) {
            throw new PaymentPluginException("Must maintain at least one active payment method");
        }
        plugins.remove(type);
    }

    /**
     * Preuzimanje plug-ina
     */
    public PaymentPlugin getPlugin(PaymentMethodType type) throws PaymentPluginException {
        PaymentPlugin plugin = plugins.get(type);
        if (plugin == null) {
            throw new PaymentPluginException("Payment method not available: " + type);
        }
        if (!plugin.isHealthy()) {
            throw new PaymentPluginException("Payment method is unhealthy: " + type);
        }
        return plugin;
    }

    /**
     * Lista dostupnih metoda
     */
    public List<PaymentMethodType> getAvailableMethods() {
        return new ArrayList<>(plugins.keySet());
    }
}
