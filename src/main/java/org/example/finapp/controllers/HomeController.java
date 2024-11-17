package org.example.finapp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController {


    @FXML
    private Label usernameLabel;

    @FXML
    private Button categoriesButton;

    @FXML
    private Button transactionsButton;


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
    private void redirectToCategories() throws IOException {
        Stage stage = (Stage) categoriesButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/categories.fxml"));
        Parent root = loader.load();

        CategoriesController categoriesController = loader.getController();

        categoriesController.setCurrentUser(usernameLabel.getText());


        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
    }


    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Если currentUsername уже установлен, обновим Label
        if (currentUsername != null) {
            setUsername(currentUsername);
        }
    }


}