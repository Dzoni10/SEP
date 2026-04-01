package com.payment.paymentserviceprovider.registry;

import com.payment.paymentserviceprovider.config.PaymentMethodConfig;
import com.payment.paymentserviceprovider.domain.PaymentMethodType;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import com.payment.paymentserviceprovider.plugins.PaymentPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PaymentPluginRegistry {
    private final Map<PaymentMethodType, PaymentPlugin> plugins = new ConcurrentHashMap<>();
    private final Map<String, PaymentMethodConfig> configurations = new ConcurrentHashMap<>();

    @Autowired
    public PaymentPluginRegistry(List<PaymentPlugin> pluginList) {
        for (PaymentPlugin plugin : pluginList) {
            try{
                plugin.initialize(new HashMap<>());
                plugins.put(plugin.getPaymentMethodType(), plugin);
            }catch (Exception e) {
                System.err.println("Error during loading plugins:" + e.getMessage());
            }
        }
        System.out.println("Success plugin load in PSP back: " + plugins.keySet());
    }

    public void registerPlugin(PaymentPlugin plugin, PaymentMethodConfig config)
            throws PaymentPluginException {

        if (!plugin.validateConfiguration(config.config())) {
            throw new PaymentPluginException("Invalid configuration for " + plugin.getPluginId());
        }

        plugin.initialize(config.config());
        plugins.put(plugin.getPaymentMethodType(), plugin);
        configurations.put(plugin.getPluginId(), config);
    }

    public void unregisterPlugin(PaymentMethodType type) throws PaymentPluginException {
        if (plugins.size() <= 1) {
            throw new PaymentPluginException("Must maintain at least one active payment method");
        }
        plugins.remove(type);
    }

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

    public List<PaymentMethodType> getAvailableMethods() {
        return new ArrayList<>(plugins.keySet());
    }
}
