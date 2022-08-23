package com.edomar.battleship.engines;

/**
 * La classe AbstractBattleFieldEngine è il super tipo di ogni BattleField,
 * I metodi implemntati sono usati da tutte le sottoclassi
 * anche se modificati in alcuni casi.
 */

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.edomar.battleship.entities.InputObserver;
import com.edomar.battleship.entities.particleSystem.ParticleSystem;
import com.edomar.battleship.entities.gameObject.GameObject;
import com.edomar.battleship.entities.grid.Grid;
import com.edomar.battleship.entities.grid.GridInputController;
import com.edomar.battleship.entities.levels.LevelManager;
import com.edomar.battleship.utils.BitmapStore;
import com.edomar.battleship.utils.WriterReader;
import com.edomar.battleship.activities.gameplayFragments.IFragmentForBattlefield;


import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBattleFieldEngine extends SurfaceView implements SurfaceHolder.Callback,
        Runnable,
        AmmoSpawner,
        BattleFieldBroadcaster
{


    public final Context mContext;

    private Thread mThread;
    public boolean mRunning;                //Variabile booleana per verificare se il thread è attivo
    public long mFPS;


    /**GameState variables**/
    public int gameState = 0;               // 0 -> pre match; 1 -> in match; 2 -> post Match
    public String level;                    //livello corrente da visualizzare
    public String interactivePlayerName;    //indica quale giocatore interagisce
    public String adversarialName;          //indica il nome dell'avversario
    public int notifyNumber = 0;            // 0 allora colpo non eseguito
                                            // 1 allora razzo caduto
                                            // 2 allora gioco finito
                                            // 3 allora nave colpita e affondata
    public boolean notified = false;        // Evita che venga notificato più volte lo stesso colpo


    /** Instances **/
    public Renderer mRenderer;                                              //Renderer del campo di battaglia
    public PhysicsEngine mPhysicsEngine;                                    //Instanza del motore fisico (gestione collisione missile-nave)
    public ParticleSystem mParticleSystem;                                  //Particlesystem per effetto esplosione
    public Grid mGrid;                                                      //Oggetto griglia
    public BitmapStore mBitmapStore;                                        //Instanza del BitmapStore per la gestione delle bitmap
    public LevelManager mLevelManager;                                      //Instanza del LevelManager per gestione oggetti da dover visualizzare
    public ArrayList<InputObserver> inputObservers = new ArrayList<>();     //Lista degli observer attivi nel campo
    public GridInputController mGridController;                             //Gestore di eventi input che avvengono sulla griglia
    public IFragmentForBattlefield mFragmentReference;                      //Instanza del fragment a cui il Battlefield è legato



    /**Variabili per gestire le coordinate**/
    ImageView mLetters;
    ImageView mNumbers;
    Point mLettersDimen = new Point();



    /** Costruttori **/
    public AbstractBattleFieldEngine(Context context) {
        super(context);
        getHolder().addCallback(this);
        mContext = context;
    }

    public AbstractBattleFieldEngine(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        mContext = context;

    }

    public AbstractBattleFieldEngine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        mContext = context;
    }

    /**Metodi Callback**/
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    /** --------------------------------------------------------------------------------------------
    * -----------------------------METODI INIZIALIZZAZIONE CAMPO DA GIOCO---------------------------
    * -------------------------------------------------------------------------------------------**/

    /**
     * init
     * Questo metodo permette di inizializzare il Battleship dal fragment.
     *
     * @param level indica quale livello caricare Classic, Russian, Standard
     * @param playerName indica a quale giocatore appartiene il campo di gioco
     * @param adversarialName indica l'avversario
     */
    public void init(String level, String playerName, String adversarialName){

        this.level = level;
        this.interactivePlayerName = playerName;
        this.adversarialName = adversarialName;

        mBitmapStore = BitmapStore.getInstance(getContext(), this.getLayoutParams().width / 10);
        mGridController = new GridInputController(this, this);
        setLevel(this.level, this.interactivePlayerName); //Carico il livello richiesto
        mPhysicsEngine = new PhysicsEngine(mLevelManager);
        mParticleSystem = new ParticleSystem();
        mParticleSystem.init(250);
        mRenderer = new Renderer(this, mContext);


    }

    /**
     * setLevel
     *
     * carica il livello richiesto da init
     * utilizzato nel momento dell'inizializzazione,
     * ma anche quando in FLeetFragment viene richiesto di modificare lo scenario da visualizzare
     *
     * @param levelToLoad indica quale livello caricare Classic, Russian, Standard
     * @param playerName indica a quale giocatore appartiene il campo di gioco
     */
    public void setLevel(String levelToLoad, String playerName) {
        //Queste due righe di codice sono necesarie per il cambio di scenario richiesto da FleetFragment
        inputObservers.clear();
        inputObservers.add(mGridController);

        //Creo gli oggetti da dover utilizzare nella partita (navi e armi)
        mLevelManager = new LevelManager(getContext(), this.getLayoutParams().width, this, levelToLoad);
        //Imposto la griglia
        mGrid = new Grid(this.getLayoutParams().width);
        List<String[]> gridRows = WriterReader.getInstance().readFleet("default",levelToLoad);
        mGrid.setDispositionInGrid(gridRows);

        //Posizione i GameObject "Ship" sulla griglia nella maniera corretta (se true => li visualizzo, false => li tengo nascosti)
        deSpawnRespawn(true);
    }

    /**
     * deSpawnRespawn
     *
     * indica ad ogni oggetto NAVE presente nel livello quali sono le sue coordinate.
     * Dall'oggetto mGrid recupera la configurazione della griglia dove ci saranno scritti
     * i gridTag. Indica al transform se la nave deve andare in verticale o orizzontale e infine
     * tramite il metodo
     *
     *                  object.spawn(row, column, show)
     *
     * imposta le coordinate nel transform. Se show = true attiva la nave, altrimenti rimane inattiva (non viene visualizzata a schermo)
     * Se la nave non è stata trovata nella griglia verrà posizionata in alto a sinistra della griglia.
     *
     * @param show permette di definire se le navi devono essre fin da subito visibili
     */
    public void deSpawnRespawn(boolean show){
        //Recupero gli oggetti da inseriretramite LevelManager
        ArrayList<GameObject> objects = mLevelManager.getObjects();
        //Recupero la configurazione della griglia tramite Grid
        String[][] gridConfiguration = mGrid.getGridConfiguration();

        //Disattivo tutti gli ogetti
        for (GameObject o: objects) {
            o.setInactive();
        }

        //Per ogni nave recupero posizione e orientazione
        for(int i = 0; i<mLevelManager.getNumShipsInLevel(); i++) {
            //Di defualt la nave viene visualizzata in alto a sinistra
            int row = 0;
            int column = 0;
            boolean found = false;

            //Recupero il tag della nave
            String gridTag = objects.get(i)
                    .getGridTag() + String.valueOf(i); //String.valueOf(i) mi permette di distinguera tra navi dello stesso tipo

            //Cerco il tag in tutte le celle della griglia
            for (int j = 0; j < gridConfiguration.length && !found; j++) {
                for (int k = 0; k < gridConfiguration.length && !found; k++) {

                    //La prima cella che contiene il tag della nave setta il flag found a true
                    // così da far terminare la ricerca e iniziare il processo per la nave successiva
                    if(gridConfiguration[j][k].equals(gridTag)){

                        found = true;

                        try {
                            //Se la cella sotto ha lo stesso tag allora la nave va in verticale
                            if (gridConfiguration[j + 1][k].equals(gridTag)) {
                                //Imposto la nave in verticale
                                objects.get(i)
                                        .getTransform()
                                        .setVertical();
                                //Salvo la prima riga e la prima colonna
                                row = j;
                                column = k;
                            }
                        }catch (IndexOutOfBoundsException ioobe){
                            ioobe.printStackTrace();

                        }

                        try {
                            //Se la cella a destra ha lo stesso tag allora la nave va in orizzontale
                            if (gridConfiguration[j][k + 1].equals(gridTag)) {
                                //Imposto la nave in orizzontale
                                objects.get(i)
                                        .getTransform()
                                        .setHorizontal();
                                //Salvo la prima riga e la prima colonna
                                row = j;
                                column = k;
                            }
                        }catch (IndexOutOfBoundsException ioobe){
                            ioobe.printStackTrace();

                        }
                    }

                }
            }

            if(found) {//Verifico se la nave è trovata
                //Esiste il file e quindi disporre secondo la griglia restituita
                objects.get(i)
                        .spawn(row, column, show);
            }else{
                //Non esiste il file quindi disporre in maniera progressiva partendo da 0 0
                objects.get(i)
                        .spawn(0,i, show);
            }


        }


    }

    /**
     * setImageViewsForCoordinates
     * indica quali sono le ImageView
     * che dovranno contenere le coordinate della griglia
     *
     * @param letters imageView per le lettere
     * @param numbers imageView per i numeri
     */
    public void setImageViewsForCoordinates(ImageView letters, ImageView numbers){//in tutti i battlefield
        mLetters = letters;
        mNumbers = numbers;
        mLettersDimen.x = letters.getLayoutParams().width;
        mLettersDimen.y = letters.getLayoutParams().height;
    }

    /**
     * setFragmentReference
     * Permette di attacare ad ogni campo da gioco il fragment in cui è visualizzato
     * per invocare metodi di notifica
     *
     * @param fragmentReference fragment in cui è visualizzato il battlefield
     */
    public void setFragmentReference(IFragmentForBattlefield fragmentReference){
        mFragmentReference = fragmentReference;
    }

    /**
     * addObserver
     * Attacca al campo di gioco un'observer
     * Usato nei battleship interattivi.
     * @param o
     */
    @Override
    public abstract void addObserver(InputObserver o);



    /** --------------------------------------------------------------------------------------------
     * -----------------------------METODI GESTIONE THREAD------------------------------------------
     * ------------------------------------------------------------------------------------------**/
    public void stopThread() {
        mRunning = false;

        if(mLevelManager != null && mLevelManager.getObjects().get(LevelManager.MISSILE).checkActive()){
            mLevelManager.getObjects().get(LevelManager.MISSILE).setInactive();
        }

        if(mParticleSystem != null && mParticleSystem.isRunning()){
            mParticleSystem.stop();
        }

        try {
            mThread.join();
        } catch (InterruptedException e) {
            Log.e("Exception","stopThread()"
                    + e.getMessage());
        }
    }

    public void startThread() {
        mRunning = true;
        notifyNumber = 0;
        notified = false;
        mThread = new Thread(this);
        mThread.start();
    }

    /** run
     * Gestisce tutto il Thread
     * E' astratto poichè ogni campo da gioco lo sfrutta in maniera leggermente diversa dagli altri.
     */
    @Override
    public abstract void run();
    //fine metodi gestione thread




    /** --------------------------------------------------------------------------------------------
     * -----------------------------METODI GESTIONE GIOCO-------------------------------------------
     * ------------------------------------------------------------------------------------------**/
    /**
     * spawnAmmo
     * Invoca il metodo
     *
     *              objects.spawn(row,column,show)
     *
     * sull'oggetto razzo per farlo apparire così da colpire (row;column)
     *
     * @param row riga in cui colpire
     * @param column colonna in cui colpire
     * @return
     */
    @Override
    public boolean spawnAmmo(int row, int column) {//solo per battlefield Match
        ArrayList<GameObject> objects = mLevelManager.getObjects();

        objects.get(LevelManager.MISSILE)
                .spawn(row, column, true);
        return true;
    }

    /**
     * onTouchEvent
     *
     * Gestisce gli input touch
     * E' astratto in quanto ogni fase del gioco gestisce in maniera diversa il touch
     * @param event
     * @return
     */
    @Override
    public abstract boolean onTouchEvent(MotionEvent event);






    /** --------------------------------------------------------------------------------------------
     * -------------------------------METODI SALVATAGGIO FLOTTA-------------------------------------
     * ------------------------------------------------------------------------------------------**/

    /**
     * saveFleet
     *
     * Usato per scrivere i file csv in cui vengono salvate le flotte
     *
     * @param levelToLoad livello da salvare
     * @param playerName giocatore a cui appartiene il file (necessario nel passaggio da pre a match)
     * @return
     */
    public abstract boolean saveFleet(String levelToLoad, String playerName, boolean timeEnded);


    /**
     * repositionShips
     *
     * Permette di riposizionare randomicamente le navi
     * che non hanno una configurazione valida al termine del tempo di preMatch
     *
     * I parametri servono per passarli a saveFleet una volta che il metodo ha terminato
     * @param level
     * @param playerName
     */
    public abstract void repositionShips(String level, String playerName);


}
