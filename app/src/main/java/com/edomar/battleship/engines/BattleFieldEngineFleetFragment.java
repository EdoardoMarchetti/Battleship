package com.edomar.battleship.engines;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.edomar.battleship.entities.InputObserver;
import com.edomar.battleship.entities.gameObject.GameObject;
import com.edomar.battleship.utils.WriterReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BattleFieldEngineFleetFragment extends AbstractBattleFieldEngine {



    /**---------------------------------------------------------------------------------------------
     * ----------------------------METODI GESTIONE SURFACEVIEW -------------------------------------
     * ------------------------------------------------------------------------------------------**/
    public BattleFieldEngineFleetFragment(Context context) {
        super(context);
    }

    public BattleFieldEngineFleetFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BattleFieldEngineFleetFragment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /** --------------------------------------------------------------------------------------------
     * -----------------------------METODI INIZIALIZZAZIONE CAMPO DA GIOCO--------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public void addObserver(InputObserver o) {
        inputObservers.add(o);
    }


    /** --------------------------------------------------------------------------------------------
     * -----------------------------METODI GESTIONE THREAD------------------------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public void run() {

        Handler threadHandler = new Handler(Looper.getMainLooper());
        threadHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        mRenderer.drawGridCoordinates(mLetters, mNumbers, mLettersDimen);
                    }
                });



        while (mRunning){
            long frameStartTime = System.currentTimeMillis();
            ArrayList<GameObject> objects = mLevelManager.getObjects();

            /** Update the objects **/
            mPhysicsEngine.update(mFPS, mParticleSystem,
                    mGrid, this);



            /** Draw objects **/
            mRenderer.draw(mGrid, objects, mParticleSystem, gameState);

            // Measure the frames per second in the usual way
            long timeThisFrame = System.currentTimeMillis()
                    - frameStartTime;
            if (timeThisFrame >= 1) {
                final int MILLIS_IN_SECOND = 1000;
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }


        }

    }


    /** --------------------------------------------------------------------------------------------
     * -------------------------------METODI SALVATAGGIO FLOTTA-------------------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public boolean saveFleet(String levelToLoad, String playerName, boolean timeEnded) { //Utilizzato per salvare la flotta di defualt
        ArrayList<GameObject> objects = mLevelManager.getObjects();
        mGrid.clearGrid();

        if(mLevelManager.checkCorrectFleetConfiguration()) { //Verifico se le navi sono in posizioni valide per le regole del livello

            //"Traduco" la nave in una matrice da salvare in un csv
            for (int i = 0; i < mLevelManager.getNumShipsInLevel(); i++) {
                //Ottengo la riga
                int startRow = (int) (objects.get(i)
                        .getTransform()
                        .getLocation().y / mGrid.getBlockDimension());

                //Ottengo la colonna
                int startColumn = (int) (objects.get(i)
                        .getTransform()
                        .getLocation().x / mGrid.getBlockDimension());

                //Dimensione della nave
                float shipWidth = objects.get(i)
                        .getTransform()
                        .getObjectWidth();

                float shipHeight = objects.get(i)
                        .getTransform()
                        .getObjectHeight();


                boolean shipIsVertical;
                int blockOccupied;

                //Imposto dimensione e orientamento della nave
                if (shipWidth >= shipHeight) {
                    shipIsVertical = false;
                    blockOccupied = (int) (shipWidth / mGrid.getBlockDimension());
                } else {
                    shipIsVertical = true;
                    blockOccupied = (int) (shipHeight / mGrid.getBlockDimension());
                }

                String gridTag = objects.get(i)
                        .getGridTag() + String.valueOf(i);

                //Posizione la nave sulla griglia
                mGrid.positionShip(startRow, startColumn, blockOccupied, shipIsVertical, gridTag);
            }

            //Ottengo la griglia per poi salvarla
            List<String[]> gridRows = new ArrayList<>();

            //Transform the matrix in an ArrayList
            gridRows.addAll(Arrays.asList(mGrid.getGridConfiguration()));

            //write on file
            if(WriterReader.getInstance().writeGrid(gridRows, "default",levelToLoad)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public void repositionShips(String level, String playerName) {
        //Do nothing
    }



    /**Gestione dei touch**/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Ad ogni evento touch attivo tutti gli observer delle navi (come in fase di preMatch)
        for (InputObserver io : inputObservers) {
            io.handleInput(event,
                    mGrid, mLevelManager,
                    gameState, notifyNumber);
        }

        return true;
    }




}
