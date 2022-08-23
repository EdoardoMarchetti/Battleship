package com.edomar.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.edomar.battleship.R;
import com.edomar.battleship.utils.HomeWatcher;
import com.edomar.battleship.utils.MusicServiceManager;
import com.edomar.battleship.utils.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class ScenarioSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sp;


    private String gameMode;
    private String playerName;

    /**Badge**/
    private ImageView ImageViewBadge;
    public TextView mPlayerNameTextView;

    /**Menu**/
    private Button mRussianButton, mClassicButton, mStandardButton;
    private TextView mRussianPlayers,  mClassicPlayers, mStandardPlayers;


    /**Firebase**/
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();
    private HomeWatcher mHomeWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario_selection);

        sp = getSharedPreferences(getString(R.string.configuration_preference_key), MODE_PRIVATE);


        gameMode = getIntent().getStringExtra("gameMode");
        playerName = getIntent().getStringExtra("playerName");


        /**Badge Configuration**/
        ImageViewBadge = (ImageView) findViewById(R.id.badge_image_view);
        Utilities.setFlagOfBadge(sp.getString(getString(R.string.flag_key), "USA"), ImageViewBadge);
        mPlayerNameTextView = (TextView) findViewById(R.id.badge_player_name);
        mPlayerNameTextView.setText(playerName);


        /**Buttons**/
        mRussianButton = (Button) findViewById(R.id.russian_button);
        mClassicButton = (Button) findViewById(R.id.classic_button);
        mStandardButton = (Button) findViewById(R.id.standard_button);

        mRussianButton.setOnClickListener(this);
        mClassicButton.setOnClickListener(this);
        mStandardButton.setOnClickListener(this);

        /**TextView**/
        mRussianPlayers = (TextView)findViewById(R.id.player_online_russian);
        mClassicPlayers = (TextView)findViewById(R.id.player_online_classic);
        mStandardPlayers = (TextView)findViewById(R.id.player_online_standard);

        if(!gameMode.equalsIgnoreCase("online")){
            mRussianPlayers.setVisibility(View.GONE);
            mClassicPlayers.setVisibility(View.GONE);
            mStandardPlayers.setVisibility(View.GONE);
        }else{ // Se voglio giocare online vengono calcolati quanti giocatori ci sono per ciascuna room e vengono visualizzati
            mRussianPlayers.setText("Please wait...");
            mClassicPlayers.setText("Please wait...");
            mStandardPlayers.setText("Please wait...");


            //Conta i giocatori online disponibili per i vari scenari
            mRef.getRoot().child("russian").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    int playerAvailable = 0;
                    if(dataSnapshot.exists()){
                        playerAvailable = updateAvailablePayerNumber(dataSnapshot);
                    }
                    mRussianPlayers.setText("player available : "+playerAvailable);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("ProblemaDatabase", "Failed to read value.", error.toException());
                }
            });


            mRef.getRoot().child("classic").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    int playerAvailable = 0;
                    if(dataSnapshot.exists()) {
                        playerAvailable = updateAvailablePayerNumber(dataSnapshot);
                    }
                    mClassicPlayers.setText("player available : " + playerAvailable);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("ProblemaDatabase", "Failed to read value.", error.toException());
                }
            });

            mRef.getRoot().child("standard").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    int playerAvailable = 0;
                    if(dataSnapshot.exists()) {
                        playerAvailable = updateAvailablePayerNumber(dataSnapshot);
                    }
                    mStandardPlayers.setText("player available : " + playerAvailable);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("ProblemaDatabase", "Failed to read value.", error.toException());
                }
            });


        }



        //homeWatcher to stop music if home button is pressed
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                MusicServiceManager.getInstance().pauseMusic(ScenarioSelectionActivity.this);

            }

            @Override
            public void onHomeLongPressed() {
                MusicServiceManager.getInstance().pauseMusic(ScenarioSelectionActivity.this);
            }
        });
        mHomeWatcher.startWatch();
    }

    private int updateAvailablePayerNumber(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        int count = 0;

        while(i.hasNext()){
            DataSnapshot user = ((DataSnapshot) i.next());
            String playerNameKey = user.getKey();

            if (playerNameKey != null && !playerNameKey.equalsIgnoreCase(playerName)) {
                count++;
            }
        }

        return count;
    }


    @Override
    public void onClick(View view) { //Cambio activity a seconda della modalità scelta
        Button button = (Button) view;
        Intent intent;
        if(!gameMode.equalsIgnoreCase("online")){
            intent = new Intent(ScenarioSelectionActivity.this, GameActivity.class);
        }else{
            intent = new Intent(ScenarioSelectionActivity.this, MatchMakingActivity.class);
        }

        String scenarioSelected = button.getText().toString();
        scenarioSelected = Utilities.translateScenario(scenarioSelected);
        intent.putExtra("scenario", scenarioSelected);
        intent.putExtra("gameMode", gameMode);
        intent.putExtra("playerName", playerName);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        if(sp.getBoolean(getString(R.string.background_music_key), true)) {
            MusicServiceManager.getInstance().doBindService(this);
            MusicServiceManager.getInstance().startService(this);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("lifecycle", "onPause: ");
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;

        if(pm != null){
            isScreenOn = pm.isScreenOn();
        }


        if(!isScreenOn){
            MusicServiceManager.getInstance().pauseMusic(this);
        }

    }
}