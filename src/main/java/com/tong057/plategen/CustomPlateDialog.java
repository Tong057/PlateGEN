package com.tong057.plategen;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.util.Objects;
import java.util.function.UnaryOperator;

public class CustomPlateDialog extends Dialog<Plate> {

    private Plate plate;
    private char voivodeshipLetter;
    private TextField plateNumberTF;

    public CustomPlateDialog(Plate plate, char voivodeshipLetter) {
        super();
        this.plate = plate;
        this.voivodeshipLetter = voivodeshipLetter;
        buildUI();
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return createPlateFromInput();
            }
            return null;
        });
    }

    private void buildUI() {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.TOP_CENTER);

        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/PlateDefault.png"))));
        imageView.setFitWidth(imageView.getImage().getWidth() * 0.3);
        imageView.setFitHeight(imageView.getImage().getHeight() * 0.3);

        Label topInfoLabel = new Label("Enter letters and numbers to create your custom plate ↓");
        topInfoLabel.setStyle("-fx-font-size: 14px; -fx-font-family: 'Consolas';");

        plateNumberTF = new TextField();
        plateNumberTF.setPromptText("Enter plate");
        plateNumberTF.setMaxWidth(100);
        plateNumberTF.setAlignment(Pos.CENTER);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            // Replace the first character with voivodeshipLetter
            if (newText.length() == 1) {
                char firstChar = newText.charAt(0);
                change.setText(change.getText().replace(Character.toString(firstChar), Character.toString(voivodeshipLetter)));
            }

            // Check the second character (should be a digit)
            if (newText.length() == 2) {
                char secondChar = newText.charAt(1);
                if (!Character.isDigit(secondChar)) {
                    change.setText(change.getText().replace(Character.toString(secondChar), ""));
                }
            }

            // Replace the third character with a space
            if (newText.length() == 3) {
                char thirdChar = newText.charAt(2);
                change.setText(change.getText().replace(Character.toString(thirdChar), " "));
            }

            // Limit the total number of characters
            if (newText.length() > 8) {
                return null;
            }

            return change;
        };

        // Convert the value for display in the text field
        StringConverter<String> converter = new DefaultStringConverter();

        // Apply input character restrictions and conversion
        TextFormatter<String> textFormatter = new TextFormatter<>(converter, null, filter);
        plateNumberTF.setTextFormatter(textFormatter);

        vbox.getChildren().addAll(imageView, topInfoLabel, plateNumberTF);
        getDialogPane().setContent(vbox);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox expandableContent = new VBox();
        expandableContent.setPadding(new Insets(10));
        expandableContent.setAlignment(Pos.BOTTOM_CENTER);

        Label expandableText = new Label("*First char is a default letter, second char must be a number 0-9");
        expandableText.setStyle("-fx-font-size: 12px; -fx-font-family: 'Consolas';");

        expandableContent.getChildren().add(expandableText);
        getDialogPane().setExpandableContent(expandableContent);
        getDialogPane().setExpanded(false);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/iconLogo.png"))));
        stage.setTitle("Create custom plate");

        stage.setWidth(480);
        stage.setHeight(200);
    }

    private Plate createPlateFromInput() {
        String plateNumber = plateNumberTF.getText();
        if (plateNumber == null || plateNumber.trim().isEmpty() || plateNumber.length() < 6) {
            showAlert("Error", "Invalid Plate Number", "Plate number must be at least 5 characters long.");
            return null; // Return null if the plate number is empty or too short
        }

        if (plateNumber.length() < 7)
            plateNumber = plateNumber.replace(" ", "  ");
        plate.setPlateNumber(plateNumber.toUpperCase());

        return plate;
    }

    private void showAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();

//        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
//        alertStage.setOnCloseRequest(event -> {
//            // Do nothing
//        });
    }
}