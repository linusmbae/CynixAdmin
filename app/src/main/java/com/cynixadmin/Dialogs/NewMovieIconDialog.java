package com.cynixadmin.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cynixadmin.Constants.Constants;
import com.cynixadmin.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;

public class NewMovieIconDialog extends DialogFragment {
    private NewMovieIconDialog.CynixDialogListener listener;
    private ImageView img;
    private EditText description,category;
    private Uri imgUri;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.activity_new_movie_icon_dialog,null);
        builder.setView(view)
                .setTitle("New Movie")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Uri Img=imgUri;
                        String Desc=description.getText().toString();
                        String Category=category.getText().toString();
                        listener.applyText(Img,Desc,Category);
                    }
                });
        img=view.findViewById(R.id.myMovieImage);
        description=view.findViewById(R.id.description);
        category=view.findViewById(R.id.Category);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        return builder.create();
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
            img.setImageURI(imgUri);
        }else {
            new SweetAlertDialog(getContext(),SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Failed To Upload File")
                    .show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener=(NewMovieIconDialog.CynixDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement Cynix Dialog Listener");
        }

    }

    public interface CynixDialogListener{
        void applyText(Uri img, String desc, String category);
    }
}
