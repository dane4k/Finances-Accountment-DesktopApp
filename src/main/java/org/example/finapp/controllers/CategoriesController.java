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
import javafx.util.Callback;
import org.example.finapp.APIInteraction.ClientCategoryApi;
import org.example.finapp.models.CategoryDTO;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CategoriesController {

    @FXML
    private TableView<String> categoriesTable;
    @FXML
    private TableColumn<String, String> categoryNameColumn;
    @FXML
    private Button addCategoryButton;
    @FXML
    private TextField enterCategoryNameTF;
    @FXML
    private TextField usernameTF;

    @FXML
    private VBox addCategoryMenu;

    @FXML
    private Button goHomeButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button openAddCategoryMenu;

    @FXML
    private TableColumn<String, String> actionsColumn;

    private ClientCategoryApi clientCategoryApi = new ClientCategoryApi();
    private String currentUsername;
    private ObservableList<String> observableList;


    /**
     * заполнение таблицы и создание кнопки
     */
    @FXML
    public void initialize() {
        categoryNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        actionsColumn.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                return new TableCell<String, String>() {
                    private final Button deleteButton = new Button("Удалить");

                    {
                        deleteButton.setStyle("-fx-background-color: #ff3030; -fx-text-fill: white;");
                        deleteButton.setOnAction(event -> {
                            String categoryName = getTableView().getItems().get(getIndex());
                            deleteCategory(categoryName);
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        });

        observableList = FXCollections.observableArrayList();
        categoriesTable.setItems(observableList);
        loadCategories();
    }


    /**
     * раскрытие меню добавления категории при нажатии на кнопку
     */
    @FXML
    public void toggleCategoryMenu() {
        addCategoryMenu.setVisible(!addCategoryMenu.isVisible());
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
        homeController.setCurrentUser(currentUsername);

        Stage stage = (Stage) goHomeButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * сет юзернейма в лейбл
     * @param username
     */
    public void setUsername(String username) {
        if (username != null) {
            usernameLabel.setText(username);
        }
    }

    /**
     * сет текущего юзера и обновление таблицы
     * @param username
     */
    public void setCurrentUser(String username) {
        this.currentUsername = username;
        setUsername(username);
        loadCategories();
    }

    /**
     * заполнение observableList для таблицы
     */
    private void loadCategories() {
        try {
            List<CategoryDTO> categories = clientCategoryApi.getCategories(currentUsername);
            observableList.clear();

            for (CategoryDTO category : categories) {
                if (!observableList.contains(category.getName())) {
                    observableList.add(category.getName());
                }
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось загрузить категории");
            e.printStackTrace();
        }
    }

    /**
     * добавление категории
     */
    @FXML
    public void addCategory() {
        String categoryName = enterCategoryNameTF.getText();
        if (categoryName.isEmpty()) {
            showAlert("Ошибка", "Введите название категории");
            return;
        }

        try {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setName(categoryName);
            String responseMessage = clientCategoryApi.addCategory(currentUsername, categoryDTO);
            showAlert("Результат", responseMessage);
            loadCategories();
            enterCategoryNameTF.clear();
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось добавить категорию");
            e.printStackTrace();
        }
    }

    /**
     * удаление категории
     * @param categoryName
     */
    @FXML
    public void deleteCategory(String categoryName) {
        if (categoryName == null) {
            showAlert("Ошибка", "Выберите категорию для удаления");
            return;
        }

        try {
            String responseMessage = clientCategoryApi.deleteCategory(currentUsername, categoryName);
            if (!Objects.equals(responseMessage, "Категория удалена")) {
                responseMessage = "Вы не можете удалить общую категорию";
            }
            showAlert("Результат", responseMessage);
            observableList.remove(categoryName);
            categoriesTable.refresh();
            loadCategories();
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось удалить категорию");
            e.printStackTrace();
        }
    }

    /**
     * алерт для информации и не информации
     * @param title имя окна
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