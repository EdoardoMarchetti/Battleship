package com.edomar.battleship.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edomar.battleship.R;
import com.edomar.battleship.activities.gameplayFragments.PostMatchFragment;
import com.edomar.battleship.utils.OnlineGameInfo;
import com.edomar.battleship.utils.Utilities;
import com.edomar.battleship.utils.WriterReader;

public class WinnerActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private OnlineGameInfo onlineGameInfo;

    /**SharedPreference**/
    private SharedPreferences sp;

    /**Varaibles**/
    private boolean disconnected;


    /**Badge**/
    private ImageView ImageViewBadge;
    private TextView mPlayerNameTextView;

    /**Frame layout**/
    private FrameLayout mFrameLayout;
    private boolean frameLayoutVisible = false;

    /**Buttons**/
    private Button mButtonComplete;
    private Button mButtonGrids;

    /**Fragments**/
    private PostMatchFragment mPostMatchFragment1;
    private PostMatchFragment mPostMatchFragment2;
    public static final String FIRST_POST_MATCH_FRAGMENT_TAG = "FirstFrag";
    public static final String SECOND_POST_MATCH_FRAGMENT_TAG = "SecondFrag";


    private FragmentManager mFM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        /**SharedPreference**/
        SharedPreferences sp = getSharedPreferences(getString(R.string.configuration_preference_key), MODE_PRIVATE);

        mFM = getSupportFragmentManager();


        Intent src = getIntent();
        String winner = src.getStringExtra("winner");
        String loser = src.getStringExtra("loser");
        String level = src.getStringExtra("level");


        disconnected = src.getBooleanExtra("disconnected", false);
        onlineGameInfo = OnlineGameInfo.getInstance();

        /**Badge Configuration**/
        ImageViewBadge = (ImageView) findViewById(R.id.badge_image_view);
        Utilities.setFlagOfBadge(sp.getString(getString(R.string.flag_key), "USA"), ImageViewBadge);
        mPlayerNameTextView = (TextView) findViewById(R.id.badge_player_name);
        mPlayerNameTextView.setText(sp.getString(getString(R.string.player_nickname), "NOT LOGGED"));


        TextView txtView = findViewById(R.id.winner_txt_view);
        txtView.setText(winner);

        if(disconnected && !winner.equals(onlineGameInfo.getOtherPlayer())){
            Toast.makeText(getApplicationContext(), "Avversario disconnesso", Toast.LENGTH_SHORT).show();
        }

        /**Buttons**/
        mButtonComplete = (Button) findViewById(R.id.button_complete);
        mButtonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!disconnected){
                    WriterReader.deleteMatchFleets(winner, loser);
                }
                OnlineGameInfo.deleteInstance();
                finish();
            }
        });

        mButtonGrids = (Button) findViewById(R.id.button_grids);
        mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout);

        mButtonGrids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(disconnected){
                    Toast.makeText(WinnerActivity.this, "Griglie non disponibili", Toast.LENGTH_SHORT).show();
                }else{
                    mFrameLayout.setVisibility(View.VISIBLE);
                    frameLayoutVisible = true;
                    goToPostMatchFragment(winner, loser, level);
                }

            }
        });

    }



    private void goToPostMatchFragment(String winner, String loser, String level) {

        Log.d("POST", "goToPostMatchFragment: level = "+ level);
        mPostMatchFragment1 = PostMatchFragment.newInstance(winner, level, 1);
        mPostMatchFragment2 = PostMatchFragment.newInstance(loser, level, 2);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, mPostMatchFragment1, FIRST_POST_MATCH_FRAGMENT_TAG)
                .commit();

    }

    @Override
    public void onBackPressed() {
        if(frameLayoutVisible){
            frameLayoutVisible = false;
            mFrameLayout.setVisibility(View.GONE);
            if(mPostMatchFragment1.isVisible()){
                mPostMatchFragment1.onPause();
            }else{
                mPostMatchFragment2.onPause();
            }


        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void requestToChangeFragment(Fragment fragment) {
        if (mPostMatchFragment1.equals(fragment)) { //Nascondo MatchFragment1 e mostro MatchFragment2

            //nascondi e mostra in momenti diversi
            if(mFM.findFragmentByTag(FIRST_POST_MATCH_FRAGMENT_TAG) != null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mPostMatchFragment1)
                        .setMaxLifecycle(mPostMatchFragment1, Lifecycle.State.STARTED)
                        .commit();
            }

            if(mFM.findFragmentByTag(SECOND_POST_MATCH_FRAGMENT_TAG) != null){ //Se MatchFragment2 è già inserito sullo stack lo mostro
                //show f2
                getSupportFragmentManager()
                        .beginTransaction()
                        .setMaxLifecycle(mPostMatchFragment2, Lifecycle.State.RESUMED)
                        .show(mPostMatchFragment2)
                        .commit();

            }else{ //altrimento lo aggiungo
                //add f2
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame_layout, mPostMatchFragment2, SECOND_POST_MATCH_FRAGMENT_TAG)
                        .commit();
            }


        }

        else if (mPostMatchFragment2.equals(fragment)) {//Nascondo MatchFragment1 e mostro MatchFragment2 (gestione simmetrica rispetto a prima)

            //nascondi e mostra in momenti diversi
            if(mFM.findFragmentByTag(SECOND_POST_MATCH_FRAGMENT_TAG) != null){

                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mPostMatchFragment2)
                        .setMaxLifecycle(mPostMatchFragment2, Lifecycle.State.STARTED)
                        .commit();

            }

            if(mFM.findFragmentByTag(FIRST_POST_MATCH_FRAGMENT_TAG) != null){
                //show f1

                getSupportFragmentManager()
                        .beginTransaction()
                        .setMaxLifecycle(mPostMatchFragment1, Lifecycle.State.RESUMED)
                        .show(mPostMatchFragment1)
                        .commit();

            }else{

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame_layout, mPostMatchFragment1, FIRST_POST_MATCH_FRAGMENT_TAG)
                        .commit();

            }

        }
    }

    @Override
    public void endGame(String playerName) {
        //Not used from postMatchFrag
    }
}