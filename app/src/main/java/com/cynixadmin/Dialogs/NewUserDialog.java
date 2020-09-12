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

public class NewUserDialog extends AppCompatDialogFragment {
    private TextInputEditText userName;
    private TextInputEditText email;
    private TextInputEditText password;

    private CynixDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.activity_new_user_dialog,null);
        builder.setView(view)
                .setTitle("Create Account")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String uName=userName.getText().toString();
                        String uMail=email.getText().toString();
                        String uPass=password.getText().toString();

                        listener.applyText(uName,uMail,uPass);
                    }
                });
        userName=view.findViewById(R.id.UserName1);
        email=view.findViewById(R.id.email1);
        password=view.findViewById(R.id.password1);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener=(CynixDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement Cynix Dialog Listener");
        }

    }

    public interface CynixDialogListener{
        void applyText(String TxtUserName, String TxtEmail, String TxtPassword);
    }

}
