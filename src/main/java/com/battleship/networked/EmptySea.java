package com.battleship.networked;

/**
 * Describes a part of the ocean that doesn't have a ship in it.
 */
class EmptySea extends Ship {

    /**
     * This constructor sets the inherited length variable to 1 and inherited hit array to an array of false.
     */
    EmptySea(){
        length = 1;
        hit = new boolean[] {false, false, false, false};
    }

    /**
     * This method overrides shootAt(int row, int column) that is inherited from Ship,
     * and always returns false to indicate that nothing was hit.
     * @param row row of target
     * @param column column of target
     * @return false
     */
    @Override
    ShootResult shootAt(int row, int column){
        if ((bowRow == row)&&(bowColumn == column)){
            hit[0] = true;
            return ShootResult.MISS;
        }
        return ShootResult.MISS;
    }

    /**
     * This method overrides isSunk() that is inherited from Ship, and always returns false
     * to indicate that you didn't sink anything.
     * @return false
     */
    @Override
    boolean isSunk(){
        return (hit[0]);
    }

    /**
     * This method returns string representing ship type
     * @return "empty"
     */
    @Override
    String getShipType(){
        return "empty";
    }

}
