package com.edomar.battleship.engines;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.edomar.battleship.entities.InputObserver;
import com.edomar.battleship.utils.OnlineGameInfo;
import com.edomar.battleship.entities.particleSystem.ParticleSystem;
import com.edomar.battleship.entities.gameObject.GameObject;
import com.edomar.battleship.entities.grid.Grid;
import com.edomar.battleship.entities.grid.GridInputController;
import com.edomar.battleship.entities.levels.LevelManager;
import com.edomar.battleship.utils.BitmapStore;
import com.edomar.battleship.utils.WriterReader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;

public class BattleFieldEngineOnlineMatch extends AbstractBattleFieldEngine {


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    private OnlineGameInfo onlineGameInfo;
    private List<String[]>  mGridRows;



    /**---------------------------------------------------------------------------------------------
     * ----------------------------METODI GESTIONE SURFACEVIEW -------------------------------------
     * ------------------------------------------------------------------------------------------**/
    public BattleFieldEngineOnlineMatch(Context context) {
        super(context);
    }

    public BattleFieldEngineOnlineMatch(Context context, AttributeSet attrs) {
        super(context, attrs);
        //onlineGameInfo = OnlineGameInfo.getInstance();

    }

    public BattleFieldEngineOnlineMatch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** --------------------------------------------------------------------------------------------
     * ---------------------------METODI INIZIALIZZAZIONE CAMPO DA GIOCO----------------------------
     * ------------------------------------------------------------------------------------------**/
    @Override
    public void init(String level, String playerName, String adversarialName) {

        this.level = level;
        this.interactivePlayerName = playerName;
        this.adversarialName = adversarialName;
        onlineGameInfo = OnlineGameInfo.getInstance();


        gameState = 1;

        mBitmapStore = BitmapStore.getInstance(getContext(), this.getLayoutParams().width /10);
        mGridController = new GridInputController(this, this);


        if (interactivePlayerName.equalsIgnoreCase(onlineGameInfo.getOtherPlayer())) { //Il giocaotre interattivo è il giocatore online
            setLevel(level, onlineGameInfo.getLocalPlayer()); //Viene visualizzato il campo di battaglia del giocatore locale

            //Aggiugni il listener per il database per far partire il colpo quando viene notificato nel database
            mRef.child("hit" + onlineGameInfo.getScenario())
                    .child(onlineGameInfo.getMatchID())
                    .child("lastHit" + onlineGameInfo.getOtherPlayer())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.getValue() != null) {
                                //Viene eseguito ogni volta che viene notificato un colpo da parte dell'avversario online
                                String[] lasHit = ((String) snapshot.getValue()).split(",");
                                if (mGrid != null) {
                                    mGrid.setLastHit(Integer.parseInt(lasHit[0]), Integer.parseInt(lasHit[1]), Boolean.parseBoolean(lasHit[2]));
                                }
                                spawnAmmo(Integer.parseInt(lasHit[0]), Integer.parseInt(lasHit[1]));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        }
        //Il giocatore interattivo è il giocatore locale e quindi mostro il campo di battaglia dell'avversario
        //Gestisco il tutto come se dovessi leggere da file grazie al metodo readFleetFromDatabase
        else{
            mLevelManager = new LevelManager(getContext(), this.getLayoutParams().width, this, level); //inizializzo il level manager per preparare gli oggetti necessari
            mGrid = new Grid(this.getLayoutParams().width); // inizializzo la griglia
            mGridRows = new ArrayList<String[]>(); //lista delle righe che dovranno poi essere usate per settare la Grid
            mRef.child("playing"+onlineGameInfo.getScenario())
                    .child(onlineGameInfo.getMatchID())
                    .child(onlineGameInfo.getOtherPlayer()+"Fleet")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                readFleetFromDatabase(snapshot); //recupero la flotta del giocatore avversario
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        //Finisco di inizializzare gli altri elementi
        mPhysicsEngine = new PhysicsEngine(mLevelManager);
        mParticleSystem = new ParticleSystem();
        mParticleSystem.init(250);
        mRenderer = new Renderer(this, mContext);
    }

    //setLevel invocato se il giocatore interattivo è il giocatore online
    @Override
    public void setLevel(String levelToLoad, String playerName) {

        inputObservers.clear();
        inputObservers.add(mGridController);

        mLevelManager = new LevelManager(getContext(), this.getLayoutParams().width, this, levelToLoad);

        //carico in griglia il risultato della lettura del file salvato in memoria
        mGrid = new Grid(this.getLayoutParams().width);
        mGridRows = WriterReader.getInstance().readFleet("match"+playerName ,levelToLoad);
        mGrid.setDispositionInGrid(mGridRows);

        deSpawnRespawn(true); //Le navi vengono mostrate poichè visualizzo la griglia del giocaotre locale


    }

    /**
     * readFleetFromDatabase
     *
     * Invocato da init qual'ora devo ottenere la flotta dell'avversario dal database
     * @param snapshot
     */
    private void readFleetFromDatabase(DataSnapshot snapshot) {

        Iterator i = snapshot.getChildren().iterator();
        boolean done = false;

        for (int j = 0; j < 10 ; j++) {
            String[] row = new String[10];

            for (int k = 0; k < 10; k++) {
                if(i.hasNext()){
                    row[k] = (String) ((DataSnapshot)i.next()).getValue();
                }
            }

            mGridRows.add(row);
        }



        mGrid.setDispositionInGrid(mGridRows);
        deSpawnRespawn(false); //Non mostro le navi perchè viene visualizzata la griglia avversaria
    }

    @Override
    public void addObserver(InputObserver o) {//Probabilmente solo battleField giocatore e fleetFragment sempre
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


            if(notifyNumber != 0 && !notified){
                List <String[]> gridRows = new ArrayList<>();
                gridRows.addAll(Arrays.asList(mGrid.getGridConfiguration()));

                //Aggiorno il file per il postMatch
                WriterReader.getInstance().writeGrid(gridRows,"match"+ this.adversarialName, this.level);


                if(notifyNumber == 1 || notifyNumber == 3){ //Colpo eseguito
                    if(interactivePlayerName.equalsIgnoreCase(onlineGameInfo.getLocalPlayer())){
                        mRef.child("hit"+onlineGameInfo.getScenario())
                                .child(onlineGameInfo.getMatchID())
                                .child("lastHit"+ interactivePlayerName)
                                .setValue(mGrid.getLastHitCoordinates().x+","+mGrid.getLastHitCoordinates().y+","+mGrid.getLastHitResult());
                    }
                    mFragmentReference.notifyHit(mGrid.getLastHitCoordinates().x,mGrid.getLastHitCoordinates().y, mGrid.getLastHitResult());
                    notified = true;

                }else if(notifyNumber == 2){//Parita finita
                    if(interactivePlayerName.equalsIgnoreCase(onlineGameInfo.getLocalPlayer())){
                        mRef.child("hit"+onlineGameInfo.getScenario())
                                .child(onlineGameInfo.getMatchID())
                                .child("lastHit"+onlineGameInfo.getLocalPlayer())
                                .setValue(mGrid.getLastHitCoordinates().x+","+mGrid.getLastHitCoordinates().y+","+mGrid.getLastHitResult());
                    }
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
        if(interactivePlayerName.equalsIgnoreCase(onlineGameInfo.getLocalPlayer())){
            for (InputObserver io : inputObservers) {
                io.handleInput(event,
                        mGrid, mLevelManager,
                        gameState, notifyNumber);
            }
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
    public void repositionShips(String level, String playerName) {
        //Do nothing
    }
}
