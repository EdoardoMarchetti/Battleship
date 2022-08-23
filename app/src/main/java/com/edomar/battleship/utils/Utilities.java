package com.edomar.battleship.utils;

import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.edomar.battleship.R;

/**
 * Classe di utilità per la gestione delle stringhe (da uppercase a lowercase, da italiano a inglese )
 */

public class Utilities {

    private static final String TAG = "Utils";


    /**
     * setFlagOfBadge
     * metodo di utilità che ermette di impostare l'immagine della bandiera indipendentemente dalla lingua scelta
     * @param selectedFlag nome nazione
     * @param flagBadge imageview in cui posizionare la bandiera
     */
    public static void setFlagOfBadge(String selectedFlag, ImageView flagBadge) {
        Log.d(TAG, "setFlagOfBadge: i'm changing the badge ");
        switch (selectedFlag){
            case "USA":
                flagBadge.setImageResource(R.drawable.flag_usa);
                break;
            case "Italy":
            case "Italia":
                flagBadge.setImageResource(R.drawable.flag_italy);
                break;
            case "Australia":
                flagBadge.setImageResource(R.drawable.flag_australia);
                break;
            case "Brazil":
            case "Brasile":
                flagBadge.setImageResource(R.drawable.flag_brazil);
                break;
        }
    }

    public static void setFlagOfImageButtonFlag(String s, ImageView badge, ImageButton mImageButtonFlag) {
        Log.d(TAG, "setFlagOfBadge: i'm the imageButton flag");
        switch (s){
            case "USA" :
                mImageButtonFlag.setImageResource(R.drawable.flag_usa);
                break;
            case "Italy":
            case "Italia":
                mImageButtonFlag.setImageResource(R.drawable.flag_italy);
                break;
            case "Australia":
                mImageButtonFlag.setImageResource(R.drawable.flag_australia);
                break;
            case "Brazil":
            case "Brasile":
                mImageButtonFlag.setImageResource(R.drawable.flag_brazil);
                break;
        }
        setFlagOfBadge(s, badge);
    }

    public static void setFlagOfImageButtonLanguage(String selectedLanguage, ImageView imageButtonLanguage){
        switch (selectedLanguage) {
            case "English":
            case "Inglese":
                imageButtonLanguage.setImageResource(R.drawable.flag_usa);
                break;
            case "Italian":
            case "Italiano":
                imageButtonLanguage.setImageResource(R.drawable.flag_italy);
                break;
        }
    }

    public static String translateScenario(String scenarioSelected){
        scenarioSelected = scenarioSelected.toLowerCase();
        if(scenarioSelected.equals("russo") ){
            scenarioSelected = "russian";
        }else if(scenarioSelected.equals("classico")){
            scenarioSelected = "classic";
        }
        return scenarioSelected;
    }

    public static String convertEmailToString(String email) {
        String value = email.substring(0, email.indexOf("@"));
        value = value.replace(".", "");
        return value;
    }

    public static String translateLanguageName(String currentLanguage) {
        if(currentLanguage.equalsIgnoreCase("Italiano")){
            return "Italian";
        }else if(currentLanguage.equalsIgnoreCase("Inglese")){
            return "English";
        }
        return currentLanguage;
    }
}
