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
    static private final String CHOOSER_TEXT = "Load " + URL + " with:";
    private static final int NUMBER = 4192;
    private static final int IMAGE_GALLERY_REQUEST = 20;
    private Uri pictureUri;
    public static final int CAMERA_REQUEST = 10;
    Intent intent;

    private ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.button2.setOnClickListener((view1 -> {
            invokeCamera();
//            onActivityResult(123, 123, intent);
        }));

        binding.btnSend.setOnClickListener((v) -> {
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("1",binding.editTextTextPersonName.getText().toString());
            intent.putExtra("2",pictureUri.toString());
            startActivity(intent);
        });


    }


    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        binding.imageView1.setImageURI(data.getData());
        pictureUri = data.getData();
    }


    private void invokeCamera() {
//        pictureUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", createImageFile());


        // tell the camera where to save the image.
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

        // tell the camera to request WRITE permission.
//        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, NUMBER);
    }



//    private File createImageFile() {
//        // the public picture director
//        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//
//        // timestamp makes unique name.
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        String timestamp = sdf.format(new Date());
//
//        // put together the directory and the timestamp to make a unique image location.
//        File imageFile = new File(picturesDirectory, "picture" + timestamp + ".jpg");
//
//        return imageFile;
//    }

    //    public void onImageGalleryClicked() {
//        // invoke the image gallery using an implict intent.
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//
//        // where do we want to find the data?
//        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        String pictureDirectoryPath = pictureDirectory.getPath();
//        // finally, get a URI representation
//        Uri data = Uri.parse(pictureDirectoryPath);
//
//        // set the data and type.  Get all image types.
//        photoPickerIntent.setDataAndType(data, "image/*");
//
//        // we will invoke this activity, and get something back from it.
//        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
//    }
}