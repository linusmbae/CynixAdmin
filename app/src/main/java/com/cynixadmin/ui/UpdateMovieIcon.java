package com.cynixadmin.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cynixadmin.Constants.Constants;
import com.cynixadmin.R;
import com.cynixadmin.models.MoviesImage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateMovieIcon extends AppCompatActivity {
    @BindView(R.id.myMovieImage3) ImageView myMovieImage3;
    @BindView(R.id.description3)  EditText description3;
    @BindView(R.id.Category3)  EditText Category3;
    @BindView(R.id.updateButton)Button update;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    StorageReference storageReference;
    StorageTask storageTask;
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_movie_icon);

        ButterKnife.bind(this);
        Intent intent=getIntent();
        if (intent!=null){
        Picasso.get().load(intent.getStringExtra("Icon")).fit().into(myMovieImage3);
        description3.setText(intent.getStringExtra("Description"));
        Category3.setText(intent.getStringExtra("Category"));
        }

        myMovieImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_movie();
            }
        });

    }

    private void update_movie() {
        String desc;
        String cat;
        cat=Category3.getText().toString();
        desc=description3.getText().toString();
        Toast.makeText(this, desc+" "+cat, Toast.LENGTH_SHORT).show();
    }

    private void openFileChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constants.IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Constants.IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imgUri=data.getData();
            myMovieImage3.setImageURI(imgUri);
        }else {
            new SweetAlertDialog(getApplicationContext(),SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Failed To Upload File")
                    .show();
        }
    }
}