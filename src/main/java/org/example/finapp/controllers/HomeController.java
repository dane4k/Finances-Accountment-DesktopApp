package org.example.finapp.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.finapp.database.DbHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class HomeController {


    @FXML
    private Label usernameLabel;

    @FXML
    private Button categoriesButton;

    @FXML
    private Button transactionsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private PieChart expensesPieChart;

    @FXML
    private PieChart incomePieChart;


    private DbHandler dbHandler = new DbHandler();

    private String currentUsername;


    /**
     * установка имени пользователя в лейбл
     *
     * @param username имя пользователя
     */
    public void setUsername(String username) {
        if (username != null) {
            usernameLabel.setText(username);
        }
    }

    /**
     * установка имени текущего пользователя
     *
     * @param username имя пользователя
     */
    public void setCurrentUser(String username) {
        this.currentUsername = username;
        setUsername(username);
        loadCharts();

    }

    /**
     * обработчик для кнопки выхода из аккаунта
     *
     * @throws IOException исключение для лоадера
     */
    @FXML
    private void redirectToLogin() throws IOException {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1920, 1080);

        stage.setScene(scene);
    }

    /**
     * обработчик для редиректа на страницу категорий
     *
     * @throws IOException исключение для лоадера
     */
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


    /**
     * обработчик редиректа на страницу транзакций
     */
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

    /**
     * уведомление об ошибке или не ошибке
     *
     * @param title   название окна
     * @param content текст ошибки или не ошибки
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    /**
     * заполнение диаграмм доходов и расходов за 30 дней через данные из метода в DbHandler
     */
    private void loadCharts() {
        int userId;
        try (Connection connection = dbHandler.getDbConnection()) {
            try {
                userId = dbHandler.getUserIdByUsername(connection, currentUsername);
            } catch (SQLException e) {
                showAlert("Ошибка", "Пользователь не найден");
                return;
            }

            ObservableList<PieChart.Data> expensesData = dbHandler.getTransactionsData(connection, false, userId);
            expensesPieChart.setData(expensesData);
            expensesPieChart.setTitle("Расходы за последние 30 дней");

            ObservableList<PieChart.Data> incomeData = dbHandler.getTransactionsData(connection, true, userId);
            incomePieChart.setData(incomeData);
            incomePieChart.setTitle("Доходы за последние 30 дней");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при загрузке диаграмм");
        }
    }


}