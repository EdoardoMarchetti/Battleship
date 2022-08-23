package com.edomar.battleship.entities.transforms;

import android.graphics.PointF;


/**
 * Questa classe permette di salvare le informazioni su un gameObject
 */

public class Transform {





    /** Private Variables **/
    private Collider mCollider;
    private PointF mLocation;
    private float mObjectHeight; //ridimensionata rispetto alla dimensione dei blocchi
    private float mObjectWidth; //ridimensionata rispetto ai blocchi
    private boolean mIsVertical = true; //solo alla nave




    /** Variabili di utilità **/
    private static float sGridDimension;
    private static float sBlockDimension;


    /**
     * Costruttore
     * @param objectWidth lunghezza dell'oggetto rispetto al numero di blocchi che deve occupare
     * @param objectHeight altezza dell'oggetto rispetto al numero di blocchi che deve occupare
     * @param startLocation posizione iniziale
     * @param gridDimension dimensioni della griglia (utile per averla sempre a disposizione)
     */
    public Transform (float objectWidth, float objectHeight, PointF startLocation, float gridDimension){
        mCollider = new Collider();
        mObjectHeight = objectHeight;
        mObjectWidth = objectWidth;
        mLocation = startLocation;
        sGridDimension = gridDimension;
        sBlockDimension = gridDimension / 10;
    }

    /** Getters **/
    public float getGridDimension(){
        return sGridDimension;
    }

    public float getBlockDimension(){
        return sBlockDimension;
    }

    public boolean isVertical() {
        return mIsVertical;
    }

    public float getObjectHeight(){
        return mObjectHeight;
    }

    public float getObjectWidth() {
        return mObjectWidth;
    }

    public PointF getLocation() {
        return mLocation;
    }

    public Collider getCollider(){
        return mCollider;
    }



    /** Setters **/

    public void setVertical(){
        mIsVertical = true;
    }

    public void setHorizontal(){
        mIsVertical = false;
    }

    public void rotate(){
        mIsVertical = !mIsVertical;
        invertDimension();
    }

    //Usato per la rotazione dell'oggetto
    private void invertDimension() {
        float support = mObjectWidth;
        mObjectWidth = mObjectHeight;
        mObjectHeight = support;
    }


    public void setLocation(float horizontal, float vertical){
        mLocation = new PointF(horizontal, vertical);
        updateCollider();
    }

    public void setStartLocation(float horizontalCoo, float verticalCoo, boolean isV){
        mLocation = new PointF(horizontalCoo, verticalCoo);
        mIsVertical = isV;

        if(!mIsVertical){
            invertDimension();
        }
        updateCollider();
    }

    //Permette di aggiornare il collider quando la nave si muove o viene ruotata
    public void updateCollider(){
        mCollider.top = mLocation.y;
        mCollider.left = mLocation.x;
        mCollider.bottom = mLocation.y + mObjectHeight;
        mCollider.right = mLocation.x + mObjectWidth;

    }



}
