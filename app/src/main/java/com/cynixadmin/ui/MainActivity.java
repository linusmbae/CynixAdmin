package com.cynixadmin.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.cynixadmin.Adapter.MoviesIconAdapter;
import com.cynixadmin.Constants.Constants;
import com.cynixadmin.Dialogs.NewMovieIconDialog;
import com.cynixadmin.Dialogs.NewUserDialog;
import com.cynixadmin.R;
import com.cynixadmin.models.MoviesImage;
import com.cynixadmin.models.Users;
import com.cynixadmin.services.RecyclerTouchListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements NewMovieIconDialog.CynixDialogListener, NewUserDialog.CynixDialogListener {
    private static final String TAG = "success";
    @BindView(R.id.recyclerViewMovies)RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshIconLayout)SwipeRefreshLayout mRefresh;

    private ArrayList<MoviesImage> mMovieIcon;
    private MoviesIconAdapter mAdapter;
    private ProgressDialog mAuthProgressDialog;


    private String userName;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    StorageReference storageReference;
    StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        Intent intent=getIntent();
//        userName=intent.getStringExtra("userNames");
//        getSupportActionBar().setTitle("Welcome "+userName);

        rootNode = FirebaseDatabase.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference(Constants.FIREBASE_CHILD_MOVIE_IMAGE);
        reference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_MOVIE_IMAGE);

        mMovieIcon=new ArrayList<MoviesImage>();
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(MainActivity.this,2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        createAuthProgressDialog();
        getMovieIcons();
        refresh();
        selectMovieIcon();
    }

    private void selectMovieIcon() {
        Context context=this;
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                AlertDialog dialog=new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.pofile)
                        .setTitle("Cynix Movies")
                        .setMessage("Confirm To Proceed")
                        .setPositiveButton("Post Movie",null)
                        .setNegativeButton("Update Icon",null)
                        .show();

                Button positiveButton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "positive", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                Button negativeButton=dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), UpdateMovieIcon.class);
                        intent.putExtra("Icon",mMovieIcon.get(position).getImage());
                        intent.putExtra("Description",mMovieIcon.get(position).getDescription());
                        intent.putExtra("Category",mMovieIcon.get(position).getCategory());
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void refresh() {
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mMovieIcon=new ArrayList<MoviesImage>();
                mAuthProgressDialog.show();
                reference= FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_MOVIE_IMAGE);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mAuthProgressDialog.dismiss();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            MoviesImage moviesImage=dataSnapshot1.getValue(MoviesImage.class);
                            mMovieIcon.add(moviesImage);
                        }
                        if (mMovieIcon!=null){
                            mAdapter=new MoviesIconAdapter(MainActivity.this,mMovieIcon);
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }else {
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops!.. There is no data in the database")
                                    .show();
                        }
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mAuthProgressDialog.dismiss();
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops!.. We encountered an error kindly try again later")
                                .show();
                    }
                });
            }

        });
    }

    private void getMovieIcons() {
        mAuthProgressDialog.show();
        reference= FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_MOVIE_IMAGE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAuthProgressDialog.dismiss();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    MoviesImage moviesImage=dataSnapshot1.getValue(MoviesImage.class);
                    mMovieIcon.add(moviesImage);
                }
                if (mMovieIcon!=null){
                    mAdapter=new MoviesIconAdapter(MainActivity.this,mMovieIcon);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }else {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops!.. There is no data in the database")
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mAuthProgressDialog.dismiss();
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops!.. We encountered an error kindly try again later")
                        .show();
            }
        });
    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Cynix Movies");
        mAuthProgressDialog.setMessage("Please Wait...");
        mAuthProgressDialog.setCancelable(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cynix_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logoutCynix) {
            logout();
            return true;
        }
        if (id == R.id.myMovies) {
            newMovie();
            return true;
        }
        if (id == R.id.CreateNewUser) {
            newUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void newUser() {
        NewUserDialog newUserDialog = new NewUserDialog();
        newUserDialog.show(getSupportFragmentManager(), "new user dialog");
    }

    private void newMovie() {
        NewMovieIconDialog newMovieDialog = new NewMovieIconDialog();
        newMovieDialog.show(getSupportFragmentManager(), "new movie dialog");
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public void applyText(Uri imageUri, String Description,String Category) {
        if (TextUtils.isEmpty(Description)) {
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Description Field is Empty")
                    .show();
        }else  if (TextUtils.isEmpty(Category)) {
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Category Field is Empty")
                    .show();
        }else {
            if (storageTask!=null&&storageTask.isInProgress()){
                Toast.makeText(this, "Upload in Progress", Toast.LENGTH_LONG).show();
            }else {
                if (imageUri!=null){

                    StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
                    storageTask=fileReference.putFile(imageUri);

                    Task<Uri> urlTask = storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Upload Successful")
                                        .show();
                                String downloadUri = task.getResult().toString();
                                MoviesImage moviesImage=new MoviesImage(downloadUri,
                                        Description.trim(),
                                        Category.trim());
                                DatabaseReference pushRef = reference.push();
                                pushRef.setValue(moviesImage);
                            } else {
                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Upload Failed")
                                        .show();
                            }
                        }
                    });
                }else {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("No file Selected")
                            .show();
                }
            }


        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void applyText(String TxtUserName, String TxtEmail, String TxtPassword) {
        if (TextUtils.isEmpty(TxtUserName)) {
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Username Field is Empty")
                    .show();
        } else if (TextUtils.isEmpty(TxtEmail)) {
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Email Field is Empty")
                    .show();
        } else if (TextUtils.isEmpty(TxtPassword)) {
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Password Field is Empty")
                    .show();
        } else {
            String mUserName = TxtUserName;
            String email = TxtEmail;
            String pass = TxtPassword;

           try {
               new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
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
           }catch (NullPointerException ex){
               Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
           }

        }

    }
}