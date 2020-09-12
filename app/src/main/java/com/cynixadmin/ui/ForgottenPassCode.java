package com.cynixadmin.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cynixadmin.Constants.Constants;
import com.cynixadmin.Dialogs.ForgotPassword;
import com.cynixadmin.R;
import com.cynixadmin.models.Users;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgottenPassCode extends AppCompatActivity implements ForgotPassword.CynixForgotPasswordDialogListener {
    @BindView(R.id.reQuest)Button reQuest;
    @BindView(R.id.ok)Button ok;
    @BindView(R.id.checkUserName)TextInputEditText checkUserName;
    @BindView(R.id.checkEmail)TextInputEditText checkEmail;
    @BindView(R.id.checkPassCode)TextInputEditText checkPassCode;
    String EmailTxt1;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.forgot_password_dialog);
        ButterKnife.bind(this);
        openForgotPassCode();
        returnToLoginPage();
        createAuthProgressDialog();
    }

    private void returnToLoginPage() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
            }
        });
    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Cynix Movies");
        mAuthProgressDialog.setMessage("Loading...");
        mAuthProgressDialog.setCancelable(false);
    }

    private void openForgotPassCode() {
        reQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassword forgotPassword=new ForgotPassword();
                forgotPassword.show(getSupportFragmentManager(),"forgot password dialog");
            }
        });

    }

    @Override
    public void applyText(String EmailTxt) {
        if (TextUtils.isEmpty(EmailTxt)) {
            new SweetAlertDialog(ForgottenPassCode.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Email Field is Empty")
                    .show();
        } else {
            EmailTxt1=EmailTxt;
            checkDetails(EmailTxt1);
        }
    }

    private void checkDetails(String EmailTxt1) {
        reference= FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_CYNIX_ADMIN);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1:snapshot.getChildren()){
                    Users usersData=dataSnapshot1.getValue(Users.class);
                    if (usersData.getEmail().equals(EmailTxt1)){
                            if (Constants.FIREBASE_CHILD_CYNIX_ADMIN.equals("Admin")){
                                mAuthProgressDialog.dismiss();
                                new SweetAlertDialog(ForgottenPassCode.this,SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Hello "+usersData.getUserName())
                                        .show();
                                checkEmail.setText(usersData.getEmail());
                                checkUserName.setText(usersData.getUserName());
                                checkPassCode.setText(usersData.getPassword());
                            }
                    }else {
                        mAuthProgressDialog.dismiss();
                        new SweetAlertDialog(ForgottenPassCode.this,SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Wrong Email")
                                .show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mAuthProgressDialog.dismiss();
                new SweetAlertDialog(ForgottenPassCode.this,SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error: "+error)
                        .show();
            }
        });
    }

}