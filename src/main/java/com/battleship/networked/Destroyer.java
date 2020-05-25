package com.battleship.networked;

/**
 * Describes a ship of length 2.
 */
class Destroyer extends Ship {

    /**
     * This constructor sets the inherited length variable to 1 and inherited hit array to an array of false.
     */
    Destroyer(){
        length = 2;
        hit = new boolean[] {false, false, false, false};
    }

    /**
     * This method returns string representing ship type
     * @return "destroyer"
     */
    @Override
    String getShipType(){
        return "destroyer";
    }

}
