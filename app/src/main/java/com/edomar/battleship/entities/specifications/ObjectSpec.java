package com.edomar.battleship.entities.specifications;

import android.graphics.PointF;

/** Questa è la calsse che tutte le Spec dovranno estendere**/

public class ObjectSpec {

    private String mTag;                //Tag dell'oggetto (Ship o Ammo)
    private String mGridTag;            //Tag con cui l'oggetto è riconosciuto in griglia
    private String mBitmapName;         //Nome della bitmap da utilizzare per l'oggetto
    private float mSpeed;               //Velocità dell'oggetto
    private PointF mBlocksOccupied;     //Blocchi occupati dall'oggetto
    private String[] mComponents;       //Lista dei componenti

    public ObjectSpec(String tag, String gTag, String bitmapName, float speed, PointF blocksOccupied, String[] components) {
        this.mTag = tag;
        this.mGridTag = gTag;
        this.mBitmapName = bitmapName;
        this.mSpeed = speed;
        this.mBlocksOccupied = blocksOccupied;
        this.mComponents = components;
    }

    /**----Getters----**/
    public String getTag() {
        return mTag;
    }

    public String getGridTag() {
        return mGridTag;
    }

    public String getBitmapName() {
        return mBitmapName;
    }

    public float getSpeed(){
        return mSpeed;
    }

    public PointF getBlocksOccupied() {
        return mBlocksOccupied;
    }

    public String[] getComponents() {
        return mComponents;
    }
}
