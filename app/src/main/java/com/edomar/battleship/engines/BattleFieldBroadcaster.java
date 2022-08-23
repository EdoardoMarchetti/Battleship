package com.edomar.battleship.engines;

import com.edomar.battleship.entities.InputObserver;

public interface BattleFieldBroadcaster {

    void addObserver(InputObserver o);
}
