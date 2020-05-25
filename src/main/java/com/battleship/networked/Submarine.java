package com.battleship.networked;

/**
 * Describes a ship of length 1.
 */
class Submarine extends Ship {

    /**
     * This constructor sets the inherited length variable to 1 and inherited hit array to an array of false.
     */
    Submarine(){
        length = 1;
        hit = new boolean[] {false, false, false, false};
    }

    /**
     * This method returns string representing ship type
     * @return "submarine"
     */
    @Override
    String getShipType(){
        return "submarine";
    }

}
