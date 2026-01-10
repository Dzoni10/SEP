package com.payment.paymentapp.domain;

import jakarta.persistence.*;

@Entity
@Table(name="car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="mark")
    private String mark;

    @Column(name="engine_size")
    private int engineSize;

    @Column(name="door_number")
    private int doorNumber;

    @Column(name="type")
    @Enumerated(EnumType.STRING)
    private CarType type;

    @Column(name="rent_price")
    private double rentPrice;

    public Car() {}

    public Car(int id, String mark, int engineSize, int doorNumber, CarType type, double rentPrice) {
        this.id = id;
        this.mark = mark;
        this.engineSize = engineSize;
        this.doorNumber = doorNumber;
        this.type = type;
        this.rentPrice = rentPrice;
    }

    public Car( String mark, int engineSize, int doorNumber, CarType type, double rentPrice) {
        this.mark = mark;
        this.engineSize = engineSize;
        this.doorNumber = doorNumber;
        this.type = type;
        this.rentPrice = rentPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getEngineSize() {
        return engineSize;
    }

    public void setEngineSize(int engineSize) {
        this.engineSize = engineSize;
    }

    public int getDoorNumber() {
        return doorNumber;
    }

    public void setDoorNumber(int doorNumber) {
        this.doorNumber = doorNumber;
    }

    public CarType getType() {
        return type;
    }

    public void setType(CarType type) {
        this.type = type;
    }

    public double getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(double rentPrice) {
        this.rentPrice = rentPrice;
    }
}
