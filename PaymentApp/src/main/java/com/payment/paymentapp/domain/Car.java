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

    @Column(name="model")
    private String model;

    @Column(name="engine_size")
    private int engineSize;

    @Column(name="door_number")
    private int doorNumber;

    @Column(name="type")
    @Enumerated(EnumType.STRING)
    private CarType type;

    @Column(name="year")
    private int year;

    @Column(name="rent_price")
    private double rentPrice;

    @Column(name="picture")
    private String picture;

    public Car() {}

    public Car(int id, String mark,String model, int engineSize, int doorNumber, CarType type,int year, double rentPrice,String picture) {
        this.id = id;
        this.mark = mark;
        this.model=model;
        this.engineSize = engineSize;
        this.doorNumber = doorNumber;
        this.type = type;
        this.year = year;
        this.rentPrice = rentPrice;
        this.picture = picture;
    }

    public Car( String mark,String model, int engineSize, int doorNumber, CarType type,int year, double rentPrice, String picture) {
        this.mark = mark;
        this.model=model;
        this.engineSize = engineSize;
        this.doorNumber = doorNumber;
        this.type = type;
        this.year = year;
        this.rentPrice = rentPrice;
        this.picture = picture;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(double rentPrice) {
        this.rentPrice = rentPrice;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
