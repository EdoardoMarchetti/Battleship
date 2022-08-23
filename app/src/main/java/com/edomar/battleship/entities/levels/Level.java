package com.edomar.battleship.entities.levels;

import com.edomar.battleship.entities.transforms.Transform;

import java.util.ArrayList;

/**
 * Classe level atratta da cui derivano le specializzazioni
 */

public abstract class Level {

    public ArrayList<String> mLevelObjects;
    public String mLevelName;
    public Transform transformInMovement = null;
    public boolean mDistance;

    public  ArrayList<String> getLevelObjects(){
        return mLevelObjects;
    }


    public String getName() {
        return mLevelName;
    }



}
