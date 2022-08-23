package com.edomar.battleship.activities.menuFragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;



import com.edomar.battleship.engines.AbstractBattleFieldEngine;
import com.edomar.battleship.R;
import com.edomar.battleship.utils.Utilities;
import com.edomar.battleship.activities.MainActivity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class FleetFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = FleetFragment.class.getSimpleName();

    private MainActivity mActivity;


    /**Shared Preference**/
    SharedPreferences sp;
    SharedPreferences.Editor editor;


    /** BattleField Instance**/
    private AbstractBattleFieldEngine mBattleField;
    private String levelToLoad = "russian"; //Come livello di default mostra russian



    public FleetFragment() {
        //Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        Log.d(TAG, "onAttach: ");
        super.onAttach(activity);
        mActivity = (MainActivity) activity;

    }



    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
        mActivity= null;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fleet, container, false);

        /** Initialize SharedPreference value **/
        sp = this.getActivity().getSharedPreferences(getString(R.string.configuration_preference_key), Context.MODE_PRIVATE);
        editor =  sp.edit();
        //end initialization

        levelToLoad = Utilities.translateScenario(sp.getString(mActivity.getString(R.string.level_key), "classic" )).toLowerCase();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //ImageView delle coordinate
        ImageView letters = (ImageView) mActivity.findViewById(R.id.letters);
        ImageView numbers = (ImageView) mActivity.findViewById(R.id.numbers);


        //Creazione SurfaceView
        mBattleField = mActivity.findViewById(R.id.battle_field);
        mBattleField.setZOrderOnTop(true);
        mBattleField.init(levelToLoad, "", ""); //"" IS A DEFAULT STRING
        mBattleField.setImageViewsForCoordinates(letters, numbers);


        //init pulsante
        /**Save Button**/
        Button mSaveButton = (Button) getActivity().findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(this);

        Button mLevelButton = (Button) getActivity().findViewById(R.id.change_scenario);
        mLevelButton.setOnClickListener(this);

    }



    @Override
    public void onResume() {
        Log.d("Thread", "onResume: ");
        super.onResume();
        //Il fragment è pronto...avvia il motore di gestione del campo
        mBattleField.startThread();
    }


    @Override
    public void onPause() {
        Log.d("Thread", "onPause: ");
        super.onPause();
        //Il fragment è sospeso....ferma il motore di gestione
        mBattleField.stopThread();

        //Salva nelle schared configuration l'ultimo livello visualizzato
        editor.putString(mActivity.getString(R.string.level_key), levelToLoad);
        editor.commit();

    }


    /**
     * Gestione dell'evento di pressione del pulsante
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.save_button: //Save
                if(mBattleField.saveFleet(levelToLoad, "", false)){//Tenta di salvare la configurazione di flotta mostrata a schermo
                    Toast.makeText(getContext(), "Salvataggio riuscito", Toast.LENGTH_SHORT).show();
                }else{//Se le navi sono in una posizione non consentita dalle regole del livello mostra un messaggio di errore
                    Toast.makeText(getContext(), "Errore posizionamento", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.change_scenario: //Mostra il popup per cambiare scenario

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.popup_scenario, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                //Rileva quale pulsante del popup è stato premuto e cambia scenario di conseguenza
                final Button russian = (Button) popupView.findViewById(R.id.russian_button);
                russian.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        levelToLoad = russian.getText().toString();
                        notifyLevelToShowChanged(levelToLoad);  //carica russian
                        popupWindow.dismiss();
                    }
                });

                final Button classic = (Button) popupView.findViewById(R.id.classic_button);
                classic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        levelToLoad = classic.getText().toString();
                        notifyLevelToShowChanged(levelToLoad);  //carica classic
                        popupWindow.dismiss();
                    }
                });


                final Button standard = (Button) popupView.findViewById(R.id.standard_button);
                standard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        levelToLoad = standard.getText().toString();
                        notifyLevelToShowChanged(levelToLoad);  //carica standard
                        popupWindow.dismiss();
                    }
                });

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

        }

    }

    /**
     * notifyLevelToShowChanged
     * Notifica al motore di gestione del campo di impostare un nuovo livello
     * @param levelToLoad
     */
    private void notifyLevelToShowChanged(String levelToLoad) {
        levelToLoad = Utilities.translateScenario(levelToLoad);
        mBattleField.setLevel(levelToLoad, "");
    }


}
