package org.example.finapp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.finapp.controllers.THomeController;

import java.io.IOException;

public class SceneUtil {
    //    public void navigateTo(Node sourceNode, String fileName) throws IOException {
//        Stage stage = (Stage) sourceNode.getScene().getWindow(); // {fileName}
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finapp/" + fileName + ".fxml"));
//        Parent root = loader.load();
//
//        Scene scene = new Scene(root, 1920, 1080);
//        stage.setScene(scene);
//    }
    public void navigateTo(Button button, String fileName, String username) throws IOException {
        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader loader;

        if (fileName != null) {
            loader = new FXMLLoader(getClass().getResource("/org/example/finapp/" + fileName + ".fxml"));
        } else {
            loader = new FXMLLoader(getClass().getResource("/org/example/finapp/home.fxml"));
        }

        Parent root = loader.load();

        if (fileName == null) {
            THomeController homeController = loader.getController();
            homeController.setCurrentUser(username);
        }

        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
    }

}
