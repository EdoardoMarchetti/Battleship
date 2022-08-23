package com.edomar.battleship.utils;

import android.util.Log;

/**
 * Classe di appoggio per avere a disposizione le info del match online anche nel motore di gestione del Battlefield
 */

public class OnlineGameInfo {

    private static final String TAG = "InfoOnlineGame";

    private String player_session;
    private String player_name;
    private String other_player;
    private String request_type;
    private String scenario;

    private static OnlineGameInfo sInstance;

    private OnlineGameInfo(){

    }

    public static OnlineGameInfo getInstance(){
        if(sInstance == null){
            sInstance = new OnlineGameInfo();
        }
        return sInstance;
    }

    public static void deleteInstance(){
        sInstance = null;
    }

    /**SETTER**/
    public void setMatchID(String ps){
        player_session = ps;
        Log.d(TAG, "setPlayerSession: "+player_session);
    }

    public void setPlayerName(String pn){
        player_name = pn;
        Log.d(TAG, "setPlayerName: "+player_name);
    }

    public void setOtherPlayer(String op){
        other_player = op;
        Log.d(TAG, "setOtherPlayer: "+other_player);
    }

    public void setRequestType(String rt){
        request_type = rt;
        Log.d(TAG, "setRequestType: "+request_type);
    }

    public void setScenario(String scen){
        scenario = scen;
        Log.d(TAG, "setScenario: "+scenario);
    }

    /**GETTER**/
    public String getMatchID() {
        return player_session;
    }

    public String getLocalPlayer() {
        return player_name;
    }

    public String getOtherPlayer() {
        return other_player;
    }

    public String getRequestType() {
        return request_type;
    }

    public String getScenario() {
        return scenario;
    }


}
