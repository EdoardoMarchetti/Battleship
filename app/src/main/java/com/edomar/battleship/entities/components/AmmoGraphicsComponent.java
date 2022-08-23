package com.edomar.battleship.entities.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.edomar.battleship.entities.transforms.Transform;
import com.edomar.battleship.entities.components.interfaces.GraphicsComponent;
import com.edomar.battleship.entities.specifications.ObjectSpec;
import com.edomar.battleship.utils.BitmapStore;

/**
 * Componente grafico per le armi
 */

public class AmmoGraphicsComponent implements GraphicsComponent {

    private String mBitmapName;

    @Override
    public void initialize(Context c, ObjectSpec spec, PointF objectSize) {
        mBitmapName = spec.getBitmapName();
        //Aggiungo la bitmap al bitmapStore
        BitmapStore.addBitmap(c, mBitmapName, objectSize.x, objectSize.y,true);
    }

    @Override
    public void draw(Canvas canvas, Paint paint, Transform t) {

        //Draw the ship
        Bitmap bitmap;

        bitmap = BitmapStore.getHorizontalBitmap(mBitmapName); //il missile è sempre in orizzontale

        canvas.drawBitmap(bitmap,
                t.getLocation().x,
                t.getLocation().y,
                paint);


    }
}
