package com.tong057.plategen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AppWindow.fxml"));
            Parent root = loader.load();

            BaseWindowController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("PlateGEN");
            Image icon = new Image(getClass().getResourceAsStream("/images/iconLogo.png"));
            primaryStage.getIcons().add(icon);
            primaryStage.setResizable(false);
            setUserAgentStylesheet(STYLESHEET_MODENA);
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> {
                // Вызываем метод для записи регистраций в Excel
                ExcelWriter.writeRegistrationsToExcel(BaseWindowController.registrationList);
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}