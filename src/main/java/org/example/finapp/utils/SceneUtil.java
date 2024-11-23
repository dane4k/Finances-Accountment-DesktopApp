package org.example.finapp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.finapp.controllers.SampleController;

import java.io.IOException;

public class SceneUtil {
    /**
     * редирект на страницу
     * @param button текущая кнопка
     * @param fileName название fxml
     * @param username ник
     * @throws IOException
     */
    public void navigateTo(Button button, String fileName, String username) throws IOException {
        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/" + fileName + ".fxml"));

        Parent root = loader.load();

        Object controller = loader.getController();

        if (controller instanceof SampleController && username != null) {
            SampleController sampleController = (SampleController) controller;
            if (username != null) {
                sampleController.setCurrentUser(username);
            }
        }

        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
    }

    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

