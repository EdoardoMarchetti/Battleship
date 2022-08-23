package com.edomar.battleship.entities.components.interfaces;

import com.edomar.battleship.entities.transforms.Transform;

/**
 * Lo SpawnComponent è il component che si occpua di posizionare l'oggetto sul campo da gioco
 */

public interface SpawnComponent {

    /** Chiamato direttamente dal GameObject**/
    void spawn(Transform t,
               int row,
               int column);
}
