package com.edomar.battleship.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edomar.battleship.R;
import com.edomar.battleship.utils.HomeWatcher;
import com.edomar.battleship.utils.MusicServiceManager;
import com.edomar.battleship.utils.Utilities;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MatchMakingActivity extends AppCompatActivity {

    private static final String TAG = "MatchMakingActivity";

    /**SharedPreference**/
    private SharedPreferences sp;

    private String scenario;
    private boolean started; //Flag per evitare che una partita sia avviata più volte

    /**Badge**/
    private ImageView ImageViewBadge;
    private TextView mPlayerNameTextView;
    private String playerName;

    /**ListView**/
    private ListView lv_loginUsers;
    private ArrayList<String> list_loginUsers = new ArrayList<String>();
    private ArrayAdapter<String> adpt;

    private ListView lv_requstedUsers;
    private ArrayList<String> list_requestedUsers = new ArrayList<String>();
    private ArrayAdapter<String> reqUsersAdpt;

    private TextView tvSendRequest, tvAcceptRequest;


    /** Firebase database reference**/
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    /**-----------------------------------------------------
     *-------------------LIFECYCLE METHODS------------------
     -----------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_making);

        /**SharedPreference**/
        sp = getSharedPreferences(getString(R.string.configuration_preference_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        //homeWatcher to stop music if home button is pressed
        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                MusicServiceManager.getInstance().pauseMusic(MatchMakingActivity.this);

            }

            @Override
            public void onHomeLongPressed() {
                MusicServiceManager.getInstance().pauseMusic(MatchMakingActivity.this);
            }
        });
        mHomeWatcher.startWatch();

        //Ottieni i dati necessari dall'intent
        playerName = getIntent().getStringExtra("playerName");
        scenario = getIntent().getStringExtra("scenario");

        /**Badge Configuration**/
        ImageViewBadge = (ImageView) findViewById(R.id.badge_image_view);
        Utilities.setFlagOfBadge(sp.getString(getString(R.string.flag_key), "USA"), ImageViewBadge);
        mPlayerNameTextView = (TextView) findViewById(R.id.badge_player_name);
        mPlayerNameTextView.setText(playerName);

        /**List Configuration**/
        tvSendRequest = (TextView) findViewById(R.id.tvSendRequest);
        tvAcceptRequest = (TextView) findViewById(R.id.tvAcceptRequest);


        lv_loginUsers = (ListView) findViewById(R.id.lv_loginUsers);
        adpt = new CustomAdapter(this, R.layout.row_layout, list_loginUsers);
        lv_loginUsers.setAdapter(adpt);


        lv_requstedUsers = (ListView) findViewById(R.id.lv_requestedUsers);
        reqUsersAdpt = new CustomAdapter(this, R.layout.row_layout, list_requestedUsers);
        lv_requstedUsers.setAdapter(reqUsersAdpt);

        lv_loginUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final TextView txt = view.findViewById(R.id.player_name_txtView);
                final String requestToUser = txt.getText().toString();
                //sendRequestTo
                sendRequestTo(requestToUser);
            }
        });



        lv_requstedUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView txt = view.findViewById(R.id.player_name_txtView);
                final String requestFromUser = txt.getText().toString();
                //acceptRequestFrom
                acceptRequestFrom(requestFromUser);
            }
        });



        /**Firebase **/
        // Read from the database
        myRef.getRoot().child(scenario).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { //Un nuovo giocatore è entato nella room "scenario"
                updateSendRequestList(snapshot, "added");                                       //Aggiorna la lista dei giocatori a cui posso inviare una richiesta di partita
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {//Un giocatore è uscito dalla room "scenario"
                //updateSendRequestList(snapshot, "added");
                updateSendRequestList(snapshot, "removed"); //Aggiorna la lista dei giocatori a cui posso inviare una richiesta di partita
                updateAcceptRequestList(snapshot, "removed");//Aggiorno la lista dei giocatori di cui posso accetare la richiesta di gioco
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myRef.getRoot().child(scenario).child(playerName).child("requestFrom") //Gestione delle richieste inviate a "playerName"
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //E' arrivata una nuova richiesta
                        if(dataSnapshot.exists()){
                            updateAcceptRequestList(dataSnapshot, "changed");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        myRef.getRoot().child(scenario).child(playerName).child("requestTo")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { //Una richiesta è stata accetata
                        if(snapshot.exists()){
                            String otherPlayer = snapshot.getKey();
                            startGame(playerName + ":" + otherPlayer, otherPlayer, "To"); //Avvia la partita
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

    @Override
    protected void onResume() {
        super.onResume();
        myRef.child(scenario).child(playerName).child("available").setValue("true");
        started = false;

        if(sp.getBoolean(getString(R.string.background_music_key), true)) {
            MusicServiceManager.getInstance().doBindService(this);
            MusicServiceManager.getInstance().startService(this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        myRef.child(scenario).child(playerName).removeValue();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;

        if(pm != null){
            isScreenOn = pm.isScreenOn();
        }


        if(!isScreenOn){
            MusicServiceManager.getInstance().pauseMusic(this);
        }
    }


    /**-----------------------------------
     * ------SEND & ACCEPT METHODS--------
     ----------------------------------**/
    //Method to send a request to an other player
    private void sendRequestTo(String otherPlayer) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_connect_player, null);
        b.setView(dialogView);

        b.setTitle("Send request to?");
        b.setMessage("Connect with " + otherPlayer);
        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                myRef.child(scenario)
                        .child(otherPlayer).child("requestFrom").child(playerName).setValue("waiting");
                myRef.child(scenario)
                        .child(playerName).child("requestTo").child(otherPlayer).setValue("waiting");
            }
        });
        b.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.show();
    }

    //Accept request from an otherPlayer
    private void acceptRequestFrom(String otherPlayer) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_connect_player, null);
        b.setView(dialogView);

        b.setTitle("Do you want accept this request?");
        b.setMessage("Request from " + otherPlayer);
        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myRef.child(scenario)
                        .child(otherPlayer)
                        .child("requestTo")
                        .child(playerName)
                        .setValue("accept");
                startGame(otherPlayer + ":" + playerName, otherPlayer, "From");
            }
        });
        b.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.show();

    }

    /**--------------------
    /**----Updater---------
     -------------------**/
    //SendRequest List
    private void updateSendRequestList(DataSnapshot dataSnapshot, String changeType){


        switch (changeType){
            case "added":
                boolean present = false;
                for (int i = 0; i < adpt.getCount(); i++) {
                    if(dataSnapshot.getKey().equals(adpt.getItem(i))){
                        present= true;
                    }
                }
                if(!present && !dataSnapshot.getKey().equals(playerName)){
                    adpt.add(dataSnapshot.getKey());
                }
                break;
            case "removed":
                if(adpt.getPosition(dataSnapshot.getKey()) != -1){
                    adpt.remove(dataSnapshot.getKey());
                }
                break;
        }



        adpt.notifyDataSetChanged();
        tvSendRequest.setText("Send request to");
        tvAcceptRequest.setText("Accept request from");
    }

    //AcceptRequest List
    private void updateAcceptRequestList(DataSnapshot dataSnapshot, String changeType) {

        switch(changeType){
            case "changed":
                Set<String> set = new HashSet<String>();
                reqUsersAdpt.clear();
                try{
                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                    if(map != null){

                        for(String key:map.keySet()){
                            if(!set.contains(key)){

                                reqUsersAdpt.add(key);
                                reqUsersAdpt.notifyDataSetChanged();
                                set.add(key);

                            }

                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case "removed":
                if(reqUsersAdpt.getPosition(dataSnapshot.getKey()) != -1){
                    reqUsersAdpt.remove(dataSnapshot.getKey());
                    myRef.child(scenario).child(playerName).child("requestFrom").child(dataSnapshot.getKey()).removeValue();
                }
                break;
        }
    }


    /**startGame
     * Permette di avviare un match online
     * @param matchID identificativo del match nel database costituito dai nomi dei giocatori
     * @param otherPlayer avversario
     * @param requestType indica chi ha invitato (se TO allora il giocatore in locale ha invitato, se FROM è stato accetato l'invito),
     *                    incide su chi inizia a giocare
     */
    private void startGame(String matchID, String otherPlayer, String requestType) {
        if(!started){
            //Imposto nel database lo stato di connessione dei due giocatori
            myRef.child("playing"+scenario).child(matchID).child(playerName+"_connection_state").setValue("online");
            myRef.child("playing"+scenario).child(matchID).child(otherPlayer+"_connection_state").setValue("online");
            //Inserisco nell'intent i dati necessari
            Intent intent = new Intent(getApplicationContext(), OnlineGameActivity.class);
            intent.putExtra("match_id", matchID);
            intent.putExtra("user_name", playerName);
            intent.putExtra("other_player", otherPlayer);
            intent.putExtra("request_type", requestType);
            intent.putExtra("scenario", scenario);
            startActivity(intent);
            started = true; //Serve per evitare tentativi di avvio multipli della stessa partita
        }


    }








}