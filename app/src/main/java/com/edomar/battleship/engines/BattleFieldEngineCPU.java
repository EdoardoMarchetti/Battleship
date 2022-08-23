package com.edomar.battleship.engines;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.edomar.battleship.entities.InputObserver;
import com.edomar.battleship.entities.gameObject.GameObject;
import com.edomar.battleship.entities.grid.Grid;
import com.edomar.battleship.entities.levels.LevelManager;
import com.edomar.battleship.utils.WriterReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleFieldEngineCPU extends AbstractBattleFieldEngine {

    private static final String TAG = "BattleFieldCPU";


    private ArtificialPlayer mAlterArtificialPlayer;

    private int lastRow;
    private int lastColumn;



    /**---------------------------------------------------------------------------------------------
     * ----------------------------METODI GESTIONE SURFACEVIEW -------------------------------------
     * ------------------------------------------------------------------------------------------**/
    public BattleFieldEngineCPU(Context context) {
        super(context);
    }

    public BattleFieldEngineCPU(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public BattleFieldEngineCPU(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        super.surfaceCreated(surfaceHolder);
        shot();
    }



    /** --------------------------------------------------------------------------------------------
     * ---------------------------METODI INIZIALIZZAZIONE CAMPO DA GIOCO----------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public void init(String level, String playerName, String adversarialName) {
        gameState = 1;

        super.init(level, playerName, adversarialName);

        mAlterArtificialPlayer = new ArtificialPlayer();
    }

    @Override
    public void setLevel(String levelToLoad, String playerName) {
        //Creo gli oggetti da dover utilizzare nella partita (navi e armi)
        mLevelManager = new LevelManager(getContext(), this.getLayoutParams().width, this, levelToLoad);
        //Creo l'oggetto griglia
        mGrid = new Grid(this.getLayoutParams().width);

        //Passo all'oggetto griglia il risultato della lettura del file con la flotta
        List<String[]> gridRows = WriterReader.getInstance().readFleet("match"+this.adversarialName,levelToLoad);
        mGrid.setDispositionInGrid(gridRows);

        deSpawnRespawn(true); //Mostro le navi poichè la griglia visualizzata è quella del giocatore umano
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
                //Aggiorno la griglia nel file per il post-partita
                List <String[]> gridRows = new ArrayList<>();
                gridRows.addAll(Arrays.asList(mGrid.getGridConfiguration()));
                WriterReader.getInstance().writeGrid(gridRows,"match"+ this.adversarialName, this.level);

                //Avviso il fragment che il colpo è stato effettuato, quindi può richiedere all'activity di fare lo switch
                if(notifyNumber == 1){ //E' stato effettuato un colpo
                    mFragmentReference.notifyHit(mGrid.getLastHitCoordinates().x,mGrid.getLastHitCoordinates().y, mGrid.getLastHitResult());
                    notified = true;
                }else if(notifyNumber == 2){ //Partita completata
                    mFragmentReference.notifyEndGame();
                    notified = true;
                }else if(notifyNumber == 3 ){ //Nave affondata
                    mFragmentReference.notifyHit(mGrid.getLastHitCoordinates().x,mGrid.getLastHitCoordinates().y, mGrid.getLastHitResult());
                    notified = true;
                    if(mLevelManager.needDistance()) {
                        mAlterArtificialPlayer.updateListForDistance(lastRow, lastColumn);
                    }
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
    /**
     * shot
     * Indica all'artificial player di sparare,
     * facendo poi un check se quel colpo è stato già effettuato oppure no
     */
    public void shot(){

        Point coordinates;
        int column;
        int row;
        boolean shot = false;

        do {
            coordinates = mAlterArtificialPlayer.shot();
            column = coordinates.y;
            row = coordinates.x;



            if (!mGrid.getGridConfiguration()[row][column].equalsIgnoreCase("S") && !mGrid.getGridConfiguration()[row][column].equalsIgnoreCase("X")) {
                if (mGrid.getGridConfiguration()[row][column].equalsIgnoreCase("0")) {
                    mGrid.setLastHit(row, column, false);
                    mAlterArtificialPlayer.updateList(row, column, false);
                    shot = true;
                } else {
                    mGrid.setLastHit(row, column, true);
                    mAlterArtificialPlayer.updateList(row, column, true);
                    shot = true;
                }
            }
        }while (!shot);

        lastRow = row;
        lastColumn = column;

        spawnAmmo(row, column);
    }



    /** --------------------------------------------------------------------------------------------
     * -----------------------METODI ASTRATTI DI IBATTLEFIELD NON UTILIZZATI------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public boolean saveFleet(String levelToLoad, String playerName, boolean timeEnded) {
        //DO Nothing
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Do nothing
        return false;
    }

    @Override
    public void repositionShips(String level,  String playerName) {
        //Do Nothing
    }

    @Override
    public void addObserver(InputObserver o) {
        //DO Nothing
    }
}
