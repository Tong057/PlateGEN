package com.tong057.plategen;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Objects;

public class CustomPlateDialog extends Dialog<Plate> {

    private final Plate plate;
    private TextField plateNumberTF;

    public CustomPlateDialog(Plate plate, String voivodeshipLetter) {
        super();
        this.plate = plate;
        buildUI(voivodeshipLetter);
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return createPlateFromInput();
            }
            return null;
        });
    }

    private Plate createPlateFromInput() {
        String plateNumber = plateNumberTF.getText();
        if (plateNumber == null || plateNumber.trim().isEmpty() || plateNumber.length() < 6) {
            DialogUtils.showAlert("Error", "Invalid Plate Number", "Plate number must be at least 5 characters long.", Alert.AlertType.ERROR);
            return null; // Return null if the plate number is empty or too short
        }

        if (plateNumber.length() < 7)
            plateNumber = plateNumber.replace(" ", "  ");
        plate.setPlateNumber(plateNumber.toUpperCase());

        return plate;
    }

    private void buildUI(String voivodeshipLetter) {
        VBox vbox = createMainVBox();
        ImageView imageView = createImageView();
        Label topInfoLabel = createTopInfoLabel();
        plateNumberTF = createPlateNumberTextField();
        applyInputRestrictions(plateNumberTF, voivodeshipLetter);
        vbox.getChildren().addAll(imageView, topInfoLabel, plateNumberTF);
        configureDialogPane(vbox);

        VBox expandableContent = createExpandableContent();
        Label expandableText = createExpandableTextLabel();
        expandableContent.getChildren().add(expandableText);
        configureExpandableContent(expandableContent);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/iconLogo.png"))));
        stage.setTitle("Create custom plate");
        stage.setWidth(480);
        stage.setHeight(200);
    }

    private VBox createMainVBox() {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.TOP_CENTER);
        return vbox;
    }

    private ImageView createImageView() {
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/PlateDefault.png"))));
        imageView.setFitWidth(imageView.getImage().getWidth() * 0.3);
        imageView.setFitHeight(imageView.getImage().getHeight() * 0.3);
        return imageView;
    }

    private Label createTopInfoLabel() {
        Label topInfoLabel = new Label("Enter letters and numbers to create your custom plate ↓");
        topInfoLabel.setStyle("-fx-font-size: 14px; -fx-font-family: 'Consolas';");
        return topInfoLabel;
    }

    private TextField createPlateNumberTextField() {
        TextField textField = new TextField();
        textField.setPromptText("Enter plate");
        textField.setMaxWidth(100);
        textField.setAlignment(Pos.CENTER);
        return textField;
    }

    private void applyInputRestrictions(TextField textField, String voivodeshipLetter) {
        textField.setTextFormatter(FormattingUtils.setCustomPlateTextFormatter(voivodeshipLetter));
    }

    private void configureDialogPane(VBox vbox) {
        getDialogPane().setContent(vbox);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }

    private VBox createExpandableContent() {
        VBox expandableContent = new VBox();
        expandableContent.setPadding(new Insets(10));
        expandableContent.setAlignment(Pos.BOTTOM_CENTER);
        return expandableContent;
    }

    private Label createExpandableTextLabel() {
        Label expandableText = new Label("*First char is a default letter, second char must be a number 0-9");
        expandableText.setStyle("-fx-font-size: 12px; -fx-font-family: 'Consolas';");
        return expandableText;
    }

    private void configureExpandableContent(VBox expandableContent) {
        getDialogPane().setExpandableContent(expandableContent);
        getDialogPane().setExpanded(false);
    }

}