package com.edomar.battleship.activities.gameplayFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edomar.battleship.R;
import com.edomar.battleship.activities.OnFragmentInteractionListener;
import com.edomar.battleship.engines.AbstractBattleFieldEngine;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnlinePreMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlinePreMatchFragment extends Fragment implements View.OnClickListener {

    // The fragment initialization parameters
    private static final String PLAYER_NAME = "playerName";
    private static final String ADVERSARIAL_NAME = "adversarialName";
    private static final String LEVEL_TO_LOAD = "scenario";


    // Variabili
    private String mPlayerName;
    private String mAdversarialName;
    private String mLevelToLoad;


    /**Shared Preference**/
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    /**Activity reference to call methods**/
    private OnFragmentInteractionListener mListener;

    /**Start Match Button**/
    private Button mStartMatchButton;

    /**Instanza del motore per la gestione del campo da battaglia**/
    private AbstractBattleFieldEngine mBattleField;


    /** Count Down Timer **/
    private TextView timer;
    int defaultTextColor;
    private TextView timerTextView;
    private CountDownTimer mCounterDownTimer;
    long duration = TimeUnit.SECONDS.toMillis(60);



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *@param pn giocatore che deve interagire con il fragment.
     *@param an giocatore avversario
     *@param scenarioToLoad scenario da carica tra classic, standard, russian
     *@return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlinePreMatchFragment newInstance(String pn, String an, String scenarioToLoad) {
        OnlinePreMatchFragment fragment = new OnlinePreMatchFragment();
        Bundle args = new Bundle();
        args.putString(PLAYER_NAME, pn);
        args.putString(ADVERSARIAL_NAME, an);
        args.putString(LEVEL_TO_LOAD, scenarioToLoad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlayerName = getArguments().getString(PLAYER_NAME);
            mAdversarialName = getArguments().getString(ADVERSARIAL_NAME);
            mLevelToLoad = getArguments().getString(LEVEL_TO_LOAD).toLowerCase();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            mListener = (OnFragmentInteractionListener) context;
        }else{
            mListener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_pre_match, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //Timer
        timer = (TextView) getActivity().findViewById(R.id.timer);
        defaultTextColor = timer.getCurrentTextColor();
        timerTextView = (TextView) getActivity().findViewById(R.id.timer_text);



        //ImageView delle coordinate
        ImageView letters = (ImageView) getActivity().findViewById(R.id.letters);
        ImageView numbers = (ImageView) getActivity().findViewById(R.id.numbers);

        //Creazione SurfaceView
        mBattleField = getActivity().findViewById(R.id.battle_field);
        mBattleField.setZOrderOnTop(true);
        mBattleField.init(mLevelToLoad, mPlayerName, mAdversarialName);
        mBattleField.setImageViewsForCoordinates(letters, numbers);

        //Button
        mStartMatchButton = getActivity().findViewById(R.id.start_match_button);
        mStartMatchButton.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        //Log.d(TAG, "onResume: ");
        super.onResume();
        //Quando il fragment è visibile attiva anche il campo di battaglia e il timer
        mBattleField.setVisibility(View.VISIBLE);
        mBattleField.setZOrderOnTop(true);
        mBattleField.startThread();
        initCountDownTimer();
        mCounterDownTimer.start();
    }


    @Override
    public void onPause() {
        //Log.d(TAG, "onPause: ");
        super.onPause();
        //Quando il fragment non è più visibile termina il motore del campo di battaglia e cancella il timer
        mBattleField.stopThread();
        mCounterDownTimer.cancel();

    }

    //Inizializza il Timer
    public void initCountDownTimer(){
        Typeface tf = ResourcesCompat.getFont(getContext(), R.font.bad_crunge);

        mCounterDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {

                String sDuration = String.format(getResources().getConfiguration().locale,"%02d",
                        TimeUnit.MILLISECONDS.toSeconds(l));
                if(TimeUnit.MILLISECONDS.toSeconds(l) < 10){
                    timer.setText(sDuration);
                    timer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 45);
                    timer.setTextColor(Color.RED);


                }else{
                    timer.setText(sDuration);
                    timer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
                    timer.setTextColor(defaultTextColor);
                    timerTextView.setTypeface(tf, Typeface.NORMAL);
                }
            }

            @Override
            public void onFinish() {
                //TODO
                if(mListener != null){
                    while(!mBattleField.saveFleet(mLevelToLoad, mPlayerName, true)){ //tenta il salvataggio fino a quando non avviene
                        //Do nothing
                    };
                    mListener.requestToChangeFragment(OnlinePreMatchFragment.this);
                }else{
                    Toast.makeText(getContext(), "mListener is null", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        //Quando il pulsante viene premuto inizia il match dopo aver indicato al motore del campo di battaglia di salvare la flotta
        if(mBattleField.saveFleet(mLevelToLoad, mPlayerName, false)){
            //Se non ci sono errori avvia
            mListener.requestToChangeFragment(OnlinePreMatchFragment.this);
            timerTextView.setText("Ready to play");
        }else{
            //Altrimenti visualizza un messaggio di errore
            Toast.makeText(getContext(), "Errore di avvio", Toast.LENGTH_SHORT).show();
        }

    }
}