package com.edomar.battleship.activities;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.edomar.battleship.utils.MusicServiceManager;
import com.edomar.battleship.utils.OnlineGameInfo;
import com.edomar.battleship.R;
import com.edomar.battleship.utils.HomeWatcher;
import com.edomar.battleship.utils.Utilities;
import com.edomar.battleship.activities.gameplayFragments.OnlineMatchFragment;
import com.edomar.battleship.activities.gameplayFragments.OnlinePreMatchFragment;
import com.edomar.battleship.utils.WriterReader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OnlineGameActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String TAG = "ogaProva";

    private static final String PRE_MATCH_FRAGMENT = "PRE_MATCH_FRAGMENT";
    private static final String FIRST_MATCH_FRAGMENT_TAG = "FIRST_MATCH_FRAGMENT_TAG";
    private static final String SECOND_MATCH_FRAGMENT_TAG = "SECOND_MATCH_FRAGMENT_TAG";

    /**Match variables**/
    private String match_id;
    private String local_player;
    private String other_player;
    private String request_type;
    private String scenario;
    private boolean disconnected = false;
    private boolean ended = false;

    private OnlinePreMatchFragment mPreMatchFragment;
    private OnlineMatchFragment mMatchFragment1;
    private OnlineMatchFragment mMatchFragment2;

    /** Fragment Manager **/
    private FragmentManager mFM;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference myRefGameSession;



    private boolean started = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game);
        //Retrive data from source Intent
        Intent src = getIntent();

        match_id = src.getStringExtra("match_id");
        local_player = src.getStringExtra("user_name");
        other_player = src.getStringExtra("other_player");
        request_type = src.getStringExtra("request_type");
        scenario = src.getStringExtra("scenario");

        //Imposto i valori di onlineGameInfo che serviranno al battlefield per notificare al database i colpi
        OnlineGameInfo onlineGameInfo = OnlineGameInfo.getInstance();
        onlineGameInfo.setMatchID(match_id);
        onlineGameInfo.setPlayerName(local_player);
        onlineGameInfo.setOtherPlayer(other_player);
        onlineGameInfo.setRequestType(request_type);
        onlineGameInfo.setScenario(scenario);

        /**SharedPreference**/
        SharedPreferences sp = getSharedPreferences(getString(R.string.configuration_preference_key), MODE_PRIVATE);


        /**Badge Configuration**/
        ImageView ImageViewBadge = (ImageView) findViewById(R.id.badge_image_view);
        Utilities.setFlagOfBadge(sp.getString(getString(R.string.flag_key), "USA"), ImageViewBadge);
        TextView mPlayerNameTextView = (TextView) findViewById(R.id.badge_player_name);
        mPlayerNameTextView.setText(local_player);

        /**HomeWathcer**/
        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                myRef.child("playing"+scenario).child(match_id).child(local_player +"_connection_state").setValue("offline");
                MusicServiceManager.getInstance().pauseMusic(OnlineGameActivity.this);

            }

            @Override
            public void onHomeLongPressed() {
                myRef.child("playing"+scenario).child(match_id).child(local_player +"_connection_state").setValue("offline");
                MusicServiceManager.getInstance().pauseMusic(OnlineGameActivity.this);
            }
        });
        mHomeWatcher.startWatch();


        /** Impostazioni referenze al database e dei fragment**/
        myRefGameSession = myRef.child("playing"+scenario)
                .child(match_id);

        //Il primo fragment che viene visualizzato è quello di PreMatch
        //al quale viene indicato chi è il "proprietario" del campo visualizzato e l'avversario
        if(savedInstanceState == null){
            mPreMatchFragment = OnlinePreMatchFragment.newInstance(local_player, other_player, scenario);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_online, mPreMatchFragment, PRE_MATCH_FRAGMENT)
                    .commit();
        }

        //Imposto l'ordine dei MatchFragment in base a chi ha inviato la richiesta
        if(request_type.equalsIgnoreCase("From")){
            //Se il giocatore in locale ha accettato inzia lui la partita
            mMatchFragment1 = OnlineMatchFragment.newInstance(local_player, other_player, 1, scenario); //Interagisce player_name e carico la griglia di other_player
            mMatchFragment2 = OnlineMatchFragment.newInstance(other_player, local_player,2, scenario); //Interagisce other_player e carico la griglia di player_name

        }else{
            mMatchFragment1 = OnlineMatchFragment.newInstance(other_player, local_player,1, scenario);//Interagisce other_player e carico la griglia di player_name
            mMatchFragment2 = OnlineMatchFragment.newInstance(local_player, other_player,2, scenario);//Interagisce player_name e carico la griglia di other_player
        }

        mFM = getSupportFragmentManager();

        myRef.child("playing"+scenario)
                .child(match_id)
                .addValueEventListener(new ValueEventListener() { //Listener all'oggetto matchId del database
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) { //Se qualcosa cambia fai due check
                        if(snapshot.exists()){
                            checkIfBothPlayerAreReady(snapshot); //Sono pronti per giocare?
                            checkIfBothPlayerAreStillOnline(snapshot); //Sono ancora entrambi online?
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    /**
     *checkIfBothThePlayerAreStillOnline controlla se uno dei due giocatori è uscito dalla partita
     * @param snapshot
     */
    private void checkIfBothPlayerAreStillOnline(DataSnapshot snapshot) {
        boolean[] offline = {false, false};
        for (DataSnapshot ss : snapshot.getChildren()) {
            String key = ss.getKey();

            if (key != null) {
                if (key.equalsIgnoreCase(local_player +"_connection_state")) {
                    String value = (String) ss.getValue();
                    if (value != null && value.equalsIgnoreCase("offline")) {
                        offline[0] = true;
                    }
                } else if (key.equalsIgnoreCase(other_player+"_connection_state")) {
                    String value = (String) ss.getValue();
                    if (value != null && value.equalsIgnoreCase("offline")) {
                        offline[1] = true;
                    }
                }
            }
        }


        if(offline[0]){ //Local player andato offline
            disconnected = true;
            endGame(other_player);
        }else if(offline[1]){
            disconnected = true;
            endGame(local_player);
        }
    }

    /**
     * checkIfBothThePlayerAreReady controlla se entrambi i giocatori sono pronti a giocare
     * @param snapshot
     */
    private void checkIfBothPlayerAreReady(DataSnapshot snapshot) {
        boolean[] ready = {false, false};
        for (DataSnapshot ss : snapshot.getChildren()) {
            String key = ss.getKey();

            if (key != null) {
                if (key.equalsIgnoreCase(local_player +"_game_state")) {
                    String value = (String) ss.getValue();
                    if (value != null && value.equalsIgnoreCase("ready")) {
                        ready[0] = true;
                    }
                } else if (key.equalsIgnoreCase(other_player+"_game_state")) {
                    String value = (String) ss.getValue();
                    if (value != null && value.equalsIgnoreCase("ready")) {
                        ready[1] = true;
                    }
                }
            }
        }

        if(ready[0] && ready[1] && !started){
            startMatch();
        }
    }

    /**Questo metodo è invocato in due casi:
     *      -a fine timer nel preMatchFragment
     *      -una volta premuto il pulsante start nel PreMatchFragment
     */
    public void startMatch() {
        try {
            if(!isDestroyed()){
                mFM.beginTransaction()
                        .replace(R.id.container_online, mMatchFragment1, FIRST_MATCH_FRAGMENT_TAG)
                        .commit();
                started = true;
            }

        }catch (Exception e){ //Problema di avvio del match dunque chiudo l'activity e lo notifico al database così da far terminare la partita anche nell'altro telefono
            myRefGameSession.child(local_player +"_connection_state").setValue("offline");
            WriterReader.deleteMatchFleets(local_player, other_player);
            finish();
            Toast.makeText(getApplicationContext(), "Errore nel collegamento", Toast.LENGTH_SHORT).show();
        }


    }

    /** Questo metodo alterna i due fragment
     * è invocato quando:
     *      -un giocatore tenta un colpo
     *      -passa tempo max di un turno**/
    public void changeFragment(Fragment fragment){

        switch (fragment.getClass().getSimpleName()){
            case "OnlineMatchFragment":
                switchMatchFragment(fragment);
                break;
        }

    }

    private void switchMatchFragment(Fragment fragment){
        if (mMatchFragment1.equals(fragment)) {

            //nascondi e mostra in momenti diversi
            if(mFM.findFragmentByTag(FIRST_MATCH_FRAGMENT_TAG) != null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mMatchFragment1)
                        .setMaxLifecycle(mMatchFragment1, Lifecycle.State.STARTED)
                        .commit();


            }

            if(mFM.findFragmentByTag(SECOND_MATCH_FRAGMENT_TAG) != null){
                //show f2

                getSupportFragmentManager()
                        .beginTransaction()
                        .setMaxLifecycle(mMatchFragment2, Lifecycle.State.RESUMED)
                        .show(mMatchFragment2)
                        .commit();

            }else{
                //add f2

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container_online, mMatchFragment2, SECOND_MATCH_FRAGMENT_TAG)
                        .commit();

            }


        }

        else if (mMatchFragment2.equals(fragment)) {



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

    @Override
    public void requestToChangeFragment(Fragment fragment) {
        changeFragment(fragment);
    }

    @Override
    public void endGame(String winner) {
        if(!ended){


            //Rimuovo gli oggetti del match dal database
            myRef.child("playing"+scenario).child(match_id).removeValue();
            myRef.child("hit"+scenario).child(match_id).removeValue();

            if(disconnected){ //se la paritta non è stata completata l'opzione di visualizzazione delle griglie non sarà disponibile
                WriterReader.deleteMatchFleets(local_player, other_player);
            }

            //Attivo la winner activity
            Intent intent = new Intent(OnlineGameActivity.this, WinnerActivity.class);
            intent.putExtra("winner", winner);
            String loser;
            if (winner.equals(local_player)){
                loser = other_player;
            }else{
                loser = local_player;
            }
            intent.putExtra("loser", loser);
            intent.putExtra("level", scenario);
            intent.putExtra("disconnected", disconnected);
            startActivity(intent);
            finish();
            ended = true; //Evita tentativi multipli di terminare una partita
        }
    }

    @Override
    public void onBackPressed() { //Quando esco dalla GameActivity mi disconnetto dalla partita
        super.onBackPressed();
        myRef.child("playing"+scenario).child(match_id).child(local_player +"_connection_state").setValue("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // A differenza dell'activity offline non chiamo endGame perchè è già stato invocato dal fragment
        // o dal listener del matchId tramite checkIfBothThePlayerAreStillOnline
        finish();
    }
}