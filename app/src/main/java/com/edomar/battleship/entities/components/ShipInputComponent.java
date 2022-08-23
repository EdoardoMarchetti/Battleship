package com.edomar.battleship.entities.components;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.edomar.battleship.engines.AbstractBattleFieldEngine;
import com.edomar.battleship.engines.BattleFieldBroadcaster;
import com.edomar.battleship.entities.grid.Grid;
import com.edomar.battleship.entities.InputObserver;
import com.edomar.battleship.entities.levels.LevelManager;
import com.edomar.battleship.entities.transforms.ShipTransform;
import com.edomar.battleship.entities.transforms.Transform;
import com.edomar.battleship.entities.components.interfaces.InputComponent;

/**
 * Componente per la gestione degli input sulle navi
 */

public class ShipInputComponent implements InputComponent, InputObserver
        {


   
    private ShipTransform mTransform;

    float mDownX;
    float mDownY;

    float mCurrentX;
    float mCurrentY;


    float SCROLL_THRESHOLD;
    boolean isOnClick;



    //Il costruttore viene chiamato dal GameObjectFactory
    public ShipInputComponent(BattleFieldBroadcaster bf) {
        bf.addObserver(this);
    }
   
    @Override
    public void setTransform(Transform t) {
        mTransform = (ShipTransform) t;
    }

    @Override
    public void handleInput(MotionEvent event, Grid grid, LevelManager levelManager, int gameState, int notifyNumber) {
        //Primo check sullo stato del gioco (if statement da inserire)
        //1->Match -> l'input non è mai rivolto alla nave
        //0->PreMatch -> l'input è sempre rivolto alla nave -> verificare se si la nave è già "attiva"
        //                                              -> verificare tipo di evento 
        //                                              -> ruota / D&D
        //                                              -> indicare nella model della griglia la nuova posizione della nave
        

        //Soglia minima per identificare uno scroll
        SCROLL_THRESHOLD = mTransform.getBlockDimension() / 10;

        RectF shipCollider = mTransform.getCollider();


        int eventType = event.getAction() & MotionEvent.ACTION_MASK;

        if(gameState == 0 &&  eventType == MotionEvent.ACTION_DOWN ){
            mDownX = event.getX();
            mDownY = event.getY();
            mCurrentX = mDownX;
            mCurrentY = mDownY;

            //Verifico se l'evento coinvolge la nave
            if(shipCollider.contains(mDownX,mDownY) && levelManager.getCurrentLevel().transformInMovement == null) {
                //La imposto come movable, quindi la posso spostare
                mTransform.setMovable();
                levelManager.getCurrentLevel().transformInMovement = mTransform;
                isOnClick = true;

            }else{
                mTransform.setImmovable();
                mTransform.setNotRotatable();
            }

        }

        //Se la nave è selezionata
        if(levelManager.getCurrentLevel().transformInMovement == mTransform){
            switch (eventType){

                case MotionEvent.ACTION_MOVE: //Gestione drag

                    if(mTransform.checkMovable()){
                        drag(event.getX(), event.getY());

                        mCurrentX = event.getX();
                        mCurrentY = event.getY();
                    }

                    if(Math.abs(mDownX-mCurrentX) > SCROLL_THRESHOLD || Math.abs(mDownY-mCurrentY ) > SCROLL_THRESHOLD){
                        isOnClick = false;
                    }

                    break;

                case MotionEvent.ACTION_UP: //rilascio nave, drop o rotazione

                    if(isOnClick){ //rotate or active
                        if(mTransform.checkMovable() && mTransform.checkRotatable()){
                            mTransform.rotate();
                            rotate();

                        }
                    }else{
                        //Drop the ship
                        //drop(event.getX(), event.getY());
                        drop();

                    }

                    mTransform.setRotatable();

                    if(!(levelManager.getCurrentLevel().transformInMovement == null)) {
                        levelManager.getCurrentLevel().transformInMovement = null;
                    }

                    //Verifico se la nave è in una posizione ammessa
                    levelManager.checkCorrectFleetConfiguration();

                    break;

            }

            
        }
        
    }



    private void drag(float eventX, float eventY) {
        //First check if the ship is outside the BattleField
        Log.d("Dragging", "drag: ");
        float differenceX = (eventX - mCurrentX);
        float differenceY = (eventY - mCurrentY);


        PointF oldLocation = mTransform.getLocation();
        PointF newLocation = new PointF(oldLocation.x + differenceX, oldLocation.y + differenceY);

        //Left
        if(newLocation.x <= 0){
            newLocation.x = 0;
        }
        //Top
        if(newLocation.y <= 0){
            newLocation.y = 0;
        }
        //Right
        if(newLocation.x + mTransform.getObjectWidth() >= mTransform.getGridDimension()){
            newLocation.x = mTransform.getGridDimension() - mTransform.getObjectWidth();
        }
        //Bottom
        if(newLocation.y + mTransform.getObjectHeight() >= mTransform.getGridDimension()){
            newLocation.y = mTransform.getGridDimension() - mTransform.getObjectHeight();
        }

        mTransform.setLocation(newLocation.x, newLocation.y);

    }

    private void rotate() {
        PointF newLocation = new PointF( );
        double x = mTransform.getLocation().x;
        double y = mTransform.getLocation().y;


        if(x + mTransform.getObjectWidth() >= mTransform.getGridDimension()){
            x = mTransform.getGridDimension() - mTransform.getObjectWidth();
        }

        if(y + mTransform.getObjectHeight() >= mTransform.getGridDimension()){
            y = mTransform.getGridDimension() - mTransform.getObjectHeight();
        }

        x = x / mTransform.getBlockDimension();
        y = y / mTransform.getBlockDimension();

        if(Math.round(x) >= 10 || Math.round(y) >= 10){
            newLocation.x = mTransform.getBlockDimension() * (int) x;
            newLocation.y = mTransform.getBlockDimension() * (int) y;
        }else{
            newLocation.x = mTransform.getBlockDimension() * Math.round(x);
            newLocation.y = mTransform.getBlockDimension() * Math.round(y);
        }


        mTransform.setLocation(newLocation.x, newLocation.y);
    }

    private void drop() {
        PointF oldLocation = mTransform.getLocation();
        PointF newLocation = new PointF();


        double x =  (oldLocation.x / mTransform.getBlockDimension());
        double y =  (oldLocation.y / mTransform.getBlockDimension());


        if(Math.round(x) >= 10 || Math.round(y) >= 10){
            newLocation.x = mTransform.getBlockDimension() * (int) x;
            newLocation.y = mTransform.getBlockDimension() * (int) y;
        }else{
            newLocation.x = mTransform.getBlockDimension() * Math.round(x);
            newLocation.y = mTransform.getBlockDimension() * Math.round(y);
        }

        mTransform.setLocation(newLocation.x, newLocation.y);
    }



}
