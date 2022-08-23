package com.edomar.battleship.activities.menuFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.edomar.battleship.R;
import com.edomar.battleship.utils.MusicService;
import com.edomar.battleship.utils.SoundEffectsManager;
import com.edomar.battleship.utils.MusicServiceManager;
import com.edomar.battleship.utils.Utilities;
import com.edomar.battleship.activities.MainActivity;
import com.edomar.battleship.activities.SplashActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;


public class SettingsFragment extends Fragment implements  //for spinners
        CompoundButton.OnCheckedChangeListener, //for switch
        View.OnClickListener { //for button



    public static final String SETTING_FRAGMENT = SettingsFragment.class.getSimpleName();


    private MainActivity mActivity;


    /** SharedPreference**/
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    /**Background music switch**/
    private SwitchCompat mBackgroundMusicSwitch;

    /**Sound effects switch*/
    private SwitchCompat mSoundEffectsSwitch;

    /**Language**/
    private ImageButton mImageButtonLanguage;

    /**Flag**/
    private ImageButton mImageButtonFlag;

    /**About button*/
    private Button mAboutButton;

    /**Badge**/
    private ImageView Badge;


    public SettingsFragment() {
        //Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mActivity= null;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        /** Initialize SharedPreference value **/
        sp = this.getActivity().getSharedPreferences(getString(R.string.configuration_preference_key), Context.MODE_PRIVATE);
        editor =  sp.edit();
        //end initialization

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /** Badge **/
        Badge = (ImageView) mActivity.findViewById(R.id.badge_image_view);

        /**Background Music switch configuration **/
        mBackgroundMusicSwitch = (SwitchCompat) getActivity().findViewById(R.id.background_music_switch);
        mBackgroundMusicSwitch.setOnCheckedChangeListener(this);
        mBackgroundMusicSwitch.setChecked(sp.getBoolean(mActivity.getString(R.string.background_music_key), true));
        //end Background music switch configuration

        /**Animation sounds switch configuration**/
        mSoundEffectsSwitch = (SwitchCompat) getActivity().findViewById(R.id.sound_effects_switch);
        mSoundEffectsSwitch.setOnCheckedChangeListener(this);
        mSoundEffectsSwitch.setChecked(sp.getBoolean(mActivity.getString(R.string.animation_sound_key), true));
        //end Animation sounds configuration

        /**Language**/
        mImageButtonLanguage = (ImageButton) getActivity().findViewById(R.id.language_image_button);
        Utilities.setFlagOfImageButtonLanguage(sp.getString(mActivity.getString(R.string.language_key), "English"), mImageButtonLanguage);
        mImageButtonLanguage.setOnClickListener(this);
        //end Language button config

        /**Flag**/
        mImageButtonFlag = (ImageButton) getActivity().findViewById(R.id.flag_image_button);
        Utilities.setFlagOfImageButtonFlag(sp.getString(mActivity.getString(R.string.flag_key), "USA"), Badge, mImageButtonFlag);
        mImageButtonFlag.setOnClickListener(this);
        //end Flag button config

        /** About button configuration **/
        mAboutButton = (Button) getActivity().findViewById(R.id.about_button);
        mAboutButton.setOnClickListener(this);
        //end About button configuration

    }



    /**Gestione di eventi a seguito di uno Switch**/
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()){
            case R.id.background_music_switch: //Attiva o disattiva la musica di background

                Intent music = new Intent();
                music.setClass(getContext(), MusicService.class);

                if(isChecked){ //Se lo switch si è acceso attiva la musica di background
                    MusicServiceManager.getInstance().doBindService(mActivity);
                    MusicServiceManager.getInstance().startService(mActivity);
                    editor.putBoolean(mActivity.getString(R.string.background_music_key),
                            true);
                }else{ //Se lo switch si è spento disattiva la musica di background
                    MusicServiceManager.getInstance().doUnbindService(mActivity);
                    MusicServiceManager.getInstance().stopService(mActivity);
                    mActivity.stopService(music);
                    editor.putBoolean(mActivity.getString(R.string.background_music_key),
                            false);
                }
                editor.apply();

                break;

            case R.id.sound_effects_switch: //Attiva o disattiva effetti sonori
                if(isChecked){
                    SoundEffectsManager.enableSoundEffect();
                    //Change SharedPreference value with animation_sound_key
                    editor.putBoolean(mActivity.getString(R.string.animation_sound_key), true);

                }else{
                    SoundEffectsManager.disableSoundEffect();
                    //Change SharedPreference value with animation_sound_key
                    editor.putBoolean(mActivity.getString(R.string.animation_sound_key), false);
                }
                editor.apply();
                break;
        }
    }
    //ens switches' method

    /** onClick buttons method **/
    @Override
    public void onClick(View view) {
        Log.d(SETTING_FRAGMENT, "onClick: ");
        switch (view.getId()){

            case R.id.about_button: //About
                // inflate the layout of the popup window
                LayoutInflater inflaterAbout = (LayoutInflater)
                        mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupViewAbout = inflaterAbout.inflate(R.layout.popup_about, null);

                // create the popup window
                int widthPopupAbout = LinearLayout.LayoutParams.WRAP_CONTENT;
                int heightPopupAbout = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusablePopupAbout = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindowAbout = new PopupWindow(popupViewAbout, widthPopupAbout, heightPopupAbout, focusablePopupAbout);


                popupWindowAbout.showAtLocation(view, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupViewAbout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindowAbout.dismiss();
                        return true;
                    }
                });
                break;


            case R.id.language_image_button: //Language, riavvia l'app e cambia lingua

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.popup_language, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);


                //RadioGroup and RadioButtons instances
                final RadioGroup group = (RadioGroup) popupView.findViewById(R.id.radio_group);

                final RadioButton english_button = (RadioButton) group.findViewById(R.id.english_button);
                final RadioButton italian_button = (RadioButton) group.findViewById(R.id.italian_button);

                /** Put here new languages adding them in switch statement
                 * IMPORTANT: it is necessary to update even the switch statement inside onCheckedChanged**/

                final String[] selectedLanguage = {sp.getString(mActivity.getString(R.string.language_key), "English")}; //By default the selected language is english
                //is used a string[] to use selectedLanguage in the inner class onCheckedChanged

                switch (selectedLanguage[0]) { //Switch case to initialize the radioGroup
                    case "English":
                    case"Inglese":
                        english_button.setChecked(true);

                        break;
                    case "Italian":
                    case"Italiano":
                        italian_button.setChecked(true);

                        break;
                }

                //Quando viene scelta una nuova lingua, ottiene il nome della lingua a riavvia
                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        int childCount = group.getChildCount();

                        for (int x = 0; x < childCount; x++) {
                            RadioButton btn = (RadioButton) group.getChildAt(x);
                            if (btn.getId() == i) {
                                selectedLanguage[0] = btn.getText().toString();
                                editor.putString(mActivity.getString(R.string.language_key), selectedLanguage[0]); //salvataggio in shared preference per ricaricare in seguito
                                editor.apply();
                                Utilities.setFlagOfImageButtonLanguage(selectedLanguage[0], mImageButtonLanguage);
                                restartForLanguageChanges(); //riavvia per cambio lingua
                            }
                        }

                        popupWindow.dismiss();
                    }
                });
                //end language management

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

            break;

            case R.id.flag_image_button: //Flag, gestione dell'apertura del popup e modifica della bandiera nel Badge

                // inflate the layout of the popup window
                LayoutInflater inflater1 = (LayoutInflater)
                        mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView1 = inflater1.inflate(R.layout.popup_flag, null);

                // create the popup window
                int width1 = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height1 = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable1 = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow1 = new PopupWindow(popupView1, width1, height1, focusable1);

                //RadioGroup and RadioButtons instances
                final RadioGroup group1 = (RadioGroup) popupView1.findViewById(R.id.radio_group);
                //Log.d(SETTING_FRAGMENT, "onClick: group is null? " + group.equals(null) );
                final RadioButton usa_button = (RadioButton) group1.findViewById(R.id.usa_button);
                final RadioButton italy_button = (RadioButton) group1.findViewById(R.id.italy_button);
                final RadioButton australia_button = (RadioButton) group1.findViewById(R.id.australia_button);
                final RadioButton brazil_button = (RadioButton) group1.findViewById(R.id.brazil_button);

                /** Put here new flag adding them in switch statement
                 * IMPORTANT: it is necessary to update even the switch statement inside onCheckedChanged**/

                final String[] selectedFlag = {sp.getString(mActivity.getString(R.string.flag_key), "USA")}; //By default the selected language is english
                //is used a string[] to use selectedLanguage in the inner class onCheckedChanged


                switch (selectedFlag[0]) { //Switch case to initialize the radioGroup
                    case "USA":
                        usa_button.setChecked(true);
                        break;
                    case "Italy":
                    case "Italia":
                        italy_button.setChecked(true);
                        break;
                    case "Australia":
                        australia_button.setChecked(true);
                        break;
                    case "Brazil":
                    case "Brasile":
                        brazil_button.setChecked(true);
                        break;
                }

                //Quando viene selzionata una nuova nazione ottienine il nome e modifica la banidera nel badge
                group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        int childCount = group1.getChildCount();

                        for (int x = 0; x < childCount; x++) {
                            RadioButton btn = (RadioButton) group1.getChildAt(x);
                            if (btn.getId() == i) {
                                selectedFlag[0] = btn.getText().toString();
                                editor.putString(mActivity.getString(R.string.flag_key), selectedFlag[0]); //salvataggio in shared preference per ricaricare in seguito
                                editor.apply();
                                Utilities.setFlagOfImageButtonFlag(selectedFlag[0], Badge, mImageButtonFlag); //aggiorna bandiera

                            }
                        }

                        popupWindow1.dismiss();
                    }
                });

                popupWindow1.showAtLocation(view, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow1.dismiss();
                        return true;
                    }
                });



                break;

        }
    }
    //end onClick buttons method


    /**
     * UTILITY METHODS
     */

    //Dopo il cambio lingua l'applicazione viene riavviata
    public void restartForLanguageChanges(){
        Intent refresh = new Intent(getContext(), SplashActivity.class);
        mActivity.overridePendingTransition(0, 0);
        refresh.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mActivity.finish();
        mActivity.overridePendingTransition(0, 0);
        startActivity(refresh);

    }



}

