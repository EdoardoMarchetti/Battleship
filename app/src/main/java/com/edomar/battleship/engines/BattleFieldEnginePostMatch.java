package com.edomar.battleship.engines;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.edomar.battleship.entities.InputObserver;
import com.edomar.battleship.entities.grid.Grid;
import com.edomar.battleship.utils.WriterReader;

import java.util.List;

public class BattleFieldEnginePostMatch extends AbstractBattleFieldEngine {

    /**---------------------------------------------------------------------------------------------
     * ----------------------------METODI GESTIONE SURFACEVIEW -------------------------------------
     * ------------------------------------------------------------------------------------------**/
    public BattleFieldEnginePostMatch(Context context) {
        super(context);
    }

    public BattleFieldEnginePostMatch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BattleFieldEnginePostMatch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    /** --------------------------------------------------------------------------------------------
     * ---------------------------METODI INIZIALIZZAZIONE CAMPO DA GIOCO----------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public void init(String level, String playerName, String adversarialName) {
        gameState = 2;
        setLevel(level, playerName);
        mRenderer = new Renderer(this, mContext);
    }

    @Override
    public void setLevel(String levelToLoad, String playerName) {
        mGrid = new Grid(this.getLayoutParams().width);
        List<String[]> gridRows = WriterReader.getInstance().readFleet("match"+ playerName,levelToLoad);
        mGrid.setDispositionInGrid(gridRows);
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
                //Qua va il codice per disegnare le coordinate
                mRenderer.drawGridCoordinates(mLetters, mNumbers, mLettersDimen);
            }
        });


        while (mRunning){
            long frameStartTime = System.currentTimeMillis();



            Log.d("NotifyNumber", "run: notifyNumber = "+notifyNumber);
            /** Draw objects **/
            mRenderer.draw(mGrid, null, mParticleSystem, gameState);

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
     * -----------------------METODI ASTRATTI DI IBATTLEFIELD NON UTILIZZATI------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean saveFleet(String levelToLoad, String playerName, boolean timeEnded) {
        return false;
    }

    @Override
    public void repositionShips(String level, String playerName) {

    }

    @Override
    public void addObserver(InputObserver o) {

    }
}
