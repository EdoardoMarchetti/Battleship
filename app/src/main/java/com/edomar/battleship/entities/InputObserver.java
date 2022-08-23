package com.edomar.battleship.entities;

import android.view.MotionEvent;

import com.edomar.battleship.entities.grid.Grid;
import com.edomar.battleship.entities.levels.LevelManager;

public interface InputObserver {

    void handleInput(MotionEvent event, Grid grid,
                     LevelManager levelManager, int gameState,
                     int notifyNumber);


}
