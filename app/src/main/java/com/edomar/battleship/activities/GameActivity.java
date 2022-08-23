package com.edomar.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.edomar.battleship.R;
import com.edomar.battleship.utils.HomeWatcher;
import com.edomar.battleship.utils.MusicServiceManager;
import com.edomar.battleship.utils.Utilities;
import com.edomar.battleship.utils.WriterReader;
import com.edomar.battleship.activities.gameplayFragments.MatchFragment;
import com.edomar.battleship.activities.gameplayFragments.PreMatchFragment;

/**
 * Inserire le righe di codice per la gestione del Badge
 */


public class GameActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String TAG = "VerificaGameActivity";
    public static final String PRE_MATCH_FRAGMENT = "PreMatchFrag";
    public static final String FIRST_MATCH_FRAGMENT_TAG = "FirstFrag";
    public static final String SECOND_MATCH_FRAGMENT_TAG = "SecondFrag";



    /**Badge**/
    public ImageView ImageViewBadge;
    public TextView mPlayerNameTextView;

    /**Level**/
    private String levelToPlay;
    private String  gameMode;

    /** Fragment Manager **/
    private FragmentManager mFM;

    /** PreMatch Fragments**/
    private PreMatchFragment mPreMatchFragment1;
    private PreMatchFragment mPreMatchFragment2;

    /** Match Fragments **/
    private MatchFragment mMatchFragment1;
    private MatchFragment mMatchFragment2;

    /**Utility variables**/
    private String player1;
    private String player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferences sp = getSharedPreferences(getString(R.string.configuration_preference_key), MODE_PRIVATE);


        /**Badge Configuration**/
        String playerName = getIntent().getStringExtra("playerName"); //Nickname del giocatore loggato
        ImageViewBadge = (ImageView) findViewById(R.id.badge_image_view);
        Utilities.setFlagOfBadge(sp.getString(getString(R.string.flag_key), "USA"), ImageViewBadge);
        mPlayerNameTextView = (TextView) findViewById(R.id.badge_player_name);
        mPlayerNameTextView.setText(playerName);

        Intent src = getIntent();
        levelToPlay = src.getStringExtra("scenario");
        gameMode = src.getStringExtra("gameMode");
        player1 = "giocatore1";
        if(gameMode.equals("1vs1")){
            player2 = "giocatore2";
        }else{
            player2 = "computer";
        }

        //Il primo fragment che viene visualizzato è quello di PreMatch
        //al quale viene indicato chi è il "proprietario" del campo visualizzato e l'avversario
        if(savedInstanceState == null){
            mPreMatchFragment1 = PreMatchFragment.newInstance(player1,player2,levelToPlay);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_online, mPreMatchFragment1, PRE_MATCH_FRAGMENT)
                    .commit();
        }


        mFM = getSupportFragmentManager();



        //L'if serve per gestire la creazione dei successivi fragment
        if(gameMode.equals("1vs1")) {
            //Se la modalità è 1vs1 allora creo un secondo PreMatch invertendo i giocatori
            mPreMatchFragment2 = PreMatchFragment.newInstance(player2, player1, levelToPlay);
        }

        mMatchFragment1 = MatchFragment.newInstance(player1, player2,1, levelToPlay);//Interagisce giocatore1 quindi carico la griglia di computer
        mMatchFragment2 = MatchFragment.newInstance(player2, player1,2, levelToPlay);//Interagisce computer quindi carico la griglia del giocatore 1




        //homeWatcher to stop music if home button is pressed
        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                MusicServiceManager.getInstance().pauseMusic(GameActivity.this);
                WriterReader.deleteMatchFleets(player1, player2);
                finish();
            }

            @Override
            public void onHomeLongPressed() {
                MusicServiceManager.getInstance().pauseMusic(GameActivity.this);
                WriterReader.deleteMatchFleets(player1, player2);
                finish();
            }
        });
        mHomeWatcher.startWatch();



    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() { //Quando esco dalla GameActivity mi disconnetto dalla partita
        super.onBackPressed();
        WriterReader.deleteMatchFleets(player1, player2);
    }

    /**Questo metodo è invocato da switchPreMatchFragment
     * una volta chiusi tutti i PreMatchFragment
     */
    public void startMatch() {

        //Visualizzo il primo preMatch così da aviare la partita
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_online, mMatchFragment1, FIRST_MATCH_FRAGMENT_TAG)
                .commit();


    }

    /** Questo metodo alterna i due fragment
     * è invocato quando:
     *      -un giocatore tenta un colpo
     *      -passa tempo max di un turno**/
    public void changeFragment(Fragment fragment){

        switch (fragment.getClass().getSimpleName()){
            case "PreMatchFragment": //Ci sarà da fare lo switch con un nuovo preMatchFragment
                switchPreMatchFragment(fragment);
                break;
            case "MatchFragment": //Devo fare il cambio con l'altro matchFragment (alternanza durante la partita)
                switchMatchFragment(fragment);
                break;
        }


    }

    /**Method to help changeFragment**/
    private void switchPreMatchFragment(Fragment fragment){

        if(gameMode.equals("single_player")){ //Allora serviva solo un preMatch quindi avvio la partita
            startMatch();
        }else if(gameMode.equals("1vs1")){ //Due casi

            if(fragment.equals(mPreMatchFragment1)){ //richiesta da preMatch1 => carico secondo preMatch
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_online, mPreMatchFragment2)
                        .commit();
            }else{//richiesta da preMatch2 allora avvio la partita
                startMatch();
            }

        }

    }

    private void switchMatchFragment(Fragment fragment){
        if (mMatchFragment1.equals(fragment)) { //Nascondo MatchFragment1 e mostro MatchFragment2

            //nascondi e mostra in momenti diversi
            if(mFM.findFragmentByTag(FIRST_MATCH_FRAGMENT_TAG) != null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mMatchFragment1)
                        .setMaxLifecycle(mMatchFragment1, Lifecycle.State.STARTED)
                        .commit();
            }

            if(mFM.findFragmentByTag(SECOND_MATCH_FRAGMENT_TAG) != null){ //Se MatchFragment2 è già inserito sullo stack lo mostro
                //show f2
                getSupportFragmentManager()
                        .beginTransaction()
                        .setMaxLifecycle(mMatchFragment2, Lifecycle.State.RESUMED)
                        .show(mMatchFragment2)
                        .commit();

            }else{ //altrimento lo aggiungo
                //add f2
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container_online, mMatchFragment2, SECOND_MATCH_FRAGMENT_TAG)
                        .commit();
            }


        }

        else if (mMatchFragment2.equals(fragment)) {//Nascondo MatchFragment1 e mostro MatchFragment2 (gestione simmetrica rispetto a prima)

            //nascondi e mostra in momenti diversi
            if(mFM.findFragmentByTag(SECOND_MATCH_FRAGMENT_TAG) != null){

                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mMatchFragment2)
                        .setMaxLifecycle(mMatchFragment2, Lifecycle.State.STARTED)
                        .commit();

            }

            if(mFM.findFragmentByTag(FIRST_MATCH_FRAGMENT_TAG) != null){
                //show f1

                getSupportFragmentManager()
                        .beginTransaction()
                        .setMaxLifecycle(mMatchFragment1, Lifecycle.State.RESUMED)
                        .show(mMatchFragment1)
                        .commit();

            }else{

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container_online, mMatchFragment1, FIRST_MATCH_FRAGMENT_TAG)
                        .commit();

            }

        }
    }

    /**......................................................
     *.......Metodi di OnFragmentInteractionListener.........
     ......................................................*/
    @Override
    public void requestToChangeFragment(Fragment fragment) {
        changeFragment(fragment);
    }

    @Override
    public void endGame(String winner) {
        if(!winner.equals("")) { //la partita è termiata perchè qualcuno ha vinto (winner == "" quando endGame è chiamato da onStop())
            Log.d("PARTITAFINITA", "endGame: avvio la winner activity");
            Intent intent = new Intent(GameActivity.this, WinnerActivity.class);
            intent.putExtra("winner", winner);
            String loser;
            if (winner.equals(player1)) {
                loser = player2;
            }else{
                loser = player1;
            }
            intent.putExtra("loser",  loser );
            intent.putExtra("level", levelToPlay);
            startActivity(intent);
        }

        finish();
    }
}