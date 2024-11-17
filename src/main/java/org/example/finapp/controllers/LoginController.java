package org.example.finapp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.finapp.database.DbHandler;

import java.io.IOException;

public class LoginController {


    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private TextField userLogin;

    @FXML
    private PasswordField userPassword;


    private DbHandler dbHandler = new DbHandler();


    @FXML
    private void handleRegisterButtonClick() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/register.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
    }

    @FXML
    private void logInUser() {
        String username = userLogin.getText();
        String password = userPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, заполните все поля");
            return;
        }

        try {
            if (dbHandler.checkPassword(username, password)) {
                showAlert("Успех", "Вход выполнен");
                clearFields();
            } else {
                showAlert("Ошибка", "Неверное имя пользователя или пароль");

            }

        } catch (Exception e) {
            showAlert("Ошибка", "хз");

            e.printStackTrace();
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        userLogin.clear();
        userPassword.clear();
    }

}
