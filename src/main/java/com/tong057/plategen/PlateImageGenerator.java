package com.tong057.plategen;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PlateImageGenerator {

    public static String generateImage(String plateNumber, String format) {
        String plateFramePath = null;
        double imageWidth;
        double imageHeight;

        if (format.equals("Motocyklowy")) {
            imageWidth = 953;
            imageHeight = 717;
        } else {
            imageWidth = 1500;
            imageHeight = 329;
        }

        switch (format) {
            case "Standardowy", "Indywidualny" -> plateFramePath = "/images/PlateDefault.png";
            case "Zabytkowy" -> plateFramePath = "/images/PlateRetro.png";
            case "Motocyklowy" -> plateFramePath = "/images/PlateBicycle.png";
        }

        // Load the plate frame image
        ImageView plateFrame = new ImageView(new Image(Objects.requireNonNull(PlateImageGenerator.class.getResource(plateFramePath)).toExternalForm()));

        // Create the plate text
        Text textNode = createPlateText(plateNumber, format);

        // Combine the frame and text
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(plateFrame, textNode);

        // Create a scene with the specified dimensions
        javafx.scene.Scene scene = new javafx.scene.Scene(stackPane, imageWidth, imageHeight);

        // Create the image
        WritableImage image = new WritableImage((int) imageWidth, (int) imageHeight);
        scene.snapshot(image);

        // Save the image to a file
        String absolutePath = null;
        try {
            String projectDirectory = System.getProperty("user.dir"); // Get the current project directory
            String imageDirectory = projectDirectory + "/src/main/resources/images/license-plates/"; // Specify the "images" directory within the project

            // Create the "images" directory if it doesn't exist
            File directory = new File(imageDirectory);
            if (!directory.exists()) {
                directory.mkdir();
            }

            String fileName = plateNumber + ".png"; // Specify the file name

            File outputFile = new File(imageDirectory + fileName); // Set the output file path
            absolutePath = outputFile.getAbsolutePath();
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
            System.out.println("License plate image generated successfully. Path: " + absolutePath + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred while generating the image: " + e.getMessage());
        }
        return absolutePath;
    }

    private static Text createPlateText(String plateNumber, String format) {
        String fontPath;

        if(format.equals("Zabytkowy"))
            plateNumber = plateNumber + "("; // ( - retro car image in font
        else if(format.equals("Motocyklowy"))
            plateNumber = toMotorcycleTextFormat(plateNumber);

        if(format.equals("Standardowy") && plateNumber.length() == 9)
            fontPath = "/fonts/arklatrs_dense.ttf";
        else
            fontPath = "/fonts/arklatrs.ttf";

        Text textNode = new Text(plateNumber.toLowerCase());
        try {
            // Load the font from the "resources" folder
            Font font = Font.loadFont(Objects.requireNonNull(PlateImageGenerator.class.getResource(fontPath)).toExternalForm(), 308);
            textNode.setFont(font);
        } catch (Exception e) {
            // Handle the error if the font failed to load
            System.out.println("Error loading the font: " + e.getMessage());
        }

        double x;
        double y = 10;
        if(format.equals("Motocyklowy")) {
            textNode.setLineSpacing(-40.0);
            x = 0;
        }
        else
            x = 60;

        textNode.setTranslateX(x);
        textNode.setTranslateY(y);
        textNode.setFill(Color.BLACK);
        return textNode;
    }

    private static String toMotorcycleTextFormat(String plateNumber) {
        char[] plateNumberChar = plateNumber.toCharArray();

        for(int i = 0; i < plateNumberChar.length; ++i) {
            if(plateNumberChar[i] == ' ')
                plateNumberChar[i] = '\n';
        }
        return "  " + new String(plateNumberChar);
    }

}
