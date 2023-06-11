package com.tong057.plategen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.DialogPane;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class DialogController {
    @FXML
    private ImageView standardPlateImageView;

    @FXML
    private DialogPane dialogPane;

    @FXML
    private ButtonType okButtonType;

    @FXML
    private ButtonType cancelButtonType;


    @FXML
    private void initialize() throws IOException {

        Image image = new Image(Objects.requireNonNull(BaseWindowController.class.getResourceAsStream("/images/PlateDefault.png")));
        standardPlateImageView.setImage(image);

    }

    @FXML
    private void onOKClicked(ActionEvent event) {
        // Обработчик события для кнопки OK
        // Здесь можно добавить код для выполнения действий при нажатии кнопки OK
        closeDialog(ButtonType.OK);
    }

    @FXML
    private void onCancelClicked(ActionEvent event) {
        // Обработчик события для кнопки Cancel
        // Здесь можно добавить код для выполнения действий при нажатии кнопки Cancel
        closeDialog(ButtonType.CANCEL);
    }

    private void closeDialog(ButtonType buttonType) {
        // Закрытие диалогового окна
        Optional<ButtonType> result = dialogPane.getButtonTypes().stream()
                .filter(type -> type.equals(buttonType))
                .findFirst();

        if (result.isPresent()) {
            ButtonType selectedButtonType = result.get();
            Button button = (Button) dialogPane.lookupButton(selectedButtonType);
            button.getScene().getWindow().hide();
        }
    }
}

