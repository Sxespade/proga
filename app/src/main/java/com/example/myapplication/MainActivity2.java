package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.example.myapplication.databinding.ActivityMain2Binding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {

    static private final String URL = "src/main/res/drawable/aaa.jpg";
    private static final int NUMBER = 4192;
    private static final int IMAGE_GALLERY_REQUEST = 1111;
    private static final int PICK_IMAGE = 0;
    private Uri pictureUri;
    Intent intent;

    private ActivityMain2Binding binding;
    MainPresenter mainPresenter = MainPresenter.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.button2.setOnClickListener((view1 -> {
            actionPick();
        }));

        binding.btnSend.setOnClickListener((v) -> {
            switch (mainPresenter.getCounter()) {
                case "1": {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("1", binding.editTextTextPersonName.getText().toString());
                    intent.putExtra("2", pictureUri.toString());
                    startActivity(intent);
                    break;
                }
                case "2": {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("1", binding.editTextTextPersonName.getText().toString());
                    intent.putExtra("2", pictureUri.toString());
                    startActivity(intent);
                    break;
                }
                case "3": {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("1", binding.editTextTextPersonName.getText().toString());
                    intent.putExtra("2", pictureUri.toString());
                    startActivity(intent);
                    break;
                }
            }
        });


    }


    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        binding.loadPic.setImageURI(data.getData());
        pictureUri = data.getData();
    }


    private void actionPick() {
//        pictureUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", createImageFile());



//         tell the camera where to save the image.
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
//
//         tell the camera to request WRITE permission.
//        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }


}