package com.payment.paymentapp.service;

import com.payment.paymentapp.domain.VerificationToken;
import com.payment.paymentapp.repositoryInterfaces.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository){
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public VerificationToken findByToken(String token){
        return verificationTokenRepository.findByToken(token);
    }

    public VerificationToken save(VerificationToken verificationToken){
        return verificationTokenRepository.save(verificationToken);
    }
}
