package org.example.finapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.finapp.APIInteraction.ApiClient;
import org.example.finapp.utils.SceneUtil;

import java.awt.event.ActionEvent;
import java.io.IOException;


public class AuthController {
    @FXML
    private Button registerButton;

    @FXML
    private TextField userLogin;

    @FXML
    private PasswordField userPassword;

    @FXML
    private Button loginButton;

    ApiClient apiClient = new ApiClient();
    SceneUtil sceneUtil = new SceneUtil();

    @FXML
    public void redirectToLogin() throws IOException {
        sceneUtil.navigateTo(loginButton, "login", null);
    }

    @FXML
    public void redirectToRegister() throws IOException {
        sceneUtil.navigateTo(registerButton, "register", null);
    }


    @FXML
    private void registerUser() {
        String username = userLogin.getText();
        String password = userPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, заполните все поля");
            return;
        }

        try {
            String responseMessage = apiClient.addUser(username, password);

            showAlert("Результат регистрации", responseMessage);
            redirectToLogin();
            clearFields();
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось зарегистрировать пользователя");
            e.printStackTrace();
        }
    }

    @FXML
    private void logInUser () {
        String username = userLogin.getText();
        String password = userPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, заполните все поля");
            return;
        }

        try {
            String responseMessage = apiClient.logInUser (username, password);
            showAlert("Результат входа", responseMessage);

            // Проверяем, успешен ли вход
            if (responseMessage.equals("Успешный вход")) {
                sceneUtil.navigateTo(loginButton, "", null); // Переход только при успешном входе
                clearFields();
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось войти в систему");
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

