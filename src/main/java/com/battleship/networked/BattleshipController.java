package com.battleship.networked;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Optional;

public class BattleshipController {
    Ocean my_ocean;
    ImageView[][] my_images = new ImageView[10][10];

    ImageView[][] enemy_images = new ImageView[10][10];

    /* scene UI nodes */
    @FXML
    GridPane myOceanPane;
    @FXML
    GridPane enemyOceanPane;
    @FXML
    private Button btnAllocate;
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnShootAt;
    @FXML
    private Label enemyName;
    @FXML
    private TextArea log;

    private volatile GameState gameState;
    volatile int result = -1;
    private final Object lockDummy = new Object();

    private Thread connectionThread;

    private int currentPlacingShipNum = 0;
    private final Ship[] shipsToPlace = {
            new Battleship(),
            new Cruiser(), new Cruiser(),
            new Destroyer(), new Destroyer(), new Destroyer(),
            new Submarine(), new Submarine(), new Submarine(), new Submarine()
    };

    @FXML
    private void initialize(){
        my_ocean = new Ocean();
        synchronized (lockDummy) {
            gameState = GameState.readyToMeet;
        }

        Connection connection;
        try {
            connection = new Connection(BattleshipApp.serverMode, getConnectionParams(), gameState, lockDummy, this);
            connectionThread = new Thread(connection);
            connectionThread.start();
        } catch (ConnectException ignored){
            showAlert("Error", "No connection to server", "Server is busy or not started up");
            System.exit(-1);
        } catch (IOException e){
            showAlert("Error", "Unable to connect to server", e.getMessage());
            System.exit(-1);
        }
    }

    @FXML
    private void allocateClick(ActionEvent event){
        addOceanCells(myOceanPane, my_images, this::myCellClick);
        btnAllocate.setDisable(true);
    }

    @FXML
    private void playClick(ActionEvent event){
        btnPlay.setDisable(true);
        btnShootAt.setDisable(false);
        addOceanCells(enemyOceanPane, enemy_images, this::enemyCellClick);
        synchronized (lockDummy){
            gameState = GameState.clientTurn;
        }
    }

    @FXML
    private void cancelClick(ActionEvent event){
        synchronized (lockDummy){
            gameState = GameState.askedToCancel;
        }
        connectionThread.interrupt();
        Platform.exit();
    }

    private ConnectionParams getConnectionParams(){
        Dialog<ConnectionParams> dialog;
        if (BattleshipApp.serverMode)
            dialog = new ServerParamsDialog();
        else
            dialog = new ClientParamsDialog();
        Optional<ConnectionParams> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void myCellClick(MouseEvent event){
        if(currentPlacingShipNum < shipsToPlace.length) {
            Node source = (Node) event.getSource();
            Integer row = GridPane.getRowIndex(source);
            Integer col = GridPane.getColumnIndex(source);

            Ship currentShip = shipsToPlace[currentPlacingShipNum];
            boolean horizontal = (event.getButton() == MouseButton.PRIMARY);

            if (currentShip.okToPlaceShipAt(row, col, horizontal, my_ocean)) {
                currentShip.placeShipAt(row, col, horizontal, my_ocean);
                updateMyOceanGrid(my_ocean, my_images);
                currentPlacingShipNum += 1;
            }
        }
        if (currentPlacingShipNum >= shipsToPlace.length)
        {
            btnPlay.setDisable(false);
            btnAllocate.setDisable(true);
            for(Node current: myOceanPane.getChildren())
                if (current instanceof AnchorPane)
                    ((AnchorPane) current).removeEventFilter(MouseEvent.MOUSE_CLICKED, this::myCellClick);
        }
    }

    //handles game field cell click
    private void enemyCellClick(MouseEvent event){
        Node source = (Node) event.getSource();
        Integer row = GridPane.getRowIndex(source);
        Integer col = GridPane.getColumnIndex(source);

        shootEnemy(row, col);
    }

    //add anchored image viewers to given game field
    //necessary at application start
    private void addOceanCells(GridPane ocean_pane, ImageView[][] images, EventHandler<? super MouseEvent> clickHandler){
        for (int i = 0; i<images.length; ++i)
            for (int j = 0; j<images[0].length; ++j){
                images[i][j] = new ImageView();
                images[i][j].setImage(null);
                AnchorPane currentPane = new AnchorPane();
                AnchorPane.setTopAnchor(images[i][j], 4d);
                AnchorPane.setRightAnchor(images[i][j], 4d);
                AnchorPane.setBottomAnchor(images[i][j], 4d);
                AnchorPane.setLeftAnchor(images[i][j], 4d);
                currentPane.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
                currentPane.getChildren().add(images[i][j]);
                currentPane.getStyleClass().add("my-ocean-cell");
                ocean_pane.add(currentPane, j, i);
            }
    }

    private void updateMyOceanGrid(Ocean ocean, ImageView[][] images){
        for (int i = 0; i<10; ++i) {
            for (int j = 0; j < 10; ++j) {
                if (ocean.isOccupied(i, j)) {
                    images[i][j].setImage(Icons.getShipIcon());
                }
            }
        }
    }

    synchronized void showAlert(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/dialog.css").toExternalForm());
        alert.showAndWait();
    }

    synchronized void setEnemyName(String enemyName){
        this.enemyName.setText(enemyName + ":");
        btnAllocate.setDisable(false);
    }

    synchronized void showFinish(int enemyShots){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations");
        alert.setHeaderText("You won the game!");
        alert.setContentText(String.format("You made %d shots, your enemy made %d shots.", my_ocean.getShotsFired(), enemyShots));
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/dialog.css").toExternalForm());
        alert.showAndWait();
        Platform.exit();
    }

    synchronized void getShootCount(){
        result = my_ocean.shotsFired;
    }

    synchronized void showCancel(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Game was ended.");
        alert.setContentText("Your enemy cancelled this game. You have not won.");
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/dialog.css").toExternalForm());
        alert.showAndWait();
        Platform.exit();
    }

    synchronized void shootMe(int row, int col){
        synchronized (lockDummy) {
            ShootResult t = my_ocean.shootAt(row, col);
            result = t.getValue();
            log.appendText(String.format("%d %d <-\n-> %s\n", row+1, col+1, t.toString()));
        }
        Platform.runLater(() -> {
            switch(result){
                case 200: //MISS
                    my_images[row][col].setImage(Icons.getMissIcon());
                    break;
                case 201: //HIT
                    my_images[row][col].setImage(Icons.getHitIcon());
                    break;
                case 202: //KILLED
                    my_images[row][col].setImage(Icons.getHitIcon());
                    break;
                case 203: //REPEAT
                case 204: //SUNK
                    break;
            }
        });
        if (my_ocean.isGameOver()){
            synchronized (lockDummy){
                gameState = GameState.finished;
            }
        }
    }

    //inputs target coordinates from user as two space-separated integers
    @FXML
    private void askCoords(ActionEvent event){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Coordinates input:");
        dialog.setHeaderText("Enter target column and row:");
        dialog.setContentText("It should be two space-separated integers from 1 to 10.\n");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/dialog.css").toExternalForm());

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String[] numbers = result.get().split(" ");
            if (numbers.length<2)
                showAlert("Alert","Coordinates are incorrect","You should input two integers.\nTry again.");
            int row = Integer.parseInt(numbers[0]);
            int col = Integer.parseInt(numbers[1]);
            if (col < 1 || col > 10 || row < 1 || row > 10)
                showAlert("Alert", "Coordinates are incorrect","Integers should be from 1 to 10.\nTry again.");
            else shootEnemy(row-1, col-1);
        }
    }

    void shootEnemy(int row, int col) {
        log.appendText(String.format("-> %d %d\n", row+1, col+1));
        if ((BattleshipApp.serverMode && gameState != GameState.serverTurn) ||
                (!BattleshipApp.serverMode && gameState != GameState.clientTurn))
            return;
        synchronized (lockDummy) {
            result = 10 * row + col;
            gameState = GameState.waitingForResult;
        }
    }

    synchronized void resultEnemyShoot(int row, int col, int value){
        switch(result){
            case 200: //MISS
                enemy_images[row][col].setImage(Icons.getMissIcon());
                log.appendText("Miss! <-");
                break;
            case 201: //HIT
                enemy_images[row][col].setImage(Icons.getHitIcon());
                log.appendText("Hit. <-");
                break;
            case 202: //KILLED
                enemy_images[row][col].setImage(Icons.getHitIcon());
                log.appendText("Killed. <-");
                break;
            case 203: //REPEAT
            case 204: //SUNK
                showAlert("Alert", "You've just shot to coordinates that are already shot",
                        String.format("Target coordinates are (%d, %d). Don't do that.", row+1, col+1));
                //System.out.println("Don't shoot here");
                break;
        }
    }
}
