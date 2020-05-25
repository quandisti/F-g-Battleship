package com.battleship.networked;

/**
 * This describes characteristics common to all the ships. It has subclasses
 */
class Ship {
    int bowRow;
    int bowColumn;
    int length;
    boolean horizontal;
    boolean[] hit;

    /**
     * Returns the length of this particular ship.
     * @return length
     */
    int getLength() {
        return length;
    }

    /**
     * Returns bowRow
     * @return bowRow
     */
    int getBowRow() {
        return bowRow;
    }

    /**
     * Returns bowColumn
     * @return bowColumn
     */
    int getBowColumn() {
        return bowColumn;
    }

    /**
     * Returns horizontal
     * @return horizontal
     */
    boolean isHorizontal() {
        return horizontal;
    }

    /**
     * Sets the value of bowRow
     * @param row value to be set to bowRow
     */
    void setBowRow(int row) {
        bowRow = row;
    }

    /**
     * Sets the value of bowColumn
     * @param column value to be set to bowColumn
     */
    void setBowColumn(int column) {
        bowColumn = column;
    }

    /**
     * Sets the value of horizontal
     * @param horizontal value to be set to horizontal
     */
    void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * This method returns string representing ship type (to be overridden)
     * @return "empty"
     */
    String getShipType() {
        return "empty";
    }

    /**
     * Returns a single-character String to use in the Ocean's print method
     * @param row row of cell to get value of
     * @param column column of cell to get value of
     * @return string to be printed
     */
    String toString(int row, int column) {
        if (this.getShipType() == "empty") {
            if (hit[0])
                return "-";
            else return ".";
        }

        if (horizontal) {
            if ((row != bowRow) || (column < bowColumn) || (column > bowColumn + length - 1))
                return ".";
            if (hit[column - bowColumn])
                return isSunk()?"X":"S";
        } else {
            //check column
            if ((column != bowColumn) || (row < bowRow) || (row > bowRow + length - 1))
                return ".";
            if (hit[row - bowRow])
                return isSunk()?"X":"S";
        }
        return ".";
    }

    /**
     * Returns true if it is okay to put a ship of this length with its bow in this location,
     * with the given orientation, and returns false otherwise.
     * @param row row where ship bow location is checking
     * @param column column where ship bow location is checking
     * @param horizontal ship orientation
     * @param ocean ocean to check possibility in
     * @return if it's possible to put the ship
     */
    boolean okToPlaceShipAt(int row, int column, boolean horizontal, Ocean ocean) {
        if ((row < 0) || (row > 9) || (column < 0) || (column>9))
            return false;

        if (horizontal){
            //check array bounds
            if (column + length - 1 > 9)
                return false;
            //check adjacency
            for (int i = Math.max(0, row - 1); i <= Math.min(9, row + 1); ++i)
                for (int j = Math.max(0, column - 1); j <= Math.min(9, column + length); ++j)
                    if (ocean.isOccupied(i, j))
                        return false;
        }
        else{
            //check array bounds
            if (row + length - 1 > 9)
                return false;
            //check adjacency
            for (int i = Math.max(0, row - 1); i <= Math.min(9, row+length); ++i)
                for (int j = Math.max(0, column - 1); j <= Math.min(9, column + 1); ++j)
                    if (ocean.isOccupied(i, j))
                        return false;
        }

        return true;
    }

    /**
     * "Puts" the ship in the ocean.
     * @param row row where ship bow will be located at
     * @param column column where ship bow will be located at
     * @param horizontal ship orientation
     * @param ocean ocean to put ship in
     */
    void placeShipAt(int row, int column, boolean horizontal, Ocean ocean) {
        bowRow = row;
        bowColumn = column;
        this.horizontal = horizontal;
        if (horizontal) {
            for (int i = column; i <= column + length - 1; ++i)
                ocean.getShipArray()[row][i] = this;
        } else {
            for (int i = row; i <= row + length - 1; ++i)
                ocean.getShipArray()[i][column] = this;
        }
    }

    /**
     * If a part of the ship occupies the given row and column, and the ship hasn't been sunk,
     * mark that part of the ship as "hit" (in the hit array, 0 indicates the bow) and return true,
     * otherwise return false.
     * @param row row of target
     * @param column column of target
     * @return true if shoot is successful(hit 'real' afloat ship), otherwise false
     */
    ShootResult shootAt(int row, int column) {
        if (isSunk())
            return ShootResult.REPEAT;
        ShootResult resultIfKilled = (isHit())? ShootResult.KILLED : ShootResult.HIT;
        if (horizontal) {
            //check coords
            if ((row != bowRow) || (column < bowColumn) || (column > bowColumn + length - 1))
                return ShootResult.MISS;
            //mark hit
            if (hit[column-bowColumn])
                return ShootResult.REPEAT;
            hit[column - bowColumn] = true;
        } else {
            //check coords
            if ((column != bowColumn) || (row < bowRow) || (row > bowRow + length - 1))
                return ShootResult.MISS;
            //mark hit
            if (hit[row-bowRow])
                return ShootResult.REPEAT;
            hit[row - bowRow] = true;
        }
        if (length == 1 && isSunk())
            return ShootResult.KILLED;
        if (isSunk())
            return ShootResult.REPEAT;
        return resultIfKilled;
    }

    /**
     * Return true if every part of the ship has been hit, false otherwise
     * @return true if every part of the ship has been hit, otherwise false
     */
    boolean isSunk() {
        for (int i = 0; i < length; i++) {
            if (!hit[i])
                return false;
        }
        return true;
    }

    /**
     * Return true if any part of the ship has been hit, false otherwise
     * @return true if any part of the ship has been hit, otherwise false
     */
    boolean isHit(){
        for (int i = 0; i < length; i++) {
            if (hit[i])
                return true;
        }
        return false;
    }

}
