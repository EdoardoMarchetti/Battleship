package com.edomar.battleship.activities.gameplayFragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.edomar.battleship.activities.OnFragmentInteractionListener;
import com.edomar.battleship.utils.OnlineGameInfo;
import com.edomar.battleship.R;
import com.edomar.battleship.engines.AbstractBattleFieldEngine;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

/**
 * Il OnlineMatchFragment è utilizzato per le partite online
 */
public class OnlineMatchFragment extends Fragment implements IFragmentForBattlefield {

    public static final String TAG = "OnlineMatchFragment";

    //Instanza dell'activity che implementa OnFragmentInteractionListener
    private OnFragmentInteractionListener mListener;

    // The fragment initialization parameters
    private static final String PLAYER_NAME = "playerName";
    private static final String ADVERSARIAL_NAME = "adversarialName";
    private static final String PLAYER_NUMBER = "player_number";
    private static final String LEVEL_TO_LOAD = "scenario";

    //Variabili
    private String mPlayerName;
    private String mAdversarialName;
    private int mPlayerNumber;
    private String mLevelToLoad;

    /** CountDownTimer **/
    private TextView timer;
    float defaultTextSize;
    int defaultTextColor;
    private TextView timerTextView;
    private CountDownTimer mCounterDownTimer;
    long duration = TimeUnit.SECONDS.toMillis(20);

    /**Instanza del motore per la gestione del campo da battaglia**/
    private AbstractBattleFieldEngine mBattleField;


    public OnlineMatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pn giocatore che deve interagire con il fragment.
     * @param an giocatore avversario
     * @param n numero giocatore.
     * @param scenarioToLoad scenario da carica tra classic, standard, russian
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlineMatchFragment newInstance(String pn, String an, int n, String scenarioToLoad) {
        OnlineMatchFragment fragment = new OnlineMatchFragment();
        Bundle args = new Bundle();
        args.putString(PLAYER_NAME, pn);
        args.putString(ADVERSARIAL_NAME, an);
        args.putInt(PLAYER_NUMBER, n);
        args.putString(LEVEL_TO_LOAD, scenarioToLoad);
        fragment.setArguments(args);

        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlayerName = getArguments().getString(PLAYER_NAME);
            mAdversarialName = getArguments().getString(ADVERSARIAL_NAME);
            mPlayerNumber = getArguments().getInt(PLAYER_NUMBER);
            mLevelToLoad = getArguments().getString(LEVEL_TO_LOAD).toLowerCase();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        //Inizializzo la view con il layout corretto
        if(mPlayerNumber == 1){ //Deve interagire il giocatore 1
            view = inflater.inflate(R.layout.fragment_online_match, container, false);
        }else {//Deve interagire il giocatore 2 o il computer
            view = inflater.inflate(R.layout.fragment_online_match2, container, false);
        }

        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        FrameLayout fl;
        if(mPlayerNumber == 1){
            fl = (FrameLayout) getActivity().findViewById(R.id.fl1_online);
        }else{
            fl = (FrameLayout) getActivity().findViewById(R.id.fl2_online);
        }

        //Impostazioni per il Timer
        timer = fl.findViewById(R.id.timer);
        defaultTextSize = timer.getTextSize();
        defaultTextColor = timer.getCurrentTextColor();
        timerTextView = fl.findViewById(R.id.timer_text);

        //Impostazioni per il motore di gestione del campo da visualizzare
        //TextView in cui vanno inserite le coordinate
        ImageView letters = fl.findViewById(R.id.letters);
        ImageView numbers = fl.findViewById(R.id.numbers);

        mBattleField = fl.findViewById(R.id.battle_field);
        mBattleField.setZOrderOnTop(true);
        mBattleField.setFragmentReference(this);
        mBattleField.init(mLevelToLoad, mPlayerName, mAdversarialName);
        mBattleField.setImageViewsForCoordinates(letters, numbers);

        //Impostazioni per l'etichetta che indica il turno
        /** Turn Label **/
        TextView mTurnLabel = fl.findViewById(R.id.turn_of_player_name);
        mTurnLabel.setText(mPlayerName);
    }


    @Override
    public void onResume() {
        super.onResume();
        //Quando il fragment torna visibili riattiva anche il campo di battaglia e il timer
        mBattleField.setVisibility(View.VISIBLE);
        mBattleField.setZOrderOnTop(true);
        mBattleField.startThread();
        initCountDownTimer();
        mCounterDownTimer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Quando il fragment non è più visibile metti in pause il motore del campo di battaglia e cancella il timer
        mBattleField.stopThread();
        mBattleField.setVisibility(View.INVISIBLE);
        mCounterDownTimer.cancel();
    }

    @Override
    public void onDetach() {
        Log.d("Lifecycle", "onDetach:number "+ mPlayerNumber);
        super.onDetach();
    }




    //Inizializza il Timer
    public void initCountDownTimer(){
        Typeface tf = ResourcesCompat.getFont(getContext(), R.font.bad_crunge);
        mCounterDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {
                //Log.d(TAG, "onTick: is UIThread? "+ (Looper.myLooper() == Looper.getMainLooper()) + "fragment number = "+ mPlayerNumber);
                String sDuration = String.format(getResources().getConfiguration().locale,"%02d",
                        TimeUnit.MILLISECONDS.toSeconds(l));
                if(TimeUnit.MILLISECONDS.toSeconds(l) < 10){
                    timer.setText(sDuration);
                    timer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 45);
                    timer.setTextColor(Color.RED);

                    //Log.d("BOHC", "onTick: in if <10");
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
                    //Se il tempo finisce indica alla GameActivity di cambiare fragment
                    mListener.requestToChangeFragment(OnlineMatchFragment.this);
                }
            }
        };
    }

    /**-----------------------------------------------
     *-------IFragmentForBattlefield Methods----------
     *----------------------------------------------*/
    @Override
    public void notifyHit(int row, int column, boolean result) {
        //Quando il motore di gestione del campo indica che è stato eseguito un colpo
        //notifica alla GameActivty che deve essere cambiato il fragment
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mListener.requestToChangeFragment(OnlineMatchFragment.this);
            }
        }, 1500);
    }

    @Override
    public void notifyEndGame() {
        //Quando il motore di gestione del campo indica che tutte le navi sono state colpite
        //indica alla GameActivity di terminare la partita
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mListener.endGame(mPlayerName);
            }
        }, 1250);
    }
}