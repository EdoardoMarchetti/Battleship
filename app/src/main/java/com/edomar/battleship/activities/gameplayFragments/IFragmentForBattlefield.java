package com.edomar.battleship.activities.gameplayFragments;

public interface IFragmentForBattlefield {

    void notifyHit(int row, int column, boolean result);

    void notifyEndGame();
}
