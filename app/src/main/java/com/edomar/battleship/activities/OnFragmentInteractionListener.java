package com.edomar.battleship.activities;

import androidx.fragment.app.Fragment;

/** Quest'interfaccia permette di far COMUNICARE activity e fragment**/

public interface OnFragmentInteractionListener {

    //Il fragment invoca questo metodo per chiedere all'activity di cambiare il fragment da visualizzare
    void requestToChangeFragment(Fragment fragment);

    //Il fragment invoca questo metodo per indicare all'activity che la partita è finita
    void endGame(String playerName);
}
