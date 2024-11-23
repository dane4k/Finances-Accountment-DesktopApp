package org.example.finapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.finapp.APIInteraction.ClientAuthApi;
import org.example.finapp.utils.SceneUtil;

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

    ClientAuthApi clientAuthApi = new ClientAuthApi();
    SceneUtil sceneUtil = new SceneUtil();

    @FXML
    public void redirectToLogin() throws IOException {
        sceneUtil.navigateTo(loginButton, "login", null);
    }

    @FXML
    public void redirectToRegister() throws IOException {
        sceneUtil.navigateTo(registerButton, "register", null);
    }

    /**
     * регистрация юзера
     */
    @FXML
    private void registerUser() {
        String username = userLogin.getText();
        String password = userPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            sceneUtil.showAlert("Ошибка", "Пожалуйста, заполните все поля");
            return;
        }

        try {
            String responseMessage = clientAuthApi.addUser(username, password);

            sceneUtil.showAlert("Результат регистрации", responseMessage);
            redirectToLogin();
            clearFields();
        } catch (Exception e) {
            sceneUtil.showAlert("Ошибка", "Не удалось зарегистрировать пользователя");
            e.printStackTrace();
        }
    }


    /**
     * вход
     */
    @FXML
    private void logInUser() {
        String username = userLogin.getText();
        String password = userPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            sceneUtil.showAlert("Ошибка", "Пожалуйста, заполните все поля");
            return;
        }

        try {
            String responseMessage = clientAuthApi.logInUser(username, password);
            sceneUtil.showAlert("Результат входа", responseMessage);

            if (responseMessage.equals("Успешный вход")) {
                sceneUtil.navigateTo(loginButton, "home", username);
                clearFields();
            }
        } catch (Exception e) {
            sceneUtil.showAlert("Ошибка", "Не удалось войти в систему");
            e.printStackTrace();
        }
    }

    /**
     * очищение полей ввода
     */
    private void clearFields() {
        userLogin.clear();
        userPassword.clear();
    }
}

