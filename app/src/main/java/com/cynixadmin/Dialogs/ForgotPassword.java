package com.cynixadmin.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cynixadmin.R;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPassword extends AppCompatDialogFragment {
    private TextInputEditText requestEmail;

    private ForgotPassword.CynixForgotPasswordDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.forgot_password_code_dialog,null);
        builder.setView(view)
                .setTitle("Forgot Password?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                    }
                })
                .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String uMail=requestEmail.getText().toString();

                        listener.applyText(uMail);
                    }
                });
        requestEmail=view.findViewById(R.id.requestEmail);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener=(ForgotPassword.CynixForgotPasswordDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement Cynix Dialog Listener");
        }

    }

    public interface CynixForgotPasswordDialogListener{
        void applyText( String TxtEmail);
    }

}
