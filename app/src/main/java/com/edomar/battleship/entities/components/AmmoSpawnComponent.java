package com.edomar.battleship.entities.components;


import com.edomar.battleship.entities.transforms.Transform;
import com.edomar.battleship.entities.components.interfaces.SpawnComponent;

/**
 * Componente che gestisce lo spawn del missile
 */

public class AmmoSpawnComponent implements SpawnComponent {


    @Override
    public void spawn(Transform t, int  row, int column) {
        t.setLocation(0 - (t.getObjectWidth()+t.getBlockDimension()), row * t.getBlockDimension());
    }

}
