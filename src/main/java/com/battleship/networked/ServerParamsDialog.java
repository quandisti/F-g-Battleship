package com.battleship.networked;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

import java.net.UnknownHostException;

public class ServerParamsDialog extends Dialog<ConnectionParams> {
    private TextField name;
    private TextField port;

    public ServerParamsDialog(){
        super();
        //initOwner(primaryStage);

        setHeaderText("Enter Server parameters:");

        name = new TextField();
        port = new TextField();
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.addRow(0, new Label("Your name:"), name);
        grid.addRow(1, new Label("Port to run server on:"), port);

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType exitButtonType = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(confirmButtonType, exitButtonType);

        Node confirmButton = getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        ChangeListener<String> validator = (observable, oldval, newval) -> {
            int p = (port.getText().isEmpty() || port.getText().length()>5)?-1:Integer.parseInt(port.getText());
            confirmButton.setDisable(name.getText().isEmpty() ||
                    port.getText().isEmpty() ||
                    p < 0 ||
                    p > 65535);
        };

        name.textProperty().addListener(validator);
        port.textProperty().addListener(validator);

        getDialogPane().setContent(grid);
        getDialogPane().getStylesheets().add(
                getClass().getResource("/dialog.css").toExternalForm());

        Platform.runLater(() -> name.requestFocus());

        setResultConverter(btn ->
        {
            try {
                return btn == confirmButtonType
                        ?new ConnectionParams(name.getText(), "localhost", Integer.parseInt(port.getText()))
                        :null;
            } catch (UnknownHostException ignored) {
                return null;
            }
        });
    }



}
