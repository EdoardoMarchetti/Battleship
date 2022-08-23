package com.edomar.battleship.entities.components;


import android.graphics.PointF;


import com.edomar.battleship.entities.transforms.AmmoTransform;
import com.edomar.battleship.entities.transforms.Transform;
import com.edomar.battleship.entities.components.interfaces.UpdateComponent;

/**
 * Componente per l'aggiornamento della posizione del missile
 */

public class AmmoUpdateComponent implements UpdateComponent {


    @Override
    public boolean update(long fps, Transform t) {
        AmmoTransform at = (AmmoTransform) t;


        float range = t.getGridDimension();
        //Where is the Ammo?
        PointF location = at.getLocation();
        //How fast is going?
        float speed = at.getSpeed();

        location.x += speed / fps;

        if(location.x > range){
            //disable the ammo
            return false;
        }

        at.updateCollider();

        return true;
    }

}
