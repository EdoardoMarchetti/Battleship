package com.edomar.battleship.activities.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edomar.battleship.R;
import com.edomar.battleship.activities.OnDialogInteractionListener;
import com.edomar.battleship.utils.Utilities;

import androidx.annotation.NonNull;

public class RegisterDialog extends Dialog implements View.OnClickListener {

    private final Context mContext;
    private OnDialogInteractionListener mListener;

    private EditText etEmail;
    private EditText etPassword;

    /**Costruttore**/
    public RegisterDialog(@NonNull Context context) {

        super(context);
        mContext = context;
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
        //TextView del titolo
        //TextView textView = (TextView) findViewById(R.id.register_dialog_title);

        //EditText
        etEmail = (EditText) findViewById(R.id.email_registration);
        etPassword = (EditText) findViewById(R.id.password_registration);

        //Buttons
        Button confirmButton = (Button) findViewById(R.id.confirmRegistrationButton);
        confirmButton.setOnClickListener(this);
        Button gToLoginDialog = (Button) findViewById(R.id.goToLoginDialog);
        gToLoginDialog.setOnClickListener(this);

        //Imposta dimensioni
        Window w = getWindow();
        w.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            //Se viene premuto il confirmRegistrationButton viene avviata la procedura per fare il login con un nuovo account
            case(R.id.confirmRegistrationButton):
                Toast.makeText(mContext, "confirm", Toast.LENGTH_SHORT).show();


                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(checkEmail(email) && checkPassword(password)){
                    String playerName = Utilities.convertEmailToString(etEmail.getText().toString());
                    mListener.registerUser(etEmail.getText().toString(),
                            etPassword.getText().toString());
                    dismiss();
                }else{
                    if(!checkEmail(email)){
                        //fai apparire la textView di errore per l'email
                        TextView emailWarningTextView = findViewById(R.id.warningEmailRegistration);
                        emailWarningTextView.setVisibility(View.VISIBLE);
                    }

                    if(!checkEmail(password)){
                        //fai apparire la texyView di errore per la password
                        TextView passwordWarningTextView = findViewById(R.id.warningPasswordRegistration);
                        passwordWarningTextView.setVisibility(View.VISIBLE);
                    }
                }

                break;

            //Se si si preme il goToLoginDialog viene aperta un nuovo LoginDialog per fare il login
            //e vine chiuso il dialog corrente
            case(R.id.goToLoginDialog):
                mListener.openLoginDialog();
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
