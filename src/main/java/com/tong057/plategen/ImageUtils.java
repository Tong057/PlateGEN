package com.tong057.plategen;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ImageUtils {

    public static void saveImage(Stage primaryStage, Image image) {
        if (image != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg"));
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                try {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    ImageIO.write(bufferedImage, "png", file);
                    System.out.println("Image has been saved: " + file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteImage(String fileName) {
        String projectDirectory = System.getProperty("user.dir"); // Get the current project directory
        String filePath = projectDirectory + "/src/main/resources/images/license-plates/" + fileName + ".png";

        try {
            Files.deleteIfExists(Paths.get(filePath));
            System.out.println("File deleted successfully.");
        } catch (IOException e) {
            System.out.println("Error deleting the file: " + e.getMessage());
        }
    }

}
