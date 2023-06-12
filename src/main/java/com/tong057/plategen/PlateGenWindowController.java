package com.tong057.plategen;

import javafx.collections.FXCollections;
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
import javafx.stage.Stage;
import java.net.URL;
import java.time.Year;
import java.util.*;

public class PlateGenWindowController implements Initializable {
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
        voivodeshipsTownships = ExcelReader.readVoivodeshipsAndTownshipsWithCodes();
        registrationList = ExcelReader.readRegistrations();
        setScene();
        initializeUI();
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        savePlateButton.setOnMouseClicked(event -> {
            if (plateImageView != null)
                ImageUtils.saveImage(primaryStage, plateImageView.getImage());
        });

        removeAllPlatesButton.setOnMouseClicked(event -> {
            for (Registration registration : registrationList) {
                ImageUtils.deleteImage(registration.getPlate().getImagePath());
            }
            registrationList.clear();
            setRegistrationsToScrollPane();
            plateImageView.setImage(null);
        });

        generatePlateButton.setOnAction(actionEvent -> {
            if (isFieldsIncomplete()) {
                DialogUtils.showAlert("Error!", "Error: Incomplete Fields", "Some fields are not filled in. Please fill in all the required fields and try again.", Alert.AlertType.ERROR);
            } else if (isInvalidYear()) {
                DialogUtils.showAlert("Error!", "Error: Invalid Year Entered", "The year you have entered is invalid. Please provide a valid year.", Alert.AlertType.ERROR);
            } else if (isIncompatiblePlate()) {
                DialogUtils.showAlert("Error!", "Error: Incompatible License Plate", "The vehicle you specified is too new to accommodate a retro-style license plate. We apologize for any inconvenience caused.", Alert.AlertType.ERROR);
            } else {
                registerVehicle();
            }
        });
    }

    private boolean isFieldsIncomplete() {
        return brandVehicleTF.getText().isEmpty() || modelVehicleTF.getText().isEmpty() ||
                yearProdVehicleTF.getText().isEmpty() || formatPlateComboBox.getValue() == null ||
                voivodeshipComboBox.getValue() == null || townshipComboBox.getValue() == null;
    }

    private boolean isInvalidYear() {
        int yearProd = Integer.parseInt(yearProdVehicleTF.getText());
        return yearProd < 1900 || yearProd > Year.now().getValue();
    }

    private boolean isIncompatiblePlate() {
        int yearProd = Integer.parseInt(yearProdVehicleTF.getText());
        String format = formatPlateComboBox.getValue();
        return yearProd > 1998 && format.equals("Zabytkowy");
    }
    private void setScene() {
        setSceneCreatePlate(true);
        createPlateButton.setOnMouseClicked(Event -> setSceneCreatePlate(true));

        plateListButton.setOnMouseClicked(mouseEvent -> {
            setSceneCreatePlate(false);
            setRegistrationsToScrollPane();
        });
    }

    private void setSceneCreatePlate(boolean isVisible) {
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
            brandVehicleTF.setText("");
            modelVehicleTF.setText("");
            yearProdVehicleTF.setText("");
            setPlateImage(registration);
            DialogUtils.showAlert("Notification", "Success! The car has been registered.", registration.getFullInfo(), Alert.AlertType.INFORMATION);
            registrationList.add(registration);

        }
    }

    private void setComboBoxSettings() {
        setVoivodeshipComboBox();
        setTownshipComboBox();
        setFormatPlateComboBox();
    }

    private void setVoivodeshipComboBox() {
        // ArrayList with loaded data from Excel
        List<String> voivodeships = new ArrayList<>(voivodeshipsTownships.keySet());
        // Sorting list of voivodeships using comparator
        voivodeships.sort(FormattingUtils.polishComparator);

        voivodeshipComboBox.setItems(FXCollections.observableArrayList(voivodeships));
        voivodeshipComboBox.setVisibleRowCount(6);
        voivodeshipComboBox.setEditable(false);

        voivodeshipComboBox.setOnAction(event -> updateTownshipComboBox());
    }

    public void updateTownshipComboBox() {
        String selectedVoivodeship = voivodeshipComboBox.getValue();
        townshipsCodes = voivodeshipsTownships.get(selectedVoivodeship);
        if (townshipsCodes != null) {
            List<String> townships = new ArrayList<>(townshipsCodes.keySet());
            townships.sort(FormattingUtils.polishComparator);

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

    private void setTextFieldSettings() {
        brandVehicleTF.setOnMouseClicked(actionEvent -> brandVehicleTF.setTextFormatter(FormattingUtils.setLimitedTextFormatter()));
        modelVehicleTF.setOnMouseClicked(actionEvent -> modelVehicleTF.setTextFormatter(FormattingUtils.setLimitedTextFormatter()));
        yearProdVehicleTF.setOnMouseClicked(actionEvent -> yearProdVehicleTF.setTextFormatter(FormattingUtils.setNumericTextFormatter()));
    }

    private void setLogo(ImageView logoImageView) {
        String imagePath = "/images/MenuLogo.png";
        Image image = new Image(Objects.requireNonNull(PlateGenWindowController.class.getResourceAsStream(imagePath)));
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

            hBox.setOnMouseEntered(event -> hBox.setStyle("-fx-background-color: #1a1e22;"));
            hBox.setOnMouseExited(event -> hBox.setStyle("-fx-background-color: #14161a;"));

            hBox.setOnMouseClicked(event -> setPlateImage(registration));

            Button removeButton = createButton();
            removeButton.setOnAction(event -> {
                ImageUtils.deleteImage(plate.getImagePath());
                int selectedIndex = registrationsListVBox.getChildren().indexOf(hBox); // index of selected element

                registrationList.remove(registration);
                registrationsListVBox.getChildren().remove(hBox);

                if (selectedIndex >= 0 && selectedIndex < registrationList.size()) {
                    setPlateImage(registrationList.get(selectedIndex));
                } else if (registrationList.isEmpty()) {
                    plateImageView.setImage(null);
                } else if (selectedIndex >= registrationList.size()) {
                    setPlateImage(registrationList.get(registrationList.size() - 1));
                }

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

    private Button createButton() {
        Button button = new Button("Remove");
        button.getStyleClass().add("button");
        return button;
    }

}

