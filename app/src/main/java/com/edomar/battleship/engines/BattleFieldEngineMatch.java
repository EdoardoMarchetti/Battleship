package com.edomar.battleship.engines;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.edomar.battleship.entities.InputObserver;
import com.edomar.battleship.entities.gameObject.GameObject;
import com.edomar.battleship.entities.grid.Grid;
import com.edomar.battleship.entities.levels.LevelManager;
import com.edomar.battleship.utils.WriterReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BattleFieldEngineMatch extends AbstractBattleFieldEngine {

    /**---------------------------------------------------------------------------------------------
     * ----------------------------METODI GESTIONE SURFACEVIEW -------------------------------------
     * ------------------------------------------------------------------------------------------**/
    public BattleFieldEngineMatch(Context context) {
        super(context);
    }

    public BattleFieldEngineMatch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BattleFieldEngineMatch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    /** --------------------------------------------------------------------------------------------
     * ---------------------------METODI INIZIALIZZAZIONE CAMPO DA GIOCO----------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public void init(String level, String playerName, String adversarialName) {
        gameState = 1;
        super.init(level, playerName, adversarialName);
    }

    @Override
    public void addObserver(InputObserver o) {//Probabilmente solo battleField giocatore e fleetFragment sempre
        inputObservers.add(o);
    }

    @Override
    public void setLevel(String levelToLoad, String playerName) {
        inputObservers.clear();
        inputObservers.add(mGridController);

        mLevelManager = new LevelManager(getContext(), this.getLayoutParams().width, this, levelToLoad);

        mGrid = new Grid(this.getLayoutParams().width);

        if(this.adversarialName.equals("computer")){ //Se l'avversario è il computer posiziona in maniera randomica
            autoPosition();
        }else{ //altrimenti leggi il rispettivo file
            List<String[]> gridRows = WriterReader.getInstance().readFleet("match"+this.adversarialName ,levelToLoad);
            mGrid.setDispositionInGrid(gridRows);
        }

        deSpawnRespawn(false); //Non mostro le navi poichè è il turno di un giocatore "umano"
    }

    /**
     * autoPosition
     *
     * Questo metodo viene invocato solo qunado l'avversario è il computer.
     * In questo caso invece di andare a recuperare la flotta da un csv le navi vengono posizionate tutte randomicamente
     */
    private void autoPosition() {


        String[] gridTag = new String[mLevelManager.getNumShipsInLevel()];
        Random random = new Random();
        float blockSize = mGrid.getBlockDimension();

        for (int i = 0; i < mLevelManager.getNumShipsInLevel(); i++) {
            gridTag[i] = mLevelManager.getObjects()
                    .get(i)
                    .getGridTag();
        }

        for(int i = 0; i < gridTag.length; i++ ){


            boolean isVertical = random.nextBoolean();
            int blockOccupied = (int) (mLevelManager.getObjects()
                    .get(i)
                    .getTransform()
                    .getObjectHeight() / blockSize); //By default all objects are vertical


            boolean goodPosition;
            int startColumn;
            int startRaw;

            do{//tramite il ciclo cerco una valida posizione della nave (comprensiva di distanza se serve)
                goodPosition = true;
                startColumn = random.nextInt(10);
                startRaw = random.nextInt(10);


                if(isVertical){
                    if(!(startRaw+blockOccupied<=9)){
                        startRaw = startRaw-blockOccupied;
                    }
                }else{
                    if(!(startColumn+blockOccupied<=9)){
                        startColumn = startColumn-blockOccupied;
                    }
                }


                if(isVertical){//NAVE VERTICALE

                    if(mLevelManager.needDistance()){
                        //controlla il contorno
                        for (int j = startRaw-1; j <= startRaw+blockOccupied+1 && goodPosition; j++) {
                            if(j >= 0 && j<10 ){
                                for (int k = startColumn-1; k <= startColumn+1 && goodPosition; k++) {
                                    if(k >= 0 && k < 10){
                                        if(!mGrid.getGridConfiguration()[j][k].equalsIgnoreCase("0")){
                                            goodPosition=false;
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        for (int j = startRaw; j < startRaw+blockOccupied && goodPosition; j++) {
                            Log.d("ShipPosition", "autoPosition: ["+j+"]["+startColumn+"] ="+ mGrid.getGridConfiguration()[j][startColumn] );
                            if(!mGrid.getGridConfiguration()[j][startColumn].equalsIgnoreCase("0")){
                                goodPosition=false;
                            }
                        }
                    }

                }else{//NAVE ORIZZONTALE
                    if(mLevelManager.needDistance()){
                        //controlla il contorno
                        for (int j = startRaw-1; j <= startRaw+1 && goodPosition; j++) {
                            if(j >= 0 && j<10 ){
                                for (int k = startColumn-1; k <=startColumn+blockOccupied+1 && goodPosition; k++) {
                                    if(k >= 0 && k < 10){
                                        Log.d("ShipPosition", "autoPosition: ["+j+"]["+k+"] ="+ mGrid.getGridConfiguration()[j][k]);
                                        if(!mGrid.getGridConfiguration()[j][k].equalsIgnoreCase("0")){
                                            goodPosition=false;
                                        }
                                    }
                                }
                            }
                        }

                    }else{
                        for (int j = startColumn; j < startColumn+blockOccupied && goodPosition; j++) {
                            Log.d("ShipPosition", "autoPosition: ["+startRaw+"]["+j+"] ="+ mGrid.getGridConfiguration()[startRaw][j] );
                            if(!mGrid.getGridConfiguration()[startRaw][j].equalsIgnoreCase("0")){
                                goodPosition =false;
                            }
                        }
                    }
                }



            }while(!goodPosition); //ripeto il ciclo fino a quando non trovo una posizione valide per la nave

            mGrid.positionShip(startRaw,startColumn, blockOccupied, isVertical, gridTag[i]+i);



        }

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


            if(notifyNumber != 0 && !notified){
                //Aggiorno il file per il post partita
                List <String[]> gridRows = new ArrayList<>();
                gridRows.addAll(Arrays.asList(mGrid.getGridConfiguration()));
                WriterReader.getInstance().writeGrid(gridRows,"match"+ this.adversarialName, this.level);

                if(notifyNumber == 1 || notifyNumber == 3){ //Colpo eseguito
                    //Transform the matrix in an ArrayList
                    mFragmentReference.notifyHit(mGrid.getLastHitCoordinates().x,mGrid.getLastHitCoordinates().y, mGrid.getLastHitResult());
                    notified = true;
                }else if(notifyNumber == 2){ //Partita terminata
                    mFragmentReference.notifyEndGame();
                    notified = true;
                }

            }

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
     * -----------------------------METODI GESTIONE GIOCO-------------------------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Ad ogni evento touch attivo tutti gli observer delle navi (come in fase di preMatch)
        for (InputObserver io : inputObservers) {
            io.handleInput(event,
                    mGrid, mLevelManager,
                    1, notifyNumber);
        }
        return true;
    }




    /** --------------------------------------------------------------------------------------------
     * -----------------------METODI ASTRATTI DI IBATTLEFIELD NON UTILIZZATI------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public boolean saveFleet(String levelToLoad, String playerName, boolean timeEnded) {
        //DO NOTHING
        return false;
    }

    @Override
    public void repositionShips(String level,  String playerName) {
        //Do nothing
    }

}
