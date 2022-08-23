package com.edomar.battleship.activities.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.edomar.battleship.R;
import com.edomar.battleship.activities.OnDialogInteractionListener;
import com.edomar.battleship.utils.Utilities;

import androidx.annotation.NonNull;

public class LoginDialog extends Dialog implements View.OnClickListener  {

    private OnDialogInteractionListener mListener;

    private EditText etEmail;
    private EditText etPassword;

    /**Costruttore**/
    public LoginDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * setListener
     * Permettere di aggiungere l'activity su cui viene mostrato come listener
     * L'activty deve implemetare l'interfaccia OnDialogInteractionListener
     * @param listener
     */
    public void setListener(OnDialogInteractionListener listener){
        mListener = listener;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        //EditText
        etEmail = (EditText) findViewById(R.id.email_login);
        etPassword = (EditText) findViewById(R.id.password_login);

        //Buttons
        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);
        Button goToRegisterDialogButton = (Button) findViewById(R.id.goToRegisterDialogButton);
        goToRegisterDialogButton.setOnClickListener(this);

        //Imposta dimensioni
        Window w = getWindow();
        w.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            //Se viene premuto il signInButton viene avviata la procedura per fare il login di un account già creato
            case (R.id.signInButton):
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();


                if(checkEmail(email) && checkPassword(password)){
                    String playerName = Utilities.convertEmailToString(etEmail.getText().toString());
                    mListener.loginUser(etEmail.getText().toString(),
                            etPassword.getText().toString());
                    dismiss();
                }else{
                    if(!checkEmail(email)){
                        //fai apparire la textView di errore per l'email
                        TextView emailWarningTextView = findViewById(R.id.warningEmailLogin);
                        emailWarningTextView.setVisibility(View.VISIBLE);
                    }

                    if(!checkEmail(password)){
                        //fai apparire la textView di errore per la password
                        TextView passwordWarningTextView = findViewById(R.id.warningPasswordLogin);
                        passwordWarningTextView.setVisibility(View.VISIBLE);
                    }
                }
                break;

            //Se si preme il goToRegisterDialogButton viene aperta un nuovo RegisterDialog per la registrazione di un nuovo account
            //e vine chiuso il dialog corrente
            case (R.id.goToRegisterDialogButton):
                mListener.openRegisterDialog();
                dismiss();
                break;

        }

    }

    /**-----------------------------------------------
     *-------------METODI DI UTILITA'-----------------
     *----------------------------------------------*/
    private boolean checkPassword(String password) {
        if(password.length() < 8){
            return false;
        }
        return true;
    }

    private boolean checkEmail(String email) {
        if(email.isEmpty() || !email.contains("@")){
            return false;
        }
        return true;
    }
}
