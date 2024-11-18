package org.example.finapp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {


    @FXML
    private Label usernameLabel;

    @FXML
    private Button categoriesButton;

    @FXML
    private Button transactionsButton;

    @FXML
    private Button logoutButton;


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
    private void redirectToLogin() throws IOException {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1920, 1080);

        stage.setScene(scene);
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

    public void redirectToTransactions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/transactions.fxml"));
            Parent root = loader.load();

            TransactionsController transactionsController = loader.getController();


            if (currentUsername != null) {
                transactionsController.setCurrentUser(currentUsername);
            } else {
                System.out.println("юзернейм null");
            }

            Stage stage = (Stage) transactionsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить страницу транзакций");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}