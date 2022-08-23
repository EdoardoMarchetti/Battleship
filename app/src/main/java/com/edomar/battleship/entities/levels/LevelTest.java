package com.edomar.battleship.entities.levels;

import java.util.ArrayList;

public class LevelTest extends Level {

    public LevelTest(){
        mLevelName = "test";
        mLevelObjects = new ArrayList<String>();

        //aggiungo le navi
        mLevelObjects.add("battleship");
        mLevelObjects.add("carrier");
        mLevelObjects.add("cruiser");
        mLevelObjects.add("destroyer");
        mLevelObjects.add("patrol");
        mLevelObjects.add("rescue");
        mLevelObjects.add("submarine");

        //aggiungo il missile
        mLevelObjects.add("missile");
    }
}
