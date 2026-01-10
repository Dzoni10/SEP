package com.payment.paymentapp.controller;


import com.payment.paymentapp.domain.Car;
import com.payment.paymentapp.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.findAll();

        if(cars!=null) {
            return ResponseEntity.ok(cars);
        }else
        {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Preuzmi jedan automobil po ID-u
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable int id) {

        Car car = carService.findCarById(id);
        if(car == null) {
            return ResponseEntity.notFound().build();
        }
        else{
            return ResponseEntity.ok(car);
        }
    }

    /**
     * Ažuriraj automobil
     */
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable int id, @RequestBody Car car) {

        Car existingCar = carService.findCarById(id);

        if(existingCar == null) {
            return ResponseEntity.notFound().build();
        }else
        {
            existingCar.setDoorNumber(car.getDoorNumber());
            existingCar.setEngineSize(car.getEngineSize());
            existingCar.setMark(car.getMark());
            existingCar.setRentPrice(car.getRentPrice());
            existingCar.setType(existingCar.getType());

            carService.save(existingCar);
            return ResponseEntity.ok(existingCar);
        }
    }

    /**
     * Obriši automobil
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable int id) {
        Car car = carService.findCarById(id);
        if(car == null) {
            return ResponseEntity.notFound().build();
        }else{
            carService.delete(id);
            return ResponseEntity.ok().build();
        }
    }
}
