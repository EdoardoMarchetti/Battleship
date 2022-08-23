package com.edomar.battleship.entities.components.interfaces;


import com.edomar.battleship.entities.transforms.Transform;

/**
 * L'UpdateComponent si occupa di aggiornare i valori del transfom di un oggetto se necessario
 */

public interface UpdateComponent {

    /** Chiamato direttamente dal GameObject**/
    boolean update(long fps,
                 Transform t);


}
