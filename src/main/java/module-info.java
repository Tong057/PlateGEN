module com.example.plategen {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi;
    requires poi.ooxml;
    requires java.desktop;
    requires javafx.swing;



    opens com.tong057.plategen to javafx.fxml;
    exports com.tong057.plategen;
}