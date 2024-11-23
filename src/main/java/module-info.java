module org.example.finapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jbcrypt;
    requires org.json;
    requires okhttp3;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.google.gson;


    opens org.example.finapp to javafx.fxml;
    opens org.example.finapp.controllers to javafx.fxml;
    opens org.example.images;
    opens org.example.finapp.models to javafx.base;

    exports org.example.finapp.models;
    exports org.example.finapp.controllers;
    exports org.example.finapp;
}