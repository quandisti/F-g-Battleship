package com.battleship.networked;

import java.util.Random;

/**
 * This contains a 10x10 array of Ships, representing the "ocean," and some methods to manipulate it
 */

class Ocean {

    Ship[][] ships = new Ship[10][10];
    int shotsFired;
    int shipsSunk;

    static Random rnd = new Random();

    /**
     * The constructor. Creates an "empty" ocean (fills the ships array with EmptySeas via invoking clearOcean()).
     */
    Ocean(){
        shotsFired = 0;
        shipsSunk = 0;
        clearOcean();
        //placeAllShipsRandomly();
    }

    /**
     * This method actually sets all cells of ships[] array with EmptySeas
     */
    void clearOcean()
    {
        for (int i = 0; i<10; ++i)
            for (int j = 0; j<10; ++j){
                EmptySea current = new EmptySea();
                ships[i][j] = current;
                current.setBowRow(i);
                current.setBowColumn(j);
            }
    }

    /**
     * Places all ten ships randomly on the (initially empty) ocean.
     */
    void placeAllShipsRandomly(){
        //place a battleship
        placeShipRandomly(new Battleship());
        //place 2 cruisers
        for (int i = 0; i<2; ++i)
            placeShipRandomly(new Cruiser());
        //place 3 destroyers
        for (int i = 0; i<3; ++i)
            placeShipRandomly(new Destroyer());
        //place 4 submarines
        for (int i = 0; i<4; ++i)
            placeShipRandomly(new Submarine());
    }

    /**
     * generates random coordinates and places an given ship object to ocean
     * @param ship ship to put
     */
    void placeShipRandomly(Ship ship)
    {
        int row;
        int col;
        boolean horizontal;

        do{
            row = rnd.nextInt(10);
            col = rnd.nextInt(10);
            horizontal = rnd.nextBoolean();
        } while(!ship.okToPlaceShipAt(row, col, horizontal, this));
        ship.placeShipAt(row, col, horizontal, this);
    }

    /**
     * Returns true if the given location contains a ship, false if it does not.
     * @param row row to check
     * @param column column to check
     * @return true if the given location contains a ship, false if it does not
     */
    boolean isOccupied(int row, int column){
        if (row < 0 || row > 9 || column < 0 || column > 9)
            return false;
        return (!ships[row][column].getShipType().equals("empty"));
    }

    /**
     * Returns true if the given location contains a "real" ship, still afloat, (not an EmptySea), false if it does not.
     * In addition, this method updates the number of shots that have been fired, and the number of hits.
     * @param row row of target
     * @param column column of target
     * @return true, if shoot is successful
     */
    ShootResult shootAt(int row, int column){
        if (ships[row][column].isSunk())
            return ShootResult.SUNK;
        ++shotsFired;
        ShootResult result = ships[row][column].shootAt(row,column);
        if (result == ShootResult.KILLED) {
            ++shipsSunk;
        }
        return result;
    }

    /**
     * Returns the number of shots fired (in this game).
     * @return the number of shots
     */
    int getShotsFired(){
        return shotsFired;
    }

    /**
     * Returns the number of ships sunk (in this game).
     * @return the number of ships sunk
     */
    int getShipsSunk(){
        return shipsSunk;
    }

    /**
     * Returns true if all ships have been sunk, otherwise false.
     * @return true if all ships have been sunk, otherwise false
     */
    boolean isGameOver(){
        return (shipsSunk == 10);
    }

    /**
     * Returns the 10x10 array of ships to be modified
     * @return the 10x10 array of ships
     */
    Ship[][] getShipArray(){
        return ships;
    }

}
