package com.tong057.plategen;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.*;

public class BaseWindowController implements Initializable {
    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private Map<String, Map<String, String>> voivodeshipsTownships;
    private Map<String, String> townshipsCodes;
    public static List<Registration> registrationList;

    @FXML
    private Button removeAllPlatesButton;

    @FXML
    private Button savePlateButton;

    @FXML
    private ImageView logoImageView;
    @FXML
    private Button createPlateButton;
    @FXML
    private Button plateListButton;
    @FXML
    private ImageView plateImageView;
    @FXML
    private Rectangle standardPlateRectangle;
    @FXML
    private Rectangle motorcyclePlateRectangle;

    @FXML
    private AnchorPane createPlatePane;
    @FXML
    private TextField brandVehicleTF;
    @FXML
    private TextField modelVehicleTF;
    @FXML
    private TextField yearProdVehicleTF;
    @FXML
    private ComboBox<String> formatPlateComboBox;
    @FXML
    private ComboBox<String> townshipComboBox;
    @FXML
    private ComboBox<String> voivodeshipComboBox;
    @FXML
    private Button generatePlateButton;

    @FXML
    private AnchorPane plateListPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox registrationsListVBox;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialize called!");
        setScene();
        voivodeshipsTownships = ExcelReader.readVoivodeshipsAndTownshipsWithCodes();
        registrationList = ExcelReader.readRegistrations();
        initializeUI();

        savePlateButton.setOnMouseClicked(event -> {
            if(plateImageView != null)
                ImageUtils.saveImage(primaryStage, plateImageView.getImage());
        });

        removeAllPlatesButton.setOnMouseClicked(event -> {
            for(int i = 0; i < registrationList.size(); ++i) {
                ImageUtils.deleteImage(registrationList.get(i).getPlate().getPlateNumber());
            }
            registrationList.clear();
            setRegistrationsToScrollPane();
            plateImageView.setImage(null);
        });

        generatePlateButton.setOnAction(actionEvent -> registerVehicle());
    }

    public void setScene() {
        setSceneCreatePlate(true);
        createPlateButton.setOnMouseClicked(mouseEvent -> setSceneCreatePlate(true));

        plateListButton.setOnMouseClicked(mouseEvent -> {
            setSceneCreatePlate(false);
            setRegistrationsToScrollPane();
        });
    }

    public void setSceneCreatePlate(boolean isVisible) {
        createPlatePane.setVisible(isVisible);
        plateListPane.setVisible(!isVisible);
        savePlateButton.setVisible(!isVisible);
        removeAllPlatesButton.setVisible(!isVisible);
        plateImageView.setImage(null);
    }

    private void initializeUI() {
        motorcyclePlateRectangle.setVisible(false);
        setLogo(logoImageView);
        setComboBoxSettings();
        setTextFieldSettings();
    }

    private void registerVehicle() {
        String brand = brandVehicleTF.getText();
        String model = modelVehicleTF.getText();
        int yearProd = Integer.parseInt(yearProdVehicleTF.getText());
        Vehicle vehicle = new Vehicle(brand, model, yearProd);

        String voivodeship = voivodeshipComboBox.getValue();
        String township = townshipComboBox.getValue();
        String townshipCode = townshipsCodes.get(township);
        String format = formatPlateComboBox.getValue();
        Plate plate = PlateGenerator.generatePlate(voivodeship, township, townshipCode, format);

        if(plate != null) {
            Registration registration = new Registration(plate, vehicle);
            showNotification(registration.getFullInfo());
            registrationList.add(registration);
            setPlateImage(registration);

        }
    }

    private void showNotification(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText("Success! The car has been registered.");
        alert.getDialogPane().setStyle("-fx-font-size: 14px; -fx-font-family: Swis721 Hv BT;");
        alert.setContentText(text);
        alert.showAndWait();
    }

    public void setComboBoxSettings() {
        setVoivodeshipComboBox();
        setTownshipComboBox();
        setFormatPlateComboBox();
    }

    private void setVoivodeshipComboBox() {
        // Creating voivodeship`s ArrayList with loaded data from Excel
        List<String> voivodeships = new ArrayList<>(voivodeshipsTownships.keySet());
        // Sorting list of voivodeships using comparator
        voivodeships.sort(polishComparator);

        voivodeshipComboBox.setItems(FXCollections.observableArrayList(voivodeships));
        voivodeshipComboBox.setVisibleRowCount(6);
        voivodeshipComboBox.setEditable(false);

        // Voivodeship selection event
        voivodeshipComboBox.setOnAction(event -> updateTownshipComboBox());
    }

    public void updateTownshipComboBox() {
        String selectedVoivodeship = voivodeshipComboBox.getValue();
        townshipsCodes = voivodeshipsTownships.get(selectedVoivodeship);
        if (townshipsCodes != null) {
            List<String> townships = new ArrayList<>(townshipsCodes.keySet());
            townships.sort(polishComparator);

            // Updating the list of counties in the ComboBox
            townshipComboBox.setItems(FXCollections.observableArrayList(townships));
        }
    }

    private void setTownshipComboBox() {
        townshipComboBox.setEditable(false);
        townshipComboBox.setVisibleRowCount(4);
    }

    private void setFormatPlateComboBox() {
        formatPlateComboBox.setEditable(false);
        formatPlateComboBox.setItems(FXCollections.observableArrayList("Standardowy", "Zabytkowy", "Indywidualny", "Motocyklowy"));

        formatPlateComboBox.setOnAction(event -> {
            plateImageView.setImage(null);
            setStandardPlateRectangle(!formatPlateComboBox.getValue().equals("Motocyklowy"));
        });
    }

    public void setTextFieldSettings() {
        yearProdVehicleTF.setOnMouseClicked(actionEvent -> yearProdVehicleTF.setTextFormatter(textFormatter));
    }

    // Comparator considering Polish letters
    Comparator<String> polishComparator = (s1, s2) -> {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("pl-PL"));
        return collator.compare(s1, s2);
    };

    TextFormatter<Integer> textFormatter = new TextFormatter<>(
            new IntegerStringConverter(),
            0,
            change -> (change.getControlNewText().matches("\\d*") && change.getControlNewText().length() <= 4) ? change : null
    );

    public void setLogo(ImageView logoImageView) {
        String imagePath = "/images/MenuLogo.png";
        Image image = new Image(Objects.requireNonNull(BaseWindowController.class.getResourceAsStream(imagePath)));
        logoImageView.setImage(image);
    }

    public void setPlateImage(Registration registration) {
        Image plateImage = new Image(Objects.requireNonNull(registration.getPlate().getImagePath()));
        plateImageView.setImage(plateImage);
        if(registration.getPlate().getFormat().equals("Motocyklowy")) {
            setStandardPlateRectangle(false);
            plateImageView.setX(150);
        }
        else {
            setStandardPlateRectangle(true);
            plateImageView.setX(0);
        }

    }

    public void setStandardPlateRectangle(boolean isVisible) {
        standardPlateRectangle.setVisible(isVisible);
        motorcyclePlateRectangle.setVisible(!isVisible);
    }

    public void setRegistrationsToScrollPane() {
        registrationsListVBox.getChildren().clear();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        for (Registration registration : registrationList) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setSpacing(10.0);
            hBox.setStyle("-fx-background-color: #14161a;");
            hBox.setPadding(new Insets(5, 0, 5, 25));
            hBox.getStyleClass().add("hbox");

            Vehicle vehicle = registration.getVehicle();
            Plate plate = registration.getPlate();

            TextFlow textFlow1 = new TextFlow();
            textFlow1.setPrefHeight(54.0);
            textFlow1.setPrefWidth(97.0);

            Text text1 = new Text(vehicle.getModel() + "\n");
            Text text2 = new Text(vehicle.getBrand() + "\n");
            Text text3 = new Text(String.valueOf(vehicle.getYearProd()));

            setElementStyles(text1, text2, text3);

            textFlow1.getChildren().addAll(text1, text2, text3);

            Label label1 = createLabel(plate.getVoivodeship(), 137.0);
            Label label2 = createLabel(plate.getTownship(), 113.0);
            Label label3 = createLabel(plate.getFormat(), 112.0);
            Label label4 = createLabel(plate.getCost() + " zł", 58.0);

            hBox.getChildren().addAll(textFlow1, label1, label2, label3, label4);

            registrationsListVBox.getChildren().add(hBox);

            int index = registrationList.indexOf(registration);

            hBox.setOnMouseEntered(event -> hBox.setStyle("-fx-background-color: #1a1e22;"));
            hBox.setOnMouseExited(event -> hBox.setStyle("-fx-background-color: #14161a;"));

            hBox.setOnMouseClicked(event -> {
                setPlateImage(registration);
                setRegistrationsToScrollPane(); // Refresh the list display
            });

            Button removeButton = createButton("Remove", "button");
            removeButton.setOnAction(event -> {
                ImageUtils.deleteImage(plate.getPlateNumber());
                registrationList.remove(registration);

                registrationsListVBox.getChildren().remove(hBox);

                if (registrationList.size() != index)
                    setPlateImage(registrationList.get(index));
                else if (registrationList.size() == 0) {
                    plateImageView.setImage(null);
                } else
                    setPlateImage(registrationList.get(index - 1));

                event.consume(); // Prevent event propagation to HBox
            });

            hBox.getChildren().add(removeButton);
        }

        scrollPane.setContent(registrationsListVBox);
    }

    private void setElementStyles(Text... texts) {
        for (Text text : texts) {
            text.setFill(Color.valueOf("#e7e5e5"));
            text.setFont(Font.font("Franklin Gothic Medium", 16.0));
        }
    }

    private Label createLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefHeight(18.0);
        label.setPrefWidth(width);
        label.setTextFill(Color.valueOf("#e7e5e5"));
        label.setAlignment(Pos.CENTER_LEFT);
        label.setFont(Font.font("Franklin Gothic Medium", 16.0));
        return label;
    }

    private Button createButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }

}

