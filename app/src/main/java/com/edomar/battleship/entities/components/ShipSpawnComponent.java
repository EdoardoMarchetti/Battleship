package com.edomar.battleship.entities.components;

import android.util.Log;

import com.edomar.battleship.entities.transforms.ShipTransform;
import com.edomar.battleship.entities.transforms.Transform;
import com.edomar.battleship.entities.components.interfaces.SpawnComponent;

/**
 * Componente per gestire lo spawn della nave
 */

public class ShipSpawnComponent implements SpawnComponent {



    @Override
    public void spawn(Transform t, int row, int column) {
        //Imposto la posizione iniziale della nave
        t.setStartLocation(column * t.getBlockDimension(), row * t.getBlockDimension(), t.isVertical());
    }
}
