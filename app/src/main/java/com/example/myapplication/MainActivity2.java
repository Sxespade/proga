package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMain2Binding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;


public class MainActivity2 extends AppCompatActivity {

    static private final String URL = "src/main/res/drawable/aaa.jpg";
    private static final int NUMBER = 4192;
    private static final int IMAGE_GALLERY_REQUEST = 1111;
    private static final int PICK_IMAGE = 0;
    private Uri pictureUri;
    Intent intent;
    StorageReference riversRef;

    private ActivityMain2Binding binding;
    MainPresenter mainPresenter = MainPresenter.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.progressBar.setVisibility(View.INVISIBLE);

        binding.button2.setOnClickListener((view1 -> {
            actionPick();
            binding.btnSend.setEnabled(false);
        }));

        binding.btnSend.setOnClickListener((v) -> {
            switch (mainPresenter.getCounter()) {
                case "1": {
                    initBtnSendAction();
                    break;
                }
                case "2": {
                    initBtnSendAction();
                }
                case "3": {
                    initBtnSendAction();
                }
            }
        });


    }

    private void initBtnSendAction() {
        if (binding.editTextTextPersonName.getText().toString().equals("")) {
            Toast.makeText(this, "Пожалуйста, введите название фильма", Toast.LENGTH_SHORT).show();
        } else if (pictureUri == null) {
            Toast.makeText(this, "Пожалуйста, добавьте постер фильма", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("1", binding.editTextTextPersonName.getText().toString());
            if (pictureUri != null) {
                intent.putExtra("2", pictureUri.toString());
            }
            startActivity(intent);
        }
    }


    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            binding.loadPic.setImageURI(data.getData());
            pictureUri = initPutFileOnStorage(data.getData());
        }
    }


    public static int getCameraPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {
            ExifInterface exif  = null;
            try {
                exif = new ExifInterface(imagePath);
            } catch (IOException e1) {
                System.out.println("Пизхдааааааааааааааааааааааааааааааа");
            }
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 90;
                    break;
                default:
                    rotate = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (riversRef != null) {
            if (!riversRef.getDownloadUrl().isSuccessful()) {
                binding.progressBar.setVisibility(View.VISIBLE);
            }
            new Thread(() -> {
                runOnUiThread(this::mmm);
            }).start();

        }
    }

    private void mmm() {
            riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Toast.makeText(this, "Успех", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.btnSend.setEnabled(true);
            }).addOnFailureListener(exception -> {
                mmm();
            });
    }

    private Uri initPutFileOnStorage(Uri URI) {
        if (URI != null) {
//            Uri file = Uri.fromFile(new File(URI));
            String email = mainPresenter.getEmail();
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            ;

            riversRef = mStorageRef.child(email + "/images/cinema" + mainPresenter.getCounter() + ".jpg");
            riversRef.putFile(URI);

            return Uri.parse(riversRef.getPath());
        } else {
            return null;
        }
    }

//    private void checkFileOnStorage(StorageReference riversRef) {
//        AtomicInteger k = new AtomicInteger(0);
//        while (!k.equals(new AtomicInteger(1))) {
//            riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                k.set(1);
//                Toast.makeText(this, "Успех" + k, Toast.LENGTH_SHORT).show();
//            }).addOnFailureListener(exception -> {
//                k.set(0);
//                Toast.makeText(this, "Провал" + k, Toast.LENGTH_SHORT).show();
//            });
//        }
//    }


    private static int getRotation(Context context, Uri imageUri, boolean isCamera) {
        int rotation;
        if (isCamera) {
            rotation = getRotationFromCamera(context, imageUri);
        } else {
            rotation = getRotationFromGallery(context, imageUri);
        }

        Toast.makeText(context, "Image rotation: " + rotation, Toast.LENGTH_SHORT).show();


        return rotation;
    }

    private static int getRotationFromCamera(Context context, Uri imageUri) {
        return 0;
    }

    public static int getRotationFromGallery(Context context, Uri imageUri) {
        int result = 0;
        String[] columns = {MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                result = cursor.getInt(orientationColumnIndex);
            }
        } catch (Exception e) {
            //Do nothing
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }//End of try-catch block
        return result;
    }


    private void actionPick() {
//        pictureUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", createImageFile());


//         tell the camera where to save the image.
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
//
//         tell the camera to request WRITE permission.
//        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


//
//        Intent intent = new Intent(
//                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        boolean outputFileUri = true;
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        startActivityForResult(intent, 1);

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }


}