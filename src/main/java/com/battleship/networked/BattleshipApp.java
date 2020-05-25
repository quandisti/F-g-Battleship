package com.battleship.networked;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class BattleshipApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    // this is set to: true if application is asked to be a TCP server,
    //                 false otherwise
    public static boolean serverMode = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            List<String> params = getParameters().getRaw();
            if (params.size()>=1 && params.get(0).equals("Server"))
                serverMode = true;

            Parent root = FXMLLoader.load(getClass().getResource("/battleship.fxml"));
            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/battleship.css").toExternalForm());
            primaryStage.setTitle("Battleship " + (serverMode?"[server]":"[client]"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
    }
}
