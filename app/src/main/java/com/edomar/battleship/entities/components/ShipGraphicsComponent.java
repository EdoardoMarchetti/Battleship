package com.edomar.battleship.entities.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.edomar.battleship.entities.transforms.ShipTransform;
import com.edomar.battleship.entities.transforms.Transform;
import com.edomar.battleship.entities.components.interfaces.GraphicsComponent;
import com.edomar.battleship.entities.specifications.ObjectSpec;
import com.edomar.battleship.utils.BitmapStore;

/**
 * Componente grafico per le navi
 */


public class ShipGraphicsComponent implements GraphicsComponent {

    private String mBitmapName;

    @Override
    public void initialize(Context c, ObjectSpec spec, PointF objectSize) {
        mBitmapName = spec.getBitmapName();
        //Aggiungo la bitmap al bitmapStore
        BitmapStore.addBitmap(c, mBitmapName, objectSize.x, objectSize.y,true);
    }

    @Override
    public void draw(Canvas canvas, Paint paint, Transform t) {

        Paint paintState = paint;
        ShipTransform sp = (ShipTransform) t;

        //Draw the collider if is movable
        if(t.getCollider().getColor() == "red" || sp.checkMovable()) {
            t.getCollider().draw(canvas, paint);
        }

        paint = paintState;

        //Draw the ship
        Bitmap bitmap;

        if(sp.isVertical()){
            bitmap = BitmapStore.getVerticalBitmap(mBitmapName);
        }else{
            bitmap = BitmapStore.getHorizontalBitmap(mBitmapName);
        }

        canvas.drawBitmap(bitmap,
                t.getLocation().x,
                t.getLocation().y,
                paint);


    }
}
