package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentList3Binding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final int RC_SIGN_IN = 40404;
    FragmentList3Binding binding;
    GoogleSignInClient googleSignInClient;

    public static ProfileFragment create(int index) {
        ProfileFragment f = new ProfileFragment();    // создание

        // Передача параметра
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentList3Binding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.mail.setText(MainPresenter.getInstance().getEmail());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initGoogleAuth();
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
        });

        binding.singOutButton.setOnClickListener(v -> signOut());
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
//            initDatabaseTakeEmail(account);
            onStart();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure
            // reason. Please refer to the GoogleSignInStatusCodes class
            // reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }

    }


//    private void initDatabaseTakeEmail(GoogleSignInAccount account) {
////        List<String> list = new ArrayList<>();
////        if (!list.contains(account.getEmail())) {
////            list.add(account.getEmail());
////        }
//
//        database = FirebaseDatabase.getInstance();
//        DatabaseReference usersEmail = database.getReference("UsersEmail");
//
//
//        usersEmail.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                List<String> list = new ArrayList<>();
//                list = (ArrayList<String>) dataSnapshot.getValue();
//                if (list == null) {
//                    list = new ArrayList();
//                    list.add(account.getEmail());
//                } else if (!list.contains(account.getEmail())) {
//                    list.add(account.getEmail());
//                }
//                Log.d("TAG1", "Value is: ");
//
//                database = FirebaseDatabase.getInstance();
//                myRef = database.getReference("UsersEmail");
//                myRef.setValue(list);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w("TAG1", "Failed to read value.", error.toException());
//            }
//        });
//    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        disableSign();
    }


    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(requireActivity(), task -> {
                    updateUI("email");
                    MainPresenter.getInstance().setEmail("email");
                    enableSign();
                });
//        allPictureEmpty();
    }

//    private void allPictureEmpty() {
//        binding2.img.setImageResource(R.drawable.aaa);
//        binding2.img2.setImageResource(R.drawable.aaa);
//        binding2.img3.setImageResource(R.drawable.aaa);
//        binding2.textView1.setText("Пусто");
//        binding2.textView2.setText("Пусто");
//        binding2.textView3.setText("Пусто");
//    }


    private void updateUI(String email) {
        binding.mail.setText(email);
    }

    private void disableSign() {
        binding.sign.setEnabled(false);
        binding.singOutButton.setEnabled(true);
    }

    private void enableSign() {
        binding.sign.setEnabled(true);
        binding.singOutButton.setEnabled(false);
    }

}
