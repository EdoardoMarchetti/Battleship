package com.edomar.battleship.activities.gameplayFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.edomar.battleship.R;
import com.edomar.battleship.activities.OnFragmentInteractionListener;
import com.edomar.battleship.engines.AbstractBattleFieldEngine;




/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostMatchFragment extends Fragment {


    /** BattleField Instance**/
    private AbstractBattleFieldEngine mBattleField;

    /** TextView instance**/
    private TextView mPlayerNameLabel;

    /** ImageView instance**/
    private ImageView mRightArrow;
    private ImageView mLeftArrow;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PLAYER_OWNER = "player_owner";
    private static final String LEVEL = "level";
    private static final String PLAYER_NUMBER = "player_number";


    // TODO: Rename and change types of parameters
    private String mPlayerOwner;
    private String mLevel;
    private int mPlayerNumber;
    private OnFragmentInteractionListener mListener;



    public PostMatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param playerOwner Giocatore di cui mostrare la griglia
     * @param level livello da caricare (utile per recuperare file)
     * @param playerNumber utile per scegliere il giusto layout
     *
     * @return A new instance of fragment PostMatchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostMatchFragment newInstance(String playerOwner, String level, int playerNumber) {
        PostMatchFragment fragment = new PostMatchFragment();
        Bundle args = new Bundle();
        args.putString(PLAYER_OWNER, playerOwner);

        args.putString(LEVEL, level);
        Log.d("POST", "newInstance: level = "+ level);

        args.putInt(PLAYER_NUMBER, playerNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlayerOwner = getArguments().getString(PLAYER_OWNER);
            Log.d("POST", "onCreate: mLevel = "+getArguments().getInt(LEVEL));
            mLevel = getArguments().getString(LEVEL);
            Log.d("POST", "onCreate: mLevel = "+mLevel);
            mPlayerNumber = getArguments().getInt(PLAYER_NUMBER);
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
        // Inflate the layout for this fragment
        if(mPlayerNumber == 1){
            return inflater.inflate(R.layout.fragment_post_match_winner, container, false);
        }else{
            return inflater.inflate(R.layout.fragment_post_match_loser, container, false);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        FrameLayout fl;
        if(mPlayerNumber == 1){
            fl = (FrameLayout) getActivity().findViewById(R.id.fl_winner);
        }else{
            fl = (FrameLayout) getActivity().findViewById(R.id.fl_loser);

        }

        //ImageView delle coordinate
        ImageView letters = (ImageView) fl.findViewById(R.id.letters);
        ImageView numbers = (ImageView) fl.findViewById(R.id.numbers);

        //Creazione SurfaceView
        mBattleField = fl.findViewById(R.id.battle_field);
        mBattleField.setZOrderOnTop(true);
        mBattleField.init(mLevel, mPlayerOwner, ""); //in questo caso mi interessa solo il nome del giocatore per recuperare la griglia dal file
        mBattleField.setImageViewsForCoordinates(letters, numbers);

        //textView
        mPlayerNameLabel = (TextView) fl.findViewById(R.id.player_name_label);
        mPlayerNameLabel.setText(mPlayerOwner);

        //ImageView
        if(mPlayerNumber == 1){
            mRightArrow = (ImageView) fl.findViewById(R.id.right_arrow);
            mRightArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.requestToChangeFragment(PostMatchFragment.this);
                }
            });
        }else{
            mLeftArrow = (ImageView) fl.findViewById(R.id.left_arrow);
            mLeftArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.requestToChangeFragment(PostMatchFragment.this);
                }
            });
        }


    }

    @Override
    public void onResume() {

        super.onResume();
        //Quando il fragment è visibile attiva anche il campo di battaglia e il timer
        mBattleField.setVisibility(View.VISIBLE);
        mBattleField.setZOrderOnTop(true);
        mBattleField.startThread();
    }

    @Override
    public void onPause() {

        super.onPause();
        //Quando il fragment non è più visibile termina il motore del campo di battaglia e cancella il timer
        mBattleField.stopThread();
        mBattleField.setVisibility(View.INVISIBLE);

    }




    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}