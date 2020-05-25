package com.battleship.networked;

/**
 * Describes a ship of length 4.
 */
class Battleship extends Ship {

    /**
     * This constructor sets the inherited length variable to 1 and inherited hit array to an array of false.
     */
    Battleship(){
        length = 4;
        hit = new boolean[] {false, false, false, false};
    }

    /**
     * This method returns string representing ship type
     * @return "battleship"
     */
    @Override
    String getShipType(){
        return "battleship";
    }
}