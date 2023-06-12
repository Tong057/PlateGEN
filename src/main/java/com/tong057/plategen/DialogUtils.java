package com.tong057.plategen;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Objects;

public class DialogUtils {

    public static void showAlert(String title, String headerText, String contentText, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(DialogUtils.class.getResource("/css/alertStyle.css")).toExternalForm());

        // Set icon at frame
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(DialogUtils.class.getResourceAsStream("/images/iconLogo.png"))));

        alert.showAndWait();
    }

}
