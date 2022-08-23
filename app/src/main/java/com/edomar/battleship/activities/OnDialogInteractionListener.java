package com.edomar.battleship.activities;

public interface OnDialogInteractionListener {

    void registerUser(String email, String password);

    void loginUser(String email, String password);

    void openLoginDialog();

    void openRegisterDialog();
}
