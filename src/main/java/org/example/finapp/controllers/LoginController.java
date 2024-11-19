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
    private TextField userLogin;

    @FXML
    private PasswordField userPassword;


    private DbHandler dbHandler = new DbHandler();


    /**
     * Редирект на регистрацию
     *
     * @throws IOException исключение для лоадера
     */
    @FXML
    private void handleRegisterButtonClick() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/register.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
    }


    /**
     * обработка события входа юзера в систему
     */
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
                redirectToHome();
            } else {
                showAlert("Ошибка", "Неверное имя пользователя или пароль");

            }

        } catch (Exception e) {
            showAlert("Ошибка", "хз");

            e.printStackTrace();
        }
    }

    /**
     * редирект на домашнюю страницу
     *
     * @throws IOException исключение для лоадера
     */
    private void redirectToHome() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/home.fxml"));
        Parent root = loader.load();

        HomeController homeController = loader.getController();

        homeController.setCurrentUser(userLogin.getText());


        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
    }

    /**
     * алерт для ошибки или не ошибки
     *
     * @param title   название окна алерта
     * @param content содержание ошибки или уведомления
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
