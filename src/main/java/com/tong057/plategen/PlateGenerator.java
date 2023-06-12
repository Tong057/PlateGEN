package com.tong057.plategen;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;

import java.io.File;
import java.util.Optional;
import java.util.Random;

public class PlateGenerator {

    public static final String[] TABLE_LETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                                                   "N", "O", "P", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    public static final String[] TABLE_NUMBERS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    private static final int STANDARD_COST = 80;
    private static final int ANTIQUE_COST = 100;
    private static final int CUSTOM_COST = 1000;

    private static final Random random = new Random();

    public static Plate generatePlate(String voivodeship, String township, String townshipCode, String format) {
        int cost = 0;
        int option;
        String plateNumber = townshipCode + " ";
        do {
            switch (format) {
                case "Standardowy" -> {
                    cost = STANDARD_COST;

                    if (townshipCode.length() == 2) {
                        option = getRandomOption(5);
                        switch (option) {
                            case 1 -> plateNumber += generateNumbers(5);
                            case 2 -> plateNumber += generateNumbers(4) + generateLetters(1);
                            case 3 -> plateNumber += generateNumbers(3) + generateLetters(2);
                            case 4 -> plateNumber += generateNumbers(1) + generateLetters(1) + generateNumbers(3);
                            case 5 -> plateNumber += generateNumbers(1) + generateLetters(2) + generateNumbers(2);
                        }
                    } else if (townshipCode.length() == 3) {
                        option = getRandomOption(11);
                        switch (option) {
                            case 1 -> plateNumber += generateLetters(1) + generateNumbers(3);
                            case 2 -> plateNumber += generateNumbers(2) + generateLetters(2);
                            case 3 -> plateNumber += generateNumbers(1) + generateLetters(1) + generateNumbers(2);
                            case 4 -> plateNumber += generateNumbers(2) + generateLetters(1) + generateNumbers(1);
                            case 5 -> plateNumber += generateNumbers(1) + generateLetters(2) + generateNumbers(1);
                            case 6 -> plateNumber += generateLetters(2) + generateNumbers(2);
                            case 7 -> plateNumber += generateLetters(1) + generateNumbers(2) + generateLetters(1);
                            case 8 -> plateNumber += generateLetters(1) + generateNumbers(1) + generateLetters(2);
                            case 9 -> plateNumber += generateNumbers(5);
                            case 10 -> plateNumber += generateNumbers(4) + generateLetters(1);
                            case 11 -> plateNumber += generateNumbers(3) + generateLetters(2);
                        }
                    }

                }

                case "Zabytkowy" -> {
                    cost = ANTIQUE_COST;

                    if (townshipCode.length() == 2) {
                        option = getRandomOption(2);
                        switch (option) {
                            case 1 -> plateNumber += generateNumbers(2) + generateLetters(1);
                            case 2 -> plateNumber += generateLetters(3);
                        }
                    } else if (townshipCode.length() == 3) {
                        option = getRandomOption(3);
                        switch (option) {
                            case 1 -> plateNumber += generateNumbers(1) + generateLetters(1);
                            case 2 -> plateNumber += generateNumbers(2);
                            case 3 -> plateNumber += generateLetters(1) + generateNumbers(1);
                        }
                    }

                }

                case "Indywidualny" -> {
                    cost = CUSTOM_COST;

                    Dialog<Plate> plateDialog = new CustomPlateDialog(new Plate(format, voivodeship, township, cost), String.valueOf(townshipCode.charAt(0)));
                    Optional<Plate> result = plateDialog.showAndWait();
                    if (result.isPresent()) {
                        Plate plate = result.get();
                        // Check for non-duplication of license plate numbers
                        if(isPlateExist(plate.getPlateNumber())) {
                            DialogUtils.showAlert("Warning!", "Warning: License Plate Already Registered", "The license plate you have entered is already registered to another vehicle. Please provide a different license plate number.", Alert.AlertType.WARNING);
                            return null;
                        }
                        plate.setImagePath(PlateImageGenerator.generateImage(plate.getPlateNumber(), plate.getFormat()));
                        return plate;
                    }
                    return null;
                }

                case "Motocyklowy" -> {
                    cost = STANDARD_COST;

                    if (townshipCode.length() == 2) {
                        option = getRandomOption(8);
                        switch (option) {
                            case 1 -> plateNumber += generateNumbers(4);
                            case 2 -> plateNumber += generateNumbers(3) + generateLetters(1);
                            case 3 -> plateNumber += generateNumbers(2) + generateLetters(1) + generateNumbers(1);
                            case 4 -> plateNumber += generateNumbers(1) + generateLetters(1) + generateNumbers(2);
                            case 5 -> plateNumber += generateLetters(1) + generateNumbers(3);
                            case 6 -> plateNumber += generateNumbers(2) + generateLetters(2);
                            case 7 -> plateNumber += generateNumbers(1) + generateLetters(2) + generateNumbers(1);
                            case 8 -> plateNumber += generateLetters(2) + generateNumbers(2);
                        }
                    } else if (townshipCode.length() == 3) {
                        option = getRandomOption(8);
                        switch (option) {
                            case 1 -> plateNumber += generateLetters(1) + generateNumbers(3);
                            case 2 -> plateNumber += generateNumbers(2) + generateLetters(2);
                            case 3 -> plateNumber += generateNumbers(1) + generateLetters(1) + generateNumbers(2);
                            case 4 -> plateNumber += generateNumbers(2) + generateLetters(1) + generateNumbers(1);
                            case 5 -> plateNumber += generateNumbers(1) + generateLetters(2) + generateNumbers(1);
                            case 6 -> plateNumber += generateLetters(2) + generateNumbers(2);
                            case 7 -> plateNumber += generateNumbers(4) + generateLetters(1);
                            case 8 -> plateNumber += generateNumbers(3) + generateLetters(2);
                        }
                    }
                }

            }
        } while (isPlateExist(plateNumber)); // Check for non-duplication of license plate numbers
        return new Plate(plateNumber, format, voivodeship, township, cost, PlateImageGenerator.generateImage(plateNumber, format));
    }

    private static int getRandomOption(int countOptions) {
        return random.nextInt(countOptions) + 1;
    }

    private static String generateLetters(int count) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < count; i++) {
            builder.append(TABLE_LETTERS[random.nextInt(TABLE_LETTERS.length)]);
        }
        return builder.toString();
    }

    private static String generateNumbers(int count) {
        StringBuilder builder = new StringBuilder();
        int zeroCount = 0;

        for(int i = 0; i < count; i++) {
            int randomNumber = Integer.parseInt(TABLE_NUMBERS[random.nextInt(TABLE_NUMBERS.length)]);

            if(randomNumber == 0)
                zeroCount++;
            if(zeroCount == count)
                randomNumber++;

            builder.append(randomNumber);
        }
        return builder.toString();
    }

    private static boolean isPlateExist(String plateNumber) {
        String imageDirectory = System.getProperty("user.dir") + "/src/main/resources/images/license-plates/" + plateNumber + ".png";
        File file = new File(imageDirectory);
        return file.exists();
    }

}
