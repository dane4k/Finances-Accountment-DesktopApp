package org.example.finapp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.finapp.APIInteraction.ClientDiagramApi;
import org.example.finapp.utils.SceneUtil;

import java.io.IOException;
import java.util.Map;

public class HomeController implements SampleController {


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


    private String currentUsername;
    private ClientDiagramApi clientDiagramApi = new ClientDiagramApi();
    SceneUtil sceneUtil = new SceneUtil();


    /**
     * сет текущего юзера
     * @param username ник
     */
    @Override
    public void setCurrentUser(String username) {
        this.currentUsername = username;
        usernameLabel.setText(username);
        loadCharts();
    }

    /**
     * редирект на логин при выходе
     * @throws IOException
     */
    @FXML
    private void redirectToLogin() throws IOException {
        sceneUtil.navigateTo(logoutButton, "login", null);
    }

    /**
     * редирект в категории
     * @throws IOException
     */
    @FXML
    private void redirectToCategories() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/categories.fxml"));
            Parent root = loader.load();

            CategoriesController categoriesController = loader.getController();
            categoriesController.setCurrentUser(currentUsername);

            Stage stage = (Stage) categoriesButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * редирект в транзакции
     * @throws IOException
     */
    public void redirectToTransactions() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/transactions.fxml"));
            Parent root = loader.load();

            TransactionsController TransactionsController = loader.getController();
            TransactionsController.setCurrentUser(currentUsername);

            Stage stage = (Stage) transactionsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * загрузка и заполнение диаграмм
     */
    private void loadCharts() {
        try {
            Map<String, Map<String, Double>> stats = clientDiagramApi.getTransactionStats(currentUsername);

            ObservableList<PieChart.Data> expensesData = FXCollections.observableArrayList();
            Map<String, Double> expenses = stats.get("expenses");
            if (expenses != null) {
                for (Map.Entry<String, Double> entry : expenses.entrySet()) {
                    expensesData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }
            }
            expensesPieChart.setData(expensesData);
            expensesPieChart.setTitle("Расходы за последние 30 дней");

            ObservableList<PieChart.Data> incomeData = FXCollections.observableArrayList();
            Map<String, Double> income = stats.get("income");
            if (income != null) {
                for (Map.Entry<String, Double> entry : income.entrySet()) {
                    incomeData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }
            }
            incomePieChart.setData(incomeData);
            incomePieChart.setTitle("Доходы за последние 30 дней");


        } catch (IOException e) {
            sceneUtil.showAlert("Ошибка", "Ошибка при загрузке диаграмм: " + e.getMessage());
        }
    }


}