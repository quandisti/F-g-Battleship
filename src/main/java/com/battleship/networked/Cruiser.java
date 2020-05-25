package com.battleship.networked;

/**
 * Describes a ship of length 3.
 */
class Cruiser extends Ship {

    /**
     * This constructor sets the inherited length variable to 1 and inherited hit array to an array of false.
     */
    Cruiser(){
        length = 3;
        hit = new boolean[] {false, false, false, false};
    }

    /**
     * This method returns string representing ship type
     * @return "cruiser"
     */
    @Override
    String getShipType(){
        return "cruiser";
    }

}
