package com.edomar.battleship.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.edomar.battleship.R;
import com.edomar.battleship.activities.dialog.LoginDialog;
import com.edomar.battleship.activities.dialog.RegisterDialog;
import com.edomar.battleship.utils.HomeWatcher;
import com.edomar.battleship.utils.MusicServiceManager;
import com.edomar.battleship.utils.Utilities;
import com.edomar.battleship.activities.menuFragments.FleetFragment;
import com.edomar.battleship.activities.menuFragments.MainMenuFragment;
import com.edomar.battleship.activities.menuFragments.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements OnDialogInteractionListener {

    public static final String TAG = "hactProva";
    public static final String FIREBASE = "MyFirebase";


    private Fragment mCurrentFragment;

    /** SharedPreference**/
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    /**Badge**/
    public ImageView ImageViewBadge;
    public TextView mPlayerNameTextView;

    /**Firebase instance**/
    //Servono per il Login
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    /**-----------------------------------------------------
     *-------------------LIFECYCLE METHODS------------------
     -----------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hud);
        if(savedInstanceState == null){
            final MainMenuFragment mainMenuFragment = new MainMenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.anchor_point, mainMenuFragment, MainMenuFragment.TAG)
                    .commit();
        }

        //Ottengo la referenza alle SharedPreference di configurazione
        sp = getSharedPreferences(getString(R.string.configuration_preference_key), MODE_PRIVATE);
        editor = sp.edit();




        /**Sound config**/
        //Avvio la musica di sottofondo se è richiesto dalle SP
        if(sp.getBoolean(getString(R.string.background_music_key), true)) {
            MusicServiceManager.getInstance().doBindService(this);
            MusicServiceManager.getInstance().startService(this);
        }//end background sound config



        //L'HomeWathcer è necessario per fermare la musica quando viene premuto HOME o il Multitasking
        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                MusicServiceManager.getInstance().pauseMusic(MainActivity.this);
                Log.d(TAG, "onHomePressed: ");
            }

            @Override
            public void onHomeLongPressed() {
                MusicServiceManager.getInstance().pauseMusic(MainActivity.this);
                Log.d(TAG, "onHomePressed: ");
            }
        });
        mHomeWatcher.startWatch();



        /**BADGE CONFIGURATION**/
        //Di seguito vengono impostati gli elementi da mostrare nel badge (nome giocatore e bandiara)
        ImageViewBadge = findViewById(R.id.badge_image_view);
        Utilities.setFlagOfBadge(sp.getString(getString(R.string.flag_key), "USA"), ImageViewBadge); //Tramite la classe di suporto imposto la bandiera da visualizzare

        mPlayerNameTextView = findViewById(R.id.badge_player_name);
        mPlayerNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginDialog();
            }
        });
        mPlayerNameTextView.setText(R.string.none_player_logged);
        editor.putString(getString(R.string.player_nickname), getString(R.string.none_player_logged));
        editor.apply();


        /**AUTENTICAZIONE UTENTE**/
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(FIREBASE, "onAuthStateChanged: ");
                if (user != null) {
                    // L'utente corrente è autenticato
                    updatePlayerNameOnBadge(user);
                } else {
                    // Nessun utente è stato autenticato quindi apro la Dialog per la registrazione
                    openRegisterDialog();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        //Aggiungo il listener al mAuth per l'autenticazione
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        //Avvio la musica di background se è richiesto
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        if(sp.getBoolean(getString(R.string.background_music_key), true) && pm.isScreenOn()) {
            MusicServiceManager.getInstance().doBindService(this);
            MusicServiceManager.getInstance().startService(this);
        }

        super.onResume();



    }

    @Override
    protected void onPause() {
        super.onPause();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;

        if(pm != null){
            isScreenOn = pm.isScreenOn();
        }

        //La musica viene fermata se il telefono va in stand-by
        if(!isScreenOn){
            MusicServiceManager.getInstance().pauseMusic(this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("lifecycle", "onStop: ");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
            Log.d(FIREBASE, "onStop: listener removed ");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //stop Music service se l'activity viene distrutta (la HUD Activity non invoca mai finish())
        MusicServiceManager.getInstance().doUnbindService(this);
        MusicServiceManager.getInstance().stopService(this);

    }

    /**-----------------------------------------------------
     *-------------------UTILITY METHODS------------------
     -----------------------------------------------------*/

    /**
     * showFragment
     * metodo per gestire il cambiamento di fragment quando viene selezionato un menu tra quelli siponibili
     * @param selectedMenu view del menu selezionato
     */
    public void showFragment ( final View selectedMenu){
        final int viewID = selectedMenu.getId();
        Fragment nextFragment;

        switch (viewID) {
            case (R.id.left_button): //Go to settings menu
                Log.d("Pressed button", "changeActivity: left");
                if(!(mCurrentFragment instanceof SettingsFragment)){
                    nextFragment = new SettingsFragment();
                    mCurrentFragment = nextFragment;
                }
                break;
            case (R.id.central_button): //Go to main menu
                Log.d("Pressed button", "changeActivity: central");
                if(!(mCurrentFragment instanceof MainMenuFragment)){
                    nextFragment = new MainMenuFragment();
                    mCurrentFragment = nextFragment;
                }
                break;
            case (R.id.right_button): //Go to fleet menu
                if(!(mCurrentFragment instanceof FleetFragment)){
                    nextFragment = new FleetFragment();
                    mCurrentFragment = nextFragment;
                }
                Log.d("Pressed button", "changeActivity: right");
                break;
            default:
                throw new IllegalArgumentException("No Fragment for the given item");
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.anchor_point, mCurrentFragment, TAG)
                .commit();

    }

    /**
     * updatePlayerNameOnBadge
     * metodo di utilità per aggiornare il nome del giocatore nel badge
     * @param user utente che ha fatto il login
     */
    private void updatePlayerNameOnBadge(FirebaseUser user) {
        if(user != null){
            String playerEmail = user.getEmail();
            String playerNickName = Utilities.convertEmailToString(playerEmail);
            mPlayerNameTextView.setText(playerNickName);
            editor.putString(getString(R.string.player_nickname), playerNickName);

        }else{
            mPlayerNameTextView.setText(R.string.none_player_logged);
            editor.putString(getString(R.string.player_nickname), getString(R.string.none_player_logged));
        }
        editor.apply();
    }




    /**-----------------------------------------------
     *-------OnDialogInteractionListener Methods------
     *----------------------------------------------*/
    /**
     * registerUser
     * usato per fare la registrazione di un nuovo giocatore
     * invocato da RegisterDialog
     * @param email
     * @param password
     */
    public void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(FIREBASE, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updatePlayerNameOnBadge(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(FIREBASE, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updatePlayerNameOnBadge(null);
                        }

                        // ...
                    }
                });
    }

    /**
     * loginUser
     * usato per fare il login di un giocatore
     * invocato da LoginDialog
     * @param email
     * @param password
     */
    public void loginUser(String email, String password){
        Toast.makeText(MainActivity.this, "Prova login",
                Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updatePlayerNameOnBadge(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updatePlayerNameOnBadge(null);
                        }

                        // ...
                    }
                });
    }

    public void openLoginDialog(){
        LoginDialog loginDialog = new LoginDialog(MainActivity.this);
        loginDialog.setContentView(R.layout.dialog_login);
        loginDialog.setListener(MainActivity.this);
        loginDialog.show();
    }

    public void openRegisterDialog(){
        RegisterDialog registerDialog = new RegisterDialog(MainActivity.this);
        registerDialog.setContentView(R.layout.dialog_register);
        registerDialog.setListener(MainActivity.this);
        registerDialog.show();
    }
}