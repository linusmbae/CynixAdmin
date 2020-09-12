package com.cynixadmin.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.cynixadmin.Constants.Constants;
import com.cynixadmin.Dialogs.ForgotPassword;
import com.cynixadmin.Dialogs.NewUserDialog;
import com.cynixadmin.R;
import com.cynixadmin.models.Users;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity implements NewUserDialog.CynixDialogListener {
    @BindView(R.id.newAcount)Button newAccount;
    @BindView(R.id.login)Button login;
    @BindView(R.id.forgotPass)Button forgotPass;
    @BindView(R.id.email)TextInputEditText mEmail;
    @BindView(R.id.password)TextInputEditText mPassword;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private ProgressDialog mAuthProgressDialog;

    private String email;
    private String password;
    public ArrayList<Users>mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        createAuthProgressDialog();

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewUser();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminLogin();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPassCode();
            }
        });

    }


    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Cynix Movies");
        mAuthProgressDialog.setMessage("Authenticating Credentials...");
        mAuthProgressDialog.setCancelable(false);
    }

    private void adminLogin() {
        email = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)){
            mAuthProgressDialog.dismiss();
            mEmail.setError("Please enter your Email");
        }else if (TextUtils.isEmpty(password)){
            mAuthProgressDialog.dismiss();
            mPassword.setError("Please enter yor password");
        }else {
            mAuthProgressDialog.show();
            AllowAccess(email,password);
        }
    }

    private void AllowAccess(String email, String password) {
        mUsers=new ArrayList<Users>();
        reference= FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_CYNIX_ADMIN);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot1:snapshot.getChildren()){
                    Users usersData=dataSnapshot1.getValue(Users.class);
                    if (usersData.getEmail().equals(email)){
                        if (usersData.getPassword().equals(password)){
                            if (Constants.FIREBASE_CHILD_CYNIX_ADMIN.equals("Admin")){
                                mAuthProgressDialog.dismiss();
                                new SweetAlertDialog(Login.this,SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Login Successful")
                                        .show();

                                String name=usersData.getUserName();
                                Intent intent = new Intent(Login.this,MainActivity.class);
                                intent.putExtra("userNames",name);
                                startActivity(intent);
                                finish();
                            }
                        }else {
                            mAuthProgressDialog.dismiss();
                            new SweetAlertDialog(Login.this,SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Wrong Password")
                                    .show();
                        }
                    }else {
                        mAuthProgressDialog.dismiss();
                        new SweetAlertDialog(Login.this,SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Wrong Email")
                                .show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mAuthProgressDialog.dismiss();
                new SweetAlertDialog(Login.this,SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error: "+error)
                        .show();
            }
        });
    }

    private void addNewUser() {
        NewUserDialog newUserDialog = new NewUserDialog();
        newUserDialog.show(getSupportFragmentManager(), "new user dialog");
    }

    private void openForgotPassCode() {
        Intent intent =new Intent(Login.this,ForgottenPassCode.class);
        startActivity(intent);
    }

    @Override
    public void applyText(String TxtUserName, String TxtEmail, String TxtPassword) {
        if (TextUtils.isEmpty(TxtUserName)) {
            new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Username Field is Empty")
                    .show();
        } else if (TextUtils.isEmpty(TxtEmail)) {
            new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Email Field is Empty")
                    .show();
        } else if (TextUtils.isEmpty(TxtPassword)) {
            new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Password Field is Empty")
                    .show();
        } else {
            String mUserName = TxtUserName;
            String email = TxtEmail;
            String pass = TxtPassword;

            new SweetAlertDialog(Login.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .show();

            Users users;
            users=new Users(email,pass,mUserName);


            rootNode = FirebaseDatabase.getInstance();

            reference = FirebaseDatabase
                    .getInstance()
                    .getReference(Constants.FIREBASE_CHILD_CYNIX_ADMIN);

            DatabaseReference pushRef = reference.push();

            pushRef.setValue(users);

        }
    }
}