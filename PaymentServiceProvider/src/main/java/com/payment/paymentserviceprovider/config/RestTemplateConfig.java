package com.payment.paymentserviceprovider.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean(name = "secureRestTemplate")
    @Primary
    public RestTemplate secureRestTemplate() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try(InputStream inputStream = new ClassPathResource("psp-truststore.p12").getInputStream()){
            trustStore.load(inputStream, "password".toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory(){
            @Override
            protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod) throws java.io.IOException{
                if(connection instanceof HttpsURLConnection){
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                }
                super.prepareConnection(connection, httpMethod);
            }
        };
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(10000);
        return new RestTemplate(requestFactory);
    }
    
    @Bean(name = "publicRestTemplate")
    public RestTemplate publicRestTemplate() {
        return new RestTemplate();
    }
}
