package com.battleship.networked;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Icons {
    private static Image shipIcon = null;
    private static Image missIcon = null;
    private static Image hitIcon = null;
    private static Image killedIcon = null;

    public static Image getShipIcon(){
        if (shipIcon == null){
            try{
                shipIcon = new Image(new FileInputStream("/ship-48.png"));
            } catch (FileNotFoundException ignored) {}
        }
        return shipIcon;
    }

    public static Image getMissIcon(){
        if (missIcon == null){
            try{
                missIcon = new Image(new FileInputStream("/circle-48.png"));
            } catch (FileNotFoundException ignored) {}
        }
        return missIcon;
    }

    public static Image getHitIcon(){
        if (hitIcon == null){
            try{
                hitIcon = new Image(new FileInputStream("/x-mark-yellow-48.png"));
            } catch (FileNotFoundException ignored) {}
        }
        return hitIcon;
    }

    //not used
    public static Image getKilledIcon(){
        if (killedIcon == null){
            try{
                killedIcon = new Image(new FileInputStream("/x-mark-48.png"));
            } catch (FileNotFoundException ignored) {}
        }
        return killedIcon;
    }
}
