package com.edomar.battleship.entities.components.interfaces;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.edomar.battleship.entities.transforms.Transform;
import com.edomar.battleship.entities.specifications.ObjectSpec;

/**
 * Il GraphicsComponent è il componente dell'oggetto che si occpua di disegnarlo
 */

public interface GraphicsComponent {

    /** Usato in GameObjectFactory**/
    void initialize (Context c , ObjectSpec spec, PointF objectSize); //objectSize è già in proporzionato in funzione della dimensione dei blocchi

    /** Invocato dal gameObject e non dal renderer **/
    void draw (Canvas canvas, Paint paint, Transform t);
}
