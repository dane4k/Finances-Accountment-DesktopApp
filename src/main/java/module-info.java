module org.example.finapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires jbcrypt;
    requires org.json;
    requires okhttp3;


    opens org.example.finapp to javafx.fxml;
    opens org.example.finapp.controllers to javafx.fxml;
    opens org.example.images;
    opens org.example.finapp.models to javafx.base;


    exports org.example.finapp;
}