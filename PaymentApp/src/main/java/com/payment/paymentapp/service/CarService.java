package com.payment.paymentapp.service;

import com.payment.paymentapp.domain.Car;
import com.payment.paymentapp.domain.Order;
import com.payment.paymentapp.repositoryInterfaces.CarRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    private final CarRepositoryInterface carRepository;

    public CarService(CarRepositoryInterface carRepository) {
        this.carRepository = carRepository;
    }

    /**
     * Preuzmi sve iznajmljene automobile
     */
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    /**
     * Preuzmi iznajmljeni auto po ID-u
     */
    public Car findCarById(int carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + carId));
    }

    public void save(Car car) {
        carRepository.save(car);
    }

    public void delete(int id){
        carRepository.removeCarById(id);
    }
}
