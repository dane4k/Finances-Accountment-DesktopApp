package org.example.finapp.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.finapp.APIInteraction.ClientCategoryApi;
import org.example.finapp.APIInteraction.ClientTransactionApi;
import org.example.finapp.models.CategoryDTO;
import org.example.finapp.models.TransactionDTO;

import java.io.IOException;
import java.util.List;

public class TransactionsController {

    @FXML
    private TableView<TransactionDTO> transactionsTable;
    @FXML
    private TableColumn<TransactionDTO, String> amountColumn;
    @FXML
    private TableColumn<TransactionDTO, String> dateColumn;
    @FXML
    private TableColumn<TransactionDTO, String> typeColumn;
    @FXML
    private TableColumn<TransactionDTO, String> categoryColumn;
    @FXML
    private TableColumn<TransactionDTO, String> actionsColumn;
    @FXML
    private VBox addTransactionMenu;
    @FXML
    private TextField enterAmountField;
    @FXML
    private DatePicker enterDatePicker;
    @FXML
    private ComboBox<String> chooseTypeComboBox;
    @FXML
    private ComboBox<CategoryDTO> categoryComboBox;

    @FXML
    private Button submitTransactionButton;

    @FXML
    private Button openAddTransactionMenu;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button goHomeButton;

    private ClientTransactionApi clientTransactionApi = new ClientTransactionApi();
    private ObservableList<TransactionDTO> observableList;
    private String currentUsername;
    private ClientCategoryApi clientCategoryApi = new ClientCategoryApi();

    /**
     * заполнение таблицы и создание кнопки удаления
      */
    @FXML
    public void initialize() {
        observableList = FXCollections.observableArrayList();
        transactionsTable.setItems(observableList);

        amountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAmount())));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIncome() ? "Доход" : "Расход"));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory() != null ? cellData.getValue().getCategory().getName() : "Без категории"));

        actionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Удалить"));
        actionsColumn.setCellFactory(col -> new TableCell<TransactionDTO, String>() {
            private final Button deleteButton = new Button("Удалить");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> {
                        TransactionDTO transaction = getTableView().getItems().get(getIndex());
                        deleteTransaction(transaction);
                    });
                }
            }
        });

        loadCategories();
    }

    /**
     * загрузка селектора категориями
     */
    private void loadCategories() {
        try {
            List<CategoryDTO> categories = clientCategoryApi.getCategories(currentUsername);
            ObservableList<CategoryDTO> categoryNames = FXCollections.observableArrayList(categories);
            categoryComboBox.setItems(categoryNames);
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось загрузить категории");
            e.printStackTrace();
        }
    }

    /**
     * сет текущего юзера
     * @param username юзернейм
     */
    public void setCurrentUser (String username) {
        this.currentUsername = username;
        setUsername(username);
        loadTransactions();
        loadCategories();
    }

    /**
     * сет лейбла
     * @param username
     */
    public void setUsername(String username) {
        if (username != null) {
            usernameLabel.setText(username);
        }
    }

    /**
     * выдвижение меню
     */
    @FXML
    public void toggleTransactionMenu() {
        addTransactionMenu.setVisible(!addTransactionMenu.isVisible());
    }

    /**
     * загрузка транзакций в observableList
     */
    private void loadTransactions() {
        try {
            List<TransactionDTO> transactions = clientTransactionApi.getTransactions(currentUsername);
            observableList.clear();
            observableList.addAll(transactions);
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось загрузить транзакции");
            e.printStackTrace();
        }
    }

    /**
     * добавление транзакции
     */
    @FXML
    public void addTransactionToDatabase() {
        TransactionDTO transaction = new TransactionDTO();
        transaction.setAmount(Double.parseDouble(enterAmountField.getText()));
        transaction.setDate(enterDatePicker.getValue());
        transaction.setIncome(chooseTypeComboBox.getValue().equals("Доход"));
        transaction.setCategory(categoryComboBox.getValue());

        try {
            String responseMessage = clientTransactionApi.addTransaction(currentUsername, transaction);
            showAlert("Результат", responseMessage);
            loadTransactions();
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось добавить транзакцию");
            e.printStackTrace();
        }
    }

    /**
     * редирект на домашнюю
     * @throws IOException
     */
    @FXML
    public void goHome() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/home.fxml"));
        Parent root = loader.load();

        HomeController homeController = loader.getController();
        homeController.setCurrentUser (currentUsername);

        Stage stage = (Stage) goHomeButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * удаление транзакции
     * @param transaction
     */
    @FXML
    public void deleteTransaction(TransactionDTO transaction) {
        if (transaction == null) {
            showAlert("Ошибка", "Выберите транзакцию для удаления");
            return;
        }

        try {
            String responseMessage = clientTransactionApi.deleteTransaction(currentUsername, transaction.getId());
            showAlert("Результат", responseMessage);
            loadTransactions();
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось удалить транзакцию");
            e.printStackTrace();
        }
    }

    /**
     * окно с ошибкой или не ошибкой
     * @param title название окна
     * @param content содержание окна
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}

