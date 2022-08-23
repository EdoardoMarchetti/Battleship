package com.edomar.battleship.entities.levels;

import java.util.ArrayList;

public class LevelRussian extends Level {

    public LevelRussian(){
        mLevelName = "russian";
        mDistance = true;
        mLevelObjects = new ArrayList<String>();

        //aggiungo le navi
        mLevelObjects.add("destroyer"); //3 blocchi
        mLevelObjects.add("rescue"); //3 blocchi
        mLevelObjects.add("patrol"); //2 blocchi
        mLevelObjects.add("patrol"); //2 blocchi
        mLevelObjects.add("patrol"); //2 blocchi


        //aggiungo il missile
        mLevelObjects.add("missile");
    }
}
