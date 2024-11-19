package org.example.finapp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.finapp.database.DbHandler;

import java.io.IOException;

public class RegisterController {
    @FXML
    private Button registerButton;

    @FXML
    private TextField userLogin;

    @FXML
    private PasswordField userPassword;

    @FXML
    private Button loginButton;

    private DbHandler dbHandler = new DbHandler();

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> registerUser());

    }

    /**
     * обработчик логина
     * @throws IOException
     */
    @FXML
    private void handleLoginButtonClick() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);

    }

    private void registerUser() {
        String username = userLogin.getText();
        String password = userPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, заполните все поля");
            return;
        }

        if (!dbHandler.isUsernameUnique(username)) {
            showAlert("Ошибка", "Пользователь уже существует");
            return;
        }


        try {
            dbHandler.addUser(username, password);
            showAlert("Успех", "Пользователь зарегистрирован");
            clearFields();
            handleLoginButtonClick();
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось зарегистрировать пользователя");
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