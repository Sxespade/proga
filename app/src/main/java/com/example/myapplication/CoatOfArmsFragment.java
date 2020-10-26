package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentListBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CoatOfArmsFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN = 40404;
    private FragmentListBinding binding;
    private GoogleSignInClient googleSignInClient;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private StorageReference riversRef;
    MainPresenter mainPresenter = MainPresenter.getInstance();
    GoogleSignInAccount account2;
    private String URI;
    private String text;
    public static final String GSTORAGE = "gs://horrors-8ac6c.appspot.com/";

    @IgnoreExtraProperties
    static class Item implements Serializable {
        public String name;
        public String Uri;

        public Item() {
        }

        public Item(String name, String Uri) {
            this.name = name;
            this.Uri = Uri;
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public static CoatOfArmsFragment create(int index) {
        CoatOfArmsFragment f = new CoatOfArmsFragment();    // создание

        // Передача параметра
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child(GSTORAGE);


        binding.img.setOnClickListener(this);
        binding.img2.setOnClickListener(this);
        binding.img3.setOnClickListener(this);
        binding.img4.setOnClickListener(this);
        binding.img5.setOnClickListener(this);
        binding.img6.setOnClickListener(this);
        binding.linear.setOnClickListener(this);
        binding.horizontalScrollView2.setOnClickListener(this);
        binding.main.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initGoogleAuth();
        checkUserLoggedIn();
        initBundleParcel();
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void initGetFile(String nameOfPicture, ImageView imageView) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference riversRef = storage.getReferenceFromUrl(GSTORAGE + MainPresenter.getInstance().getEmail() + "/images/").child(nameOfPicture);
//        riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
//            // Got the download URL for 'users/me/profile.png'
//        }).addOnFailureListener(exception -> {
//            Toast.makeText(getActivity(),"", Toast.LENGTH_SHORT).show();
//        });

//        ifriversRef.getDownloadUrl().isSuccessful()



        try {
            final File localFile = File.createTempFile("images", "jpg");
            riversRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                if (bitmap != null) {
                    try {
                        bitmap = getBitmap(localFile, bitmap);
                    }catch(IOException ex){
                        Log.e("LOG EXIF", "Failed to get Exif data", ex);
                    }

//                    binding.img.setImageBitmap(bitmap);
//                    showPhoto(GSTORAGE + MainPresenter.getInstance().getEmail() + "/images/" + nameOfPicture);
//                    FromCamera(path);
                    imageView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        } catch (IOException e) {
        }
    }


//    private Bitmap getBitmap(File localFile, Bitmap bitmap) {
//        int origWidth = bitmap.getWidth();
//        int origHeight = bitmap.getHeight();
//        int orientation = get
//
//        if (orientation == 90 || orientation == 270) {
//            origWidth = o.outHeight;
//            origHeight = o.outWidth;
//        } else {
//            origWidth = o.outWidth;
//            origHeight = o.outHeight;
//        }
//
//        if (orientation > 0) {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(orientation);
//            Bitmap decodedBitmap = bitmap;
//            bitmap = Bitmap.createBitmap(decodedBitmap, 0, 0, bitmap.getWidth(),
//                    bitmap.getHeight(), matrix, true);
//            //рецайклим оригинальный битмап за ненадобностью
//            if (decodedBitmap != null && !decodedBitmap.equals(bitmap)) {
//                decodedBitmap.recycle();
//            }
//        }
//
//    }

    private Bitmap getBitmap(File localFile, Bitmap bitmap) throws IOException {
        ExifInterface exif = new ExifInterface(localFile.getAbsoluteFile());
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationInDegrees = exifToDegrees(rotation);
        Matrix matrix = new Matrix();
        if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
        bitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    Bitmap bitmap;
    File file;


    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }


//    void showPhoto(String url){
//
//        file = new File(url);
//        try {
//            ExifInterface exif = new ExifInterface(file.getPath());
//            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int rotationInDegrees = exifToDegrees(rotation);
//            Matrix matrix = new Matrix();
//            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
//            bitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//
//        }catch(IOException ex){
//            Log.e("LOG EXIF", "Failed to get Exif data", ex);
//        }
//
//        binding.img.setImageBitmap(bitmap);
//    }






//    public void FromCamera(String path) {
//
//        Log.i("camera", "startCameraActivity()");
//        File file = new File(path);
//        Uri outputFileUri = Uri.fromFile(file);
//        Intent intent = new Intent(
//                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        startActivityForResult(intent, 1);
//
//    }





    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        initDrawRecommend();
        initDrawRecommendToFriends();
    }



    private void initDrawRecommendToFriends() {
        if (mainPresenter.getEmail() != null) {
            DatabaseReference usersEmail = database.getReference(mainPresenter.getEmail().replace(".", "dot")).child("friends");

            usersEmail.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    List<String> list = (ArrayList<String>) dataSnapshot.getValue();
                    if (list != null) {
                        for (String s : list) {
                            if (s != null) {
                                if (!s.equals("email")) {


                                    String filmRef1 = s.replace(".", "dot") + "/rec_films/film1";
                                    String filmRef2 = s.replace(".", "dot") + "/rec_films/film2";
                                    String filmRef3 = s.replace(".", "dot") + "/rec_films/film3";

                                    initTakeInfo2(filmRef1, binding.img4, "cinema1.jpg", binding.textView4, s);
                                    initTakeInfo2(filmRef2, binding.img5, "cinema2.jpg", binding.textView5, s);
                                    initTakeInfo2(filmRef3, binding.img6, "cinema3.jpg", binding.textView6, s);
                                }
                            }
                        }
                    }
                    Log.d("TAG1", "Value is: ");
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TAG1", "Failed to read value.", error.toException());
                }
            });
        }
    }


    private void initGetFile2(String nameOfPicture, ImageView imageView, String mail) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference riversRef = storage.getReferenceFromUrl(GSTORAGE + mail + "/images/").child(nameOfPicture);

        try {
            final File localFile = File.createTempFile("images", "jpg");
            riversRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                if (bitmap != null) {
//                    imageView.setImageBitmap(bitmap);
                    try {
                        bitmap = getBitmap(localFile, bitmap);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e) {
        }
    }


    private void initTakeInfo2(String ref, ImageView imageView, String cinemaCount, TextView textView, String mail) {

        DatabaseReference myRef = database.getReference(ref);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Item value = dataSnapshot.getValue(Item.class);
                if (value != null && value.Uri != null) {
                    initGetFile2(cinemaCount, imageView, mail);
                    textView.setText(value.name);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG3", "Failed to read value.", error.toException());
            }
        });
    }

    private void initTakeInfo(String ref, ImageView imageView, String cinemaCount, TextView textView) {

        Log.d("TAG5", mainPresenter.getEmail() + "!!!");

        if (mainPresenter.getEmail() != null) {

            DatabaseReference myRef = database.getReference(ref);

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Item value = dataSnapshot.getValue(Item.class);
                    Log.d("TAG5", value + "!!!");
                    if (value != null && value.name != null) {
                        textView.setText(value.name);
                        Log.d("TAG5", value.name + "!!!");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TAG5", "Failed to read value.", error.toException());
                }
            });

            initGetFile(cinemaCount, imageView);
        }
    }


    private void initDrawRecommend() {
        if (MainPresenter.getInstance().getEmail() != null) {
            if (!MainPresenter.getInstance().getEmail().equals("email")) {

                String filmRef1 = MainPresenter.getInstance().getEmail().replace(".", "dot") + "/rec_films/film1";
                String filmRef2 = MainPresenter.getInstance().getEmail().replace(".", "dot") + "/rec_films/film2";
                String filmRef3 = MainPresenter.getInstance().getEmail().replace(".", "dot") + "/rec_films/film3";

                initTakeInfo(filmRef1, binding.img, "cinema1.jpg", binding.textView1);
                initTakeInfo(filmRef2, binding.img2, "cinema2.jpg", binding.textView2);
                initTakeInfo(filmRef3, binding.img3, "cinema3.jpg", binding.textView3);

            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == binding.img || v == binding.img2 || v == binding.img3) {
            Toast.makeText(requireActivity(), v.toString(), Toast.LENGTH_SHORT).show();
            initBtnClick(v);
        } else {
            Toast.makeText(requireActivity(), v.toString(), Toast.LENGTH_SHORT).show();
            clearImg();
        }
    }


    private void clearImg() {
        binding.img.setImageAlpha(255);
        binding.t1.setVisibility(View.GONE);

        binding.img2.setImageAlpha(255);
        binding.t2.setVisibility(View.GONE);

        binding.img3.setImageAlpha(255);
        binding.t3.setVisibility(View.GONE);
    }


    private void initBtnClick(View view1) {

        switch (view1.getId()) {
            case R.id.img:
                if (binding.t1.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(getActivity(), MainActivity2.class);
                    startActivity(intent);
                    clearImg();
                    MainPresenter.getInstance().setCounter("1");
                    initPutFileOnStorage();
                } else {
                    binding.img.setImageAlpha(130);
                    binding.t1.setVisibility(View.VISIBLE);

                    binding.img2.setImageAlpha(255);
                    binding.t2.setVisibility(View.GONE);

                    binding.img3.setImageAlpha(255);
                    binding.t3.setVisibility(View.GONE);
                }
                break;

            case R.id.img2:
                if (binding.t2.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(getActivity(), MainActivity2.class);
                    startActivity(intent);
                    clearImg();
                    MainPresenter.getInstance().setCounter("2");
                    initPutFileOnStorage();
                } else {
                    binding.img.setImageAlpha(255);
                    binding.t1.setVisibility(View.GONE);

                    binding.img2.setImageAlpha(130);
                    binding.t2.setVisibility(View.VISIBLE);

                    binding.img3.setImageAlpha(255);
                    binding.t3.setVisibility(View.GONE);
                }
                break;


            case R.id.img3:
                if (binding.t3.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(getActivity(), MainActivity2.class);
                    startActivity(intent);
                    clearImg();
                    MainPresenter.getInstance().setCounter("3");
                    initPutFileOnStorage();
                } else {
                    binding.img.setImageAlpha(255);
                    binding.t1.setVisibility(View.GONE);

                    binding.img2.setImageAlpha(255);
                    binding.t2.setVisibility(View.GONE);

                    binding.img3.setImageAlpha(130);
                    binding.t3.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    private void initPutFileOnStorage() {
        if (URI != null) {
//            Uri file = Uri.fromFile(new File(URI));
            String email = MainPresenter.getInstance().getEmail();
            String s = email + "/images/cinema" + MainPresenter.getInstance().getCounter() + ".jpg";
            riversRef = mStorageRef.child(s);
            riversRef.putFile(Uri.parse(URI));
        }
    }

    private void initGoogleAuth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Получаем клиента для регистрации и данные по клиенту
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Кнопка регистрации пользователя
        binding.sign.setOnClickListener(v -> {
            signIn();
            onStart();
        });

        binding.singOutButton.setOnClickListener(v -> signOut());

    }


    private void checkUserLoggedIn() {
        enableSign();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());
        account2 = GoogleSignIn.getLastSignedInAccount(requireActivity());

        if (account != null) {

            // Пользователь уже входил, сделаем кнопку недоступной
            MainPresenter.getInstance().setEmail(account.getEmail());
            disableSign();
            binding.sign.setEnabled(false);
            // Обновим почтовый адрес этого пользователя и выведем его на экран
            updateUI(account.getEmail());

            initDatabaseTakeEmail(account);

        }
    }

    private void initDatabaseTakeEmail(GoogleSignInAccount account) {
//        List<String> list = new ArrayList<>();
//        if (!list.contains(account.getEmail())) {
//            list.add(account.getEmail());
//        }

        database = FirebaseDatabase.getInstance();
        DatabaseReference usersEmail = database.getReference("UsersEmail");


        usersEmail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                List<String> list = new ArrayList<>();
                list = (ArrayList<String>) dataSnapshot.getValue();
                if (list == null) {
                    list = new ArrayList();
                    list.add(account.getEmail());
                } else if (!list.contains(account.getEmail())) {
                    list.add(account.getEmail());
                }
                Log.d("TAG1", "Value is: ");

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("UsersEmail");
                myRef.setValue(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Когда сюда возвращается Task, результаты аутентификации уже
            // готовы
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            MainPresenter.getInstance().setEmail(account.getEmail());
            // Регистрация прошла успешно
            disableSign();
            updateUI(account.getEmail());
            initDatabaseTakeEmail(account);
            onStart();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure
            // reason. Please refer to the GoogleSignInStatusCodes class
            // reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }

    }


    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        disableSign();
    }

    private void checkEmailNotEmail() {
        Toast.makeText(getActivity(), "22222" + mainPresenter.getEmail(), Toast.LENGTH_SHORT).show();

        String filmRef1 = mainPresenter.getEmail().replace(".", "dot") + "/rec_films/film1";
        String filmRef2 = mainPresenter.getEmail().replace(".", "dot") + "/rec_films/film2";
        String filmRef3 = mainPresenter.getEmail().replace(".", "dot") + "/rec_films/film3";


        initTakeInfo(filmRef1, binding.img, "cinema1.jpg", binding.textView1);
        initTakeInfo(filmRef2, binding.img2, "cinema2.jpg", binding.textView2);
        initTakeInfo(filmRef3, binding.img3, "cinema3.jpg", binding.textView3);
    }


    private void updateUI(String email) {
        binding.token.setText(email);
    }

    //
    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(requireActivity(), task -> {
                    updateUI("email");
                    MainPresenter.getInstance().setEmail("email");
                    enableSign();
                });
        allPictureEmpty();
    }

    //
    private void allPictureEmpty() {
        binding.img.setImageResource(R.drawable.aaa);
        binding.img2.setImageResource(R.drawable.aaa);
        binding.img3.setImageResource(R.drawable.aaa);
        binding.textView1.setText("Пусто");
        binding.textView2.setText("Пусто");
        binding.textView3.setText("Пусто");
    }

    //
    private void enableSign() {
        binding.sign.setEnabled(true);
        binding.singOutButton.setEnabled(false);
    }

    private void disableSign() {
        binding.sign.setEnabled(false);
        binding.singOutButton.setEnabled(true);
    }


    private void initBundleParcel() {
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null && MainPresenter.getInstance().getCounter().equals("1")) {

            if (mainPresenter.getUri2() != null) {
                binding.img2.setImageURI(Uri.parse(mainPresenter.getUri2()));
            }
            if (mainPresenter.getName2() != null) {
                binding.textView2.setText(mainPresenter.getName2());
            }
            if (mainPresenter.getUri3() != null) {
                binding.img3.setImageURI(Uri.parse(mainPresenter.getUri3()));
            }
            if (mainPresenter.getName3() != null) {
                binding.textView3.setText(mainPresenter.getName3());
            }

            String text = getActivity().getIntent().getExtras().getString("1");

            binding.textView1.setText(text);
            String URI = getActivity().getIntent().getExtras().getString("2");
            mainPresenter.setUri1(URI);
            mainPresenter.setName1(text);
            System.out.println("0000000000000000000000000000000" + URI);
            if (URI != null) {
                binding.img.setImageURI(Uri.parse(URI));
            }

            Item item = new Item(text, URI);

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference(mainPresenter.getEmail().replace(".", "dot")).child("rec_films").child("film1");

            if (item.name != null) {
            if (!item.name.equals("") && item.Uri != null) {
                myRef.setValue(item);
            } }

            this.text = text;
            this.URI = URI;


        } else if (extras != null && mainPresenter.getCounter().equals("2")) {

            if (mainPresenter.getUri1() != null) {
                binding.img.setImageURI(Uri.parse(mainPresenter.getUri1()));
            }
            if (mainPresenter.getName1() != null) {
                binding.textView1.setText(mainPresenter.getName1());
            }
            if (mainPresenter.getUri3() != null) {
                binding.img3.setImageURI(Uri.parse(mainPresenter.getUri3()));
            }
            if (mainPresenter.getName3() != null) {
                binding.textView3.setText(mainPresenter.getName3());
            }

            String text = getActivity().getIntent().getExtras().getString("1");
            binding.textView2.setText(text);
            String URI = getActivity().getIntent().getExtras().getString("2");
            mainPresenter.setUri2(URI);
            mainPresenter.setName2(text);
            System.out.println("0000000000000000000000000000000" + URI);
            if (URI != null) {
                binding.img2.setImageURI(Uri.parse(URI));
            }

            Item item = new Item(text, URI);

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference(mainPresenter.getEmail().replace(".", "dot")).child("rec_films").child("film2");

            if (!item.name.equals("") && item.Uri != null) {
                myRef.setValue(item);
            }

            this.text = text;
            this.URI = URI;

        } else if (extras != null && mainPresenter.getCounter().equals("3")) {

            if (mainPresenter.getUri1() != null) {
                binding.img.setImageURI(Uri.parse(mainPresenter.getUri1()));
            }
            if (mainPresenter.getName1() != null) {
                binding.textView1.setText(mainPresenter.getName1());
            }
            if (mainPresenter.getUri2() != null) {
                binding.img2.setImageURI(Uri.parse(mainPresenter.getUri2()));
            }
            if (mainPresenter.getName2() != null) {
                binding.textView2.setText(mainPresenter.getName2());
            }

            String text = getActivity().getIntent().getExtras().getString("1");
            binding.textView3.setText(text);
            String URI = getActivity().getIntent().getExtras().getString("2");
            mainPresenter.setUri3(URI);
            mainPresenter.setName3(text);
            System.out.println("0000000000000000000000000000000" + URI);
            if (URI != null) {
                binding.img3.setImageURI(Uri.parse(URI));
            }

            Item item = new Item(text, URI);

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference(mainPresenter.getEmail().replace(".", "dot")).child("rec_films").child("film3");

            if (!item.name.equals("") && item.Uri != null) {
                myRef.setValue(item);
            }
            this.text = text;
            this.URI = URI;
        }
    }


}
