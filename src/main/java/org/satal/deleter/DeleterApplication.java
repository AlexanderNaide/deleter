package org.satal.deleter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class DeleterApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
/*        FXMLLoader fxmlLoader = new FXMLLoader(DeleterApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();*/

        FXMLLoader loader = new FXMLLoader(DeleterApplication.class.getResource("Deleter-View.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add("org/satal/deleter/style.css");
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.setTitle("Satal - deleter");
        stage.setResizable(false);
//        stage.getIcons().add(new Image(String.valueOf((getClass().getResource("cloud.png"))), 137, 84, false, false));
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}