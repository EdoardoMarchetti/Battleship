package com.edomar.battleship.entities.transforms;

import android.graphics.PointF;

public class ShipTransform extends Transform {



    private boolean mIsMovable= false;
    private boolean mRotatable = false;
    private int mLives;



    public ShipTransform(float objectWidth, float objectHeight, PointF startLocation, float gridDimension) {
        super(objectWidth, objectHeight, startLocation, gridDimension);
        mLives = (int) (Math.max(objectWidth, objectHeight) / super.getBlockDimension()) ;
    }

    /**-----Rendo la nave ruotabile e/o pronta ad essere mossa ---**/
    public void setMovable(){
        mIsMovable = true;
    }

    public void setImmovable(){
        mIsMovable = false;
    }

    public boolean checkMovable(){
        return mIsMovable;
    }


    public void setRotatable(){
        mRotatable = true;
    }

    public void setNotRotatable(){
        mRotatable = false;
    }

    public boolean checkRotatable(){
        return mRotatable;
    }


    public int getLives(){
        return mLives;
    }

    public void shipHit(){
        mLives = mLives-1;
    }








}
