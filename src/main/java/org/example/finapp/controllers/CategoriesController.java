package org.example.finapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class CategoriesController {


    @FXML
    private Label usernameLabel;

    private String currentUsername;

    public void setUsername(String username) {
        if (username != null) {
            usernameLabel.setText(username);
        }
    }

    public void setCurrentUser(String username) {
        this.currentUsername = username;
        setUsername(username);
    }



    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Если currentUsername уже установлен, обновим Label
        if (currentUsername != null) {
            setUsername(currentUsername);
        }
    }
}
