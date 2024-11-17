module org.example.finapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires jbcrypt;


    opens org.example.finapp to javafx.fxml;
    opens org.example.finapp.controllers to javafx.fxml;
    opens org.example.images;
    exports org.example.finapp;
}