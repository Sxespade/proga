package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String GSTORAGE = "gs://horrors-8ac6c.appspot.com/";
    public static final String IMAGEDIR = "/images/";
    private ActivityMainBinding binding;
    MainPresenter mainPresenter = MainPresenter.getInstance();
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private int imageCounter;
    private int counterT = 0;
    StorageReference riversRef;

    private static final int RC_SIGN_IN = 40404;
    private static final String TAG = "GoogleAuth";
    private GoogleSignInClient googleSignInClient;
    private com.google.android.gms.common.SignInButton buttonSignIn;

    private String text;
    private String URI;

    FirebaseStorage storage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        binding.mTextMessage.setText(R.string.title_home);
                        return true;
                    case R.id.navigation_dashboard:
                        binding.mTextMessage.setText(R.string.title_dashboard);
                        Intent intent = new Intent(this, MainActivity2.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_notifications:
                        binding.mTextMessage.setText(R.string.title_notifications);
                        return true;
                }
                return false;
            };



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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("items");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        myRef.setValue("Hello, World!");
        mStorageRef = FirebaseStorage.getInstance().getReference();


        initGoogleAuth();

        initTokenForCloudMsg();

        initNotificationChannel();

        initBundleParcel();




        binding.img.setOnClickListener(this);
        binding.img2.setOnClickListener(this);
        binding.img3.setOnClickListener(this);
        binding.imageView4.setOnClickListener(this);
        binding.imageView6.setOnClickListener(this);
        binding.linear.setOnClickListener(this);
        binding.horizontalScrollView2.setOnClickListener(this);
        binding.main.setOnClickListener(this);


        binding.navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.img || v == binding.img2 || v == binding.img3) {
            Toast.makeText(this, v.toString(), Toast.LENGTH_SHORT).show();
            initBtnClick(v);
        } else {
            Toast.makeText(this, v.toString(), Toast.LENGTH_SHORT).show();
            clearImg();
    }
    }


    @Override
    protected void onStart() {
        super.onStart();

        checkUserLoggedIn();

        initGetFile("cinema1.jpg", binding.img);
        initGetFile("cinema2.jpg", binding.img2);
        initGetFile("cinema3.jpg", binding.img3);

    }

    private void checkUserLoggedIn() {
        enableSign();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // Пользователь уже входил, сделаем кнопку недоступной
            MainPresenter.getInstance().setEmail(account.getEmail());
            disableSign();
            binding.sign.setEnabled(false);
            // Обновим почтовый адрес этого пользователя и выведем его на экран
            updateUI(account.getEmail());
        }
    }

    private void initBundleParcel() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && mainPresenter.getCounter().equals("1")) {

            if (mainPresenter.getUri2() != null) {
                binding.img.setImageURI(Uri.parse(mainPresenter.getUri2()));
            }
            if (mainPresenter.getName2() != null) {
                binding.textView1.setText(mainPresenter.getName2());
            }
            if (mainPresenter.getUri3() != null) {
                binding.img.setImageURI(Uri.parse(mainPresenter.getUri3()));
            }
            if (mainPresenter.getName3() != null) {
                binding.textView1.setText(mainPresenter.getName3());
            }

            String text = getIntent().getExtras().getString("1");
            binding.textView1.setText(text);
            String URI = getIntent().getExtras().getString("2");
            mainPresenter.setUri1(URI);
            mainPresenter.setName1(text);
            System.out.println("0000000000000000000000000000000" + URI);
            binding.img.setImageURI(Uri.parse(URI));

            Item item = new Item(text, URI);

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("items");
            myRef.push().setValue(item);

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
                binding.img.setImageURI(Uri.parse(mainPresenter.getUri3()));
            }
            if (mainPresenter.getName3() != null) {
                binding.textView1.setText(mainPresenter.getName3());
            }

            String text = getIntent().getExtras().getString("1");
            binding.textView2.setText(text);
            String URI = getIntent().getExtras().getString("2");
            mainPresenter.setUri2(URI);
            mainPresenter.setName2(text);
            System.out.println("0000000000000000000000000000000" + URI);
            binding.img2.setImageURI(Uri.parse(URI));

            Item item = new Item(text, URI);

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("items");
            myRef.push().setValue(item);

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
                binding.img.setImageURI(Uri.parse(mainPresenter.getUri2()));
            }
            if (mainPresenter.getName2() != null) {
                binding.textView1.setText(mainPresenter.getName2());
            }

            String text = getIntent().getExtras().getString("1");
            binding.textView3.setText(text);
            String URI = getIntent().getExtras().getString("2");
            mainPresenter.setUri3(URI);
            mainPresenter.setName3(text);
            System.out.println("0000000000000000000000000000000" + URI);
            binding.img3.setImageURI(Uri.parse(URI));

            Item item = new Item(text, URI);

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("items");
            myRef.push().setValue(item);

            this.text = text;
            this.URI = URI;
        }
    }

    private void initBtnClick(View view1) {

        switch (view1.getId()) {
            case R.id.img:   if (binding.t1.getVisibility() == View.VISIBLE) {
                Intent intent = new Intent(this, MainActivity2.class);
                startActivity(intent);
                clearImg();
                mainPresenter.setCounter("1");
                initPutFileOnStorage();
            } else {
                binding.img.setImageAlpha(130);
                binding.t1.setVisibility(View.VISIBLE);

                binding.img2.setImageAlpha(255);
                binding.t2.setVisibility(View.GONE);

                binding.img3.setImageAlpha(255);
                binding.t3.setVisibility(View.GONE);
            } break;

            case R.id.img2:   if (binding.t2.getVisibility() == View.VISIBLE) {
                Intent intent = new Intent(this, MainActivity2.class);
                startActivity(intent);
                clearImg();
                mainPresenter.setCounter("2");
                initPutFileOnStorage();
            } else {
                binding.img.setImageAlpha(255);
                binding.t1.setVisibility(View.GONE);

                binding.img2.setImageAlpha(130);
                binding.t2.setVisibility(View.VISIBLE);

                binding.img3.setImageAlpha(255);
                binding.t3.setVisibility(View.GONE);
            } break;


            case R.id.img3:   if (binding.t3.getVisibility() == View.VISIBLE) {
                Intent intent = new Intent(this, MainActivity2.class);
                startActivity(intent);
                clearImg();
                mainPresenter.setCounter("3");
                initPutFileOnStorage();
            } else {
                binding.img.setImageAlpha(255);
                binding.t1.setVisibility(View.GONE);

                binding.img2.setImageAlpha(255);
                binding.t2.setVisibility(View.GONE);

                binding.img3.setImageAlpha(130);
                binding.t3.setVisibility(View.VISIBLE);
            } break;


        }

//        binding.img.setOnClickListener((view1 -> {
//            if (binding.t1.getVisibility() == View.VISIBLE) {
//                Intent intent = new Intent(this, MainActivity2.class);
//                startActivity(intent);
//                mainPresenter.setCounter("1");
//                initPutFileOnStorage();
//            } else {
//                binding.img.setImageAlpha(130);
//                binding.t1.setVisibility(View.VISIBLE);
//
//                binding.img2.setImageAlpha(255);
//                binding.t2.setVisibility(View.GONE);
//
//                binding.img3.setImageAlpha(255);
//                binding.t3.setVisibility(View.GONE);
//
//                counterT = 1;
//            }
//        }));
//
//        binding.img2.setOnClickListener((view1 -> {
//            if (binding.t2.getVisibility() == View.VISIBLE) {
//                Intent intent = new Intent(this, MainActivity2.class);
//                startActivity(intent);
//                mainPresenter.setCounter("2");
//                initPutFileOnStorage();
//            } else {
//                binding.img2.setImageAlpha(130);
//                binding.t2.setVisibility(View.VISIBLE);
//
//                binding.img.setImageAlpha(255);
//                binding.t1.setVisibility(View.GONE);
//
//                binding.img3.setImageAlpha(255);
//                binding.t3.setVisibility(View.GONE);
//
//                counterT = 1;
//            }
//        }));
//
//        binding.img3.setOnClickListener((view1 -> {
//            if (binding.t3.getVisibility() == View.VISIBLE) {
//                Intent intent = new Intent(this, MainActivity2.class);
//                startActivity(intent);
//                mainPresenter.setCounter("3");
//                initPutFileOnStorage();
//            } else {
//                binding.img3.setImageAlpha(130);
//                binding.t3.setVisibility(View.VISIBLE);
//
//                binding.img.setImageAlpha(255);
//                binding.t1.setVisibility(View.GONE);
//
//                binding.img2.setImageAlpha(255);
//                binding.t2.setVisibility(View.GONE);
//
//                counterT = 1;
//            }
//        }));


    }

    private void clearImg() {
        binding.img.setImageAlpha(255);
        binding.t1.setVisibility(View.GONE);

        binding.img2.setImageAlpha(255);
        binding.t2.setVisibility(View.GONE);

        binding.img3.setImageAlpha(255);
        binding.t3.setVisibility(View.GONE);
    }


    private void initPutFileOnStorage() {
        if (URI != null) {
//            Uri file = Uri.fromFile(new File(URI));
            String email = mainPresenter.getEmail();
            riversRef = mStorageRef.child(email + "/images/cinema" + mainPresenter.getCounter() + ".jpg");
            riversRef.putFile(Uri.parse(URI));
        }
    }

    private void initGetFile(String nameOfPicture, ImageView imageView) {

        StorageReference riversRef = storage.getReferenceFromUrl(GSTORAGE + MainPresenter.getInstance().getEmail() + "/images/").child(nameOfPicture);

        try {
            final File localFile = File.createTempFile("images", "jpg");
            riversRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
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

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void initTokenForCloudMsg() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);
                        MainPresenter.getInstance().setToken(token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initGoogleAuth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Получаем клиента для регистрации и данные по клиенту
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Кнопка регистрации пользователя
        binding.sign.setOnClickListener(v -> signIn()
        );

        binding.singOutButton.setOnClickListener(v -> signOut());

    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
            onStart();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure
            // reason. Please refer to the GoogleSignInStatusCodes class
            // reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }

    }


    private void updateUI(String email) {
        binding.token.setText(email);
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    updateUI("email");
                    enableSign();
                });
        allPictureEmpty();
    }

    private void allPictureEmpty() {
        binding.img.setImageResource(R.drawable.aaa);
        binding.img2.setImageResource(R.drawable.aaa);
        binding.img3.setImageResource(R.drawable.aaa);
    }

    private void enableSign() {
        binding.sign.setEnabled(true);
        binding.singOutButton.setEnabled(false);
    }

    private void disableSign() {
        binding.sign.setEnabled(false);
        binding.singOutButton.setEnabled(true);
    }


}