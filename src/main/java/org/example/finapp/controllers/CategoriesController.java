package org.example.finapp.controllers;

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
import org.example.finapp.models.CategoryItem;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class CategoriesController implements Initializable {


    @FXML
    private Label usernameLabel;


    @FXML
    private VBox addCategoryMenu;

    @FXML
    private TextField enterCategoryNameTF;


    @FXML
    private TableView<CategoryItem> categoriesTable;

    @FXML
    private TableColumn<CategoryItem, String> categoryNameColumn;

    @FXML
    private TableColumn<CategoryItem, Void> actionsColumn;

    @FXML
    private Button goHomeButton;


    private String currentUsername;

    private DbHandler dbHandler = new DbHandler();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

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
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CategoryItem categoryItem = getTableView().getItems().get(getIndex());
                    if (categoryItem.isDeletable()) {
                        setGraphic(deleteButton);
                        deleteButton.setOnAction(_ -> deleteCategory(categoryItem.getName()));
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        try {
            loadCategoriesFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void redirectToHomeButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/home.fxml"));
            Parent root = loader.load();

            HomeController homeController = loader.getController();
            homeController.setCurrentUser(currentUsername);

            Stage stage = (Stage) goHomeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить главную страницу");
        }
    }


    private void loadCategoriesFromDatabase() throws SQLException {
        ObservableList<CategoryItem> categoriesItemList = FXCollections.observableArrayList();

        String query = "SELECT name, user_id IS NOT NULL as is_deletable FROM categories WHERE user_id IS NULL OR user_id = ?";

        try (Connection connection = dbHandler.getDbConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            int userId = dbHandler.getUserIdByUsername(connection, currentUsername);
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String categoryName = rs.getString("name");
                    boolean isDeletable = rs.getBoolean("is_deletable");
                    categoriesItemList.add(new CategoryItem(categoryName, isDeletable));
                }
            }
        }

        categoriesTable.setItems(categoriesItemList);
    }

    @FXML
    public void toggleCategoryMenu() {
        addCategoryMenu.setVisible(!addCategoryMenu.isVisible());
    }


    public void setUsername(String username) {
        if (username != null) {
            usernameLabel.setText(username);
        }
    }


    public void setCurrentUser(String username) {
        this.currentUsername = username;
        setUsername(username);

        try {
            loadCategoriesFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addCategoryToDatabase() {
        String category = enterCategoryNameTF.getText();

        if (category == null) {
            showAlert("Ошибка", "Вы не ввели название категории");
            return;
        }

        try {
            dbHandler.addCategory(category, currentUsername);
            enterCategoryNameTF.clear();
            showAlert("успешно", "категория добавлена");
            loadCategoriesFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось добавить категорию");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void deleteCategory(String category) {
        try {
            dbHandler.deleteCategory(category, currentUsername);
            loadCategoriesFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось удалить категорию");
        }
    }


}
