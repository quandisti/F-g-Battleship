package com.battleship.networked;

public enum GameState {
    readyToMeet(-1),
    meet(-2),
    clientTurn(-3),
    serverTurn(-4),
    waitingForResult(-5),
    askedToCancel(-6),
    finished(-7);

    private final int value;

    GameState(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}
