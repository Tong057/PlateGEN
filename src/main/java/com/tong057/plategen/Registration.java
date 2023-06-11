package com.tong057.plategen;

public class Registration {
    private Plate plate;
    private Vehicle vehicle;

    public Registration(Plate plate, Vehicle vehicle) {
        this.plate = plate;
        this.vehicle = vehicle;
    }

    public String getFullInfo() {
        return vehicle.getFullInfo() + "\n" + plate.getFullInfo();
    }

    public Plate getPlate() {
        return plate;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}
