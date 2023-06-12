package com.tong057.plategen;

public class Vehicle {
    private final String brand;
    private final String model;
    private final int yearProd;

    Vehicle(String brand, String model, int yearProd) {
        this.brand = brand;
        this.model = model;
        this.yearProd = yearProd;
    }

    public String getFullInfo() {
        return brand + " " + model + ", " + yearProd;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYearProd() {
        return yearProd;
    }
}
