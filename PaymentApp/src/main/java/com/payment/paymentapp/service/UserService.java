package com.payment.paymentapp.service;


import com.payment.paymentapp.domain.User;
import com.payment.paymentapp.repositoryInterfaces.UserRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepositoryInterface userReposioryInterface;

    @Autowired
    public UserService(UserRepositoryInterface userRepositoryInterface){
        this.userReposioryInterface=userRepositoryInterface;
    }

    public User findByEmail(String email){
        return userReposioryInterface.findUserByEmail(email);
    }

    public User save(User user){
        return userReposioryInterface.save(user);
    }

    public boolean isEmailExist(String email){
        return userReposioryInterface.existsByEmail(email);
    }
}
