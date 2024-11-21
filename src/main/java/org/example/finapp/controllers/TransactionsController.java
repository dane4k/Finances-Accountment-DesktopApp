package org.example.finapp.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.finapp.database.DbHandler;
import org.example.finapp.models.TransactionItem;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {


    @FXML
    private Label usernameLabel;


    @FXML
    private VBox addTransactionMenu;

    @FXML
    private TextField enterAmountField;

    @FXML
    private DatePicker enterDatePicker;

    @FXML
    private ComboBox<String> chooseTypeComboBox;

    @FXML
    private ComboBox<String> categoryComboBox;


    @FXML
    private Button goHomeButton;

    @FXML
    private TableView<TransactionItem> transactionsTable;

    @FXML
    private TableColumn<TransactionItem, Double> amountColumn;


    @FXML
    private TableColumn<TransactionItem, LocalDate> dateColumn;


    @FXML
    private TableColumn<TransactionItem, String> typeColumn;


    @FXML
    private TableColumn<TransactionItem, String> categoryColumn;


    @FXML
    private TableColumn<TransactionItem, String> actionsColumn;


    private String currentUsername;

    private DbHandler dbHandler = new DbHandler();

    /**
     * инициализация страницы
     * @param location для имплемента Initializable и инициализации страницы
     * @param resources для имплемента Initializable и инициализации страницы
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTransactionsTable();
    }

    /**
     * создание и заполнение таблицы данными и кнопками
     */
    private void setupTransactionsTable() {
        actionsColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button deleteButton = new Button("Удалить");

            {
                deleteButton.setStyle(
                        "-fx-background-color: red;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;"
                );
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransactionItem transaction = getTableView().getItems().get(getIndex());
                    setGraphic(deleteButton);
                    deleteButton.setOnAction(_ -> {
                        try {
                            deleteTransaction(transaction);
                            loadTransactionsFromDatabase();
                        } catch (SQLException e) {
                            showAlert("Ошибка", "Не удалось удалить транзакцию");
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }


    /**
     * обработка кнопки возврата на домашнюю страницу
     */
    public void redirectToHomeButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/home.fxml"));
            Parent root = loader.load();

            THomeController homeController = loader.getController();
            homeController.setCurrentUser(currentUsername);

            Stage stage = (Stage) goHomeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить главную страницу");
        }
    }


    /**
     * получение данных из таблицы бд transactions и заполнение таблицы fxml
     * @throws SQLException исключение DbHandler
     */
    private void loadTransactionsFromDatabase() throws SQLException {
        ObservableList<TransactionItem> transactions = FXCollections.observableArrayList();

        try (Connection connection = dbHandler.getDbConnection()) {
            List<TransactionItem> transactionList = dbHandler.fetchTransactions(connection, currentUsername);
            transactions.addAll(transactionList);

            amountColumn.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().getAmount())
            );

            dateColumn.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().getDate())
            );

            typeColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getType())
            );

            categoryColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getCategory())
            );
            transactionsTable.setItems(transactions);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить транзакции");
        }
    }


    /**
     * показать/спрятать меню добавления транзакции
     */
    @FXML
    public void toggleTransactionMenu() {
        addTransactionMenu.setVisible(!addTransactionMenu.isVisible());
    }

    /**
     * установка имени юзера в лейбл
     * @param username имя пользователя
     */
    public void setUsername(String username) {
        if (username != null) {
            usernameLabel.setText(username);
        }
    }

    /**
     * установка имени юзера в currentUsername
     * @param username имя пользователя
     */
    public void setCurrentUser(String username) {
        this.currentUsername = username;
        setUsername(username);
        List<String> categoriesToChoose = fetchCategoriesFromDatabase();
        categoryComboBox.getItems().addAll(categoriesToChoose);

        try {
            loadTransactionsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить транзакции");
        }
    }


    /**
     * заполнение списка категориями по юзернейму через DbHandler
     * @return список категорий
     */
    private List<String> fetchCategoriesFromDatabase() {
        List<String> categories = new ArrayList<>();
        try (Connection connection = dbHandler.getDbConnection();) {
            dbHandler.fetchCategories(connection, currentUsername, categories);
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка при получении категорий");
        }
        return categories;
    }


    /**
     * добавление введенной транзакции в БД
     * @throws SQLException исключение DbHandler
     */
    @FXML
    private void addTransactionToDatabase() throws SQLException {
        String amountStr = enterAmountField.getText();
        String dateStr = enterDatePicker.getValue() != null ? enterDatePicker.getValue().toString() : null;
        String typeStr = chooseTypeComboBox.getValue();
        String categoryStr = categoryComboBox.getValue();

        if (amountStr.isEmpty() || dateStr == null || typeStr == null || categoryStr == null) {
            showAlert("Ошибка", "Заполните все поля.");
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Некорректная сумма");
            return;
        }

        LocalDate date = LocalDate.parse(dateStr);


        boolean type;

        if (typeStr.equals("Доход")) {
            type = true;
        } else {
            type = false;
        }

        int userId;

        try (Connection connection = dbHandler.getDbConnection()) {
            userId = dbHandler.getUserIdByUsername(connection, currentUsername);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        int categoryId;

        try (Connection connection = dbHandler.getDbConnection()) {
            categoryId = dbHandler.getCategoryIdByCategoryName(connection, categoryStr);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }


        try (Connection connection = dbHandler.getDbConnection()) {
            dbHandler.addTransaction(connection, userId, amount, date, type, categoryId);
            showAlert("a", "транзакция добавлена");
            clearAllInputFields();
            loadTransactionsFromDatabase();
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "ошибка");
        }

    }

    /**
     * очистка полей ввода и выбора после добавления транзакции
     */
    private void clearAllInputFields() {
        enterAmountField.clear();
        enterDatePicker.setValue(null);
        chooseTypeComboBox.getSelectionModel().clearSelection();
        categoryComboBox.getSelectionModel().clearSelection();
    }

    /**
     * уведомление
     * @param title название
     * @param content содержание
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * обработчик удаления транзакции
     * @param transaction объект транзакции TransactionItem
     * @throws SQLException исключение DbHandler
     */
    private void deleteTransaction(TransactionItem transaction) throws SQLException {
        try (Connection connection = dbHandler.getDbConnection()) {
            dbHandler.deleteTransaction(connection, currentUsername, transaction);
        }
    }

}

