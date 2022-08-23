package com.edomar.battleship.entities.levels;

import java.util.ArrayList;

public class LevelStandard extends Level {

    public LevelStandard(){
        mLevelName = "standard";
        mDistance = true;
        mLevelObjects = new ArrayList<String>();

        //aggiungo le navi
        mLevelObjects.add("submarine"); //5 blocchi
        mLevelObjects.add("carrier"); //4 blocchi
        mLevelObjects.add("destroyer"); //3 blocchi
        mLevelObjects.add("rescue"); //3 blocchi
        mLevelObjects.add("patrol"); //2 blocchi


        //aggiungo il missile
        mLevelObjects.add("missile");
    }


}
