package com.battleship.networked;

import javafx.application.Platform;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Connection implements Runnable{
    private final boolean serverMode;
    private final ConnectionParams connectionParams;
    private final BattleshipController controller;

    private GameState gameState;
    private final Object lock;

    private Socket socket;
    private ServerSocket server;

    private PrintWriter writer;
    private Scanner scanner;

    public Connection(boolean serverMode, ConnectionParams connectionParams, GameState gameState, Object lock, BattleshipController controller)
            throws IOException{
        this.serverMode = serverMode;
        this.connectionParams = connectionParams;
        if (connectionParams == null)
            System.exit(0);
        this.gameState = gameState;
        this.lock = lock;
        this.controller = controller;
        makeConnection();
    }

    private void makeConnection() throws IOException{
        if (serverMode){
                server = new ServerSocket(connectionParams.getPort(), 1);
                socket = server.accept();
        } else {
                socket = new Socket(connectionParams.getHost(), connectionParams.getPort());
        }
        scanner = new Scanner(socket.getInputStream());
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private void closeSockets(){
        try{
            if (serverMode && server.isBound() && !server.isClosed()){
                server.close();
            } else if (socket.isConnected() && !socket.isClosed())
                socket.close();
        }
        catch(IOException ignored) {}
    }

    private void meet(){
        if (serverMode){
            if (scanner.nextInt() != GameState.readyToMeet.getValue()) {
                System.out.println("Something wrong happened, didn't get enemy name...");
                System.exit(-1);
            }
            Platform.runLater(() -> controller.setEnemyName(scanner.nextLine()));
            writer.write(GameState.readyToMeet.getValue());
            writer.write(connectionParams.getUsername());
        } else {
            writer.write(GameState.readyToMeet.getValue());
            writer.write(connectionParams.getUsername());
            if (scanner.nextInt() != GameState.readyToMeet.getValue()) {
                System.out.println("Something wrong happened, didn't get enemy name...");
                System.exit(-1);
            }
            Platform.runLater(() -> controller.setEnemyName(scanner.nextLine()));
        }
        synchronized (lock){
            gameState = GameState.meet;
        }
    }

    @Override
    public void run(){
        while (!Thread.currentThread().isInterrupted()){
            GameState current = GameState.meet;
            synchronized (lock) {
                current = gameState;
            }
            if (current == GameState.waitingForResult){
                writer.write(serverMode?GameState.serverTurn.getValue():GameState.clientTurn.getValue());
                writer.write(controller.result);
                controller.result = -1;
                if (scanner.nextInt() == GameState.waitingForResult.getValue()){
                    int coords = scanner.nextInt();
                    Platform.runLater(() -> controller.resultEnemyShoot(coords/10, coords%10, scanner.nextInt()));
                }
            } else if ((!serverMode && current == GameState.serverTurn) ||
                    (serverMode && current == GameState.clientTurn)){
                    int state = scanner.nextInt();
                    if ((serverMode && state == GameState.clientTurn.getValue()) ||
                            (!serverMode && state == GameState.serverTurn.getValue())){
                        int coords = scanner.nextInt();
                        controller.shootMe(coords/10, coords%10);
                        synchronized (lock) {
                            if (gameState != GameState.finished) {
                                writer.write(GameState.waitingForResult.getValue());
                                writer.write(coords);
                                writer.write(controller.result);
                            } else {
                                writer.write(GameState.finished.getValue());
                                controller.getShootCount();
                                writer.write(controller.result);
                            }
                        }
                    }
                }
        }
        synchronized (lock){
            if (gameState == GameState.askedToCancel){
                writer.write(GameState.askedToCancel.getValue());
            } else {
                if (scanner.nextInt() != GameState.finished.getValue()){
                    System.out.println("Something wrong happened, didn't matched finish state...");
                    System.exit(-1);
                }
                Platform.runLater(() -> controller.showFinish(scanner.nextInt()));
            }
        }
        closeSockets();
        Platform.exit();
    }

}
