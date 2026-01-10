package com.payment.paymentapp.repositoryInterfaces;

import com.payment.paymentapp.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepositoryInterface extends JpaRepository<Car, Integer> {
    void removeCarById(int id);
}
