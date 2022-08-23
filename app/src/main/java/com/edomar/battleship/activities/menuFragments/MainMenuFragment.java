package com.edomar.battleship.activities.menuFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edomar.battleship.R;
import com.edomar.battleship.activities.MainActivity;
import com.edomar.battleship.activities.ScenarioSelectionActivity;
//import com.edomar.battleship.view.SinglePlayerGameActivity;
//import com.edomar.battleship.view.SinglePlayerGameActivity;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class MainMenuFragment extends Fragment {

    public static final String TAG = MainMenuFragment.class.getSimpleName();

    /**Shared Preference**/
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    /**GameModeMenu**/
    private TextView mTextViewGameMode;
    private String[] gameMode;
    private int position;
    private Animation mRightArrowAnim, mLeftArrowAnim;


    private MainActivity mActivity;

    public MainMenuFragment() {
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
        mActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        /** Initialize SharedPreference value **/
        sp = this.getActivity().getSharedPreferences(getString(R.string.configuration_preference_key), Context.MODE_PRIVATE);
        editor =  sp.edit();

        //Inizializza un vettore per visualizzare le modalità di gioco disponibili
        gameMode = new String[]{getString(R.string.single_player),
                getString(R.string.local_game),
                getString(R.string.online)};


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Locale current = getResources().getConfiguration().locale;
        Toast.makeText(getContext(), String.valueOf(current), Toast.LENGTH_LONG).show();



        //Gestione della Textview per visualizzare la modilità di gioco attualmente selezionabile
        mTextViewGameMode = (TextView) getActivity().findViewById(R.id.game_mode_text);

        position = sp.getInt(getString(R.string.configuration_preference_key), //Recupera l'ultima modalità di gioco selezionata o visualizzata
                0);                                   //Se non esiste visualizza Single Player


        mTextViewGameMode.setText(gameMode[position]);

        mTextViewGameMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedGameMode = ((TextView) view).getText().toString();
                goToScenarioSelectionActivity(selectedGameMode);
            }
        });

        //Gestione dell'attivazione dell'animazione a seguito della pressione delle frecce
        ImageView mRightArrow = (ImageView) mActivity.findViewById(R.id.right_arrow);
        mRightArrowAnim = AnimationUtils.loadAnimation(getContext(), R.anim.right_arrow_transiction);
        mRightArrow.setClickable(true);
        mRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = (position+1)%3;
                mTextViewGameMode.startAnimation(mRightArrowAnim);
                mTextViewGameMode.setText(gameMode[position]);
            }
        });

        ImageView mLeftArrow = (ImageView) mActivity.findViewById(R.id.left_arrow);
        mLeftArrowAnim = AnimationUtils.loadAnimation(getContext(), R.anim.left_arrow_transiction);
        mLeftArrow.setClickable(true);
        mLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = (position<= 0) ? position-1+3 : position-1;
                mTextViewGameMode.startAnimation(mLeftArrowAnim);
                mTextViewGameMode.setText(gameMode[position]);
            }
        });
    }

    private void goToScenarioSelectionActivity(String selectedGameMode) {
        Intent intent;
        intent = new Intent(getContext(), ScenarioSelectionActivity.class);
        switch (selectedGameMode) { //Indica all'activity successiva la modalità di gioco scelta (Necessaria per funzionalità successive)
            case "Single Player":
            case "Giocatore Singolo":
                intent.putExtra("gameMode", "single_player");
                break;
             case "1vs1":
                intent.putExtra("gameMode", "1vs1");
                break;
            case "Online":
                intent.putExtra("gameMode", "online");
                break;
            default:
                throw new IllegalArgumentException("No GameMode for the given item");
        }
        intent.putExtra("playerName", mActivity.mPlayerNameTextView.getText().toString());
        startActivity(intent);
        }


    @Override
    public void onPause() {
        super.onPause();
        //Quando il fragment non è èiù visualizzato salva l'ultima modalità di gioco mostrata
        editor.putInt(mActivity.getString(R.string.game_mode_key), position);
        editor.commit();
    }
}
