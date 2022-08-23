package com.edomar.battleship.entities.transforms;

import android.graphics.PointF;

public class AmmoTransform extends Transform {

    private float mSpeed;

    public AmmoTransform(float speed, float objectWidth, float objectHeight, PointF startLocation, float gridDimension) {
        super(objectWidth, objectHeight, startLocation, gridDimension);
        mSpeed = speed;
    }

    public float getSpeed(){
        return mSpeed;
    }
}
