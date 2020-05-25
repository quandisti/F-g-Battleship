package com.battleship.networked;

public enum ShootResult {
    MISS(200,"Miss!"),
    HIT(201,"Hit."),
    KILLED(202,"Killed."),
    REPEAT(203,"Killed."),
    SUNK(203,"Invalid position.");

    private final String name;
    private final int value;

    private ShootResult(int value, String str){
        this.value = value;
        name = str;
    }

    public int getValue() {
        return value;
    }
    @Override
    public String toString(){
        return name;
    }
}
