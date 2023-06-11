package com.tong057.plategen;

public class Plate {
    private String plateNumber;
    private String format;
    private String voivodeship;
    private String township;
    private int cost;
    private String imagePath;

    public Plate(String plateNumber, String format, String voivodeship, String township, int cost, String imagePath) {
        this.plateNumber = plateNumber;
        this.format = format;
        this.voivodeship = voivodeship;
        this.township = township;
        this.cost = cost;
        this.imagePath = imagePath;
    }

    public Plate(String format, String voivodeship, String township, int cost) {
        this.format = format;
        this.voivodeship = voivodeship;
        this.township = township;
        this.cost = cost;
    }

    public Plate() {

    }

    public String getFullInfo() {
        return "[" + plateNumber + "]" + ", " + format + " format" + "\n" + voivodeship + " województwo, "
                + township + " " + determineTownshipOrCity() + "\nPrice: " + cost + "zł";
    }

    public String determineTownshipOrCity() {
        char[] townshipCharArray = township.toCharArray();
        int length = townshipCharArray.length;
        if(townshipCharArray[length - 3] == 's' && townshipCharArray[length - 2] == 'k' && townshipCharArray[length - 1] == 'i') {
            return "powiat";
        }
        else {
            return "";
        }
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getFormat() {
        return format;
    }

    public String getVoivodeship() {
        return voivodeship;
    }

    public String getTownship() {
        return township;
    }

    public int getCost() {
        return cost;
    }

    public String getImagePath() {
        return imagePath;
    }

}
