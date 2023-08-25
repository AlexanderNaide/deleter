package org.satal.deleter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.satal.deleter.controllers.DeleterController;

import java.io.IOException;

public class DeleterApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(DeleterApplication.class.getResource("Deleter-View.fxml"));
        Scene scene = new Scene(loader.load());
//        scene.getStylesheets().add("org/satal/deleter/style.css");
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.setTitle("Satal - deleter v1.1");
        stage.setResizable(false);
        stage.getIcons().add(new Image(String.valueOf((getClass().getResource("icons8-удалить-вид-48.png"))), 48, 48, false, false));
        DeleterController controller = loader.getController();
        stage.setOnCloseRequest(e -> controller.shutdown());
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}