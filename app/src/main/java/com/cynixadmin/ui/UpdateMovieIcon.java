package com.cynixadmin.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cynixadmin.R;
import com.cynixadmin.models.MoviesImage;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateMovieIcon extends AppCompatActivity {
    @BindView(R.id.myMovieImage3) ImageView myMovieImage3;
    @BindView(R.id.description3)  EditText description3;
    @BindView(R.id.Category3)  EditText Category3;


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

    }
}