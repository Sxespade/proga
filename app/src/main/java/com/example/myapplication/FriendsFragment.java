package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.databinding.FragmentList2Binding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {

    FragmentList2Binding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String pathEmail;
    private List<String> listEmailAll;


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


    // Фабричный метод создания фрагмента
    // Фрагменты рекомендуется создавать через фабричные методы.
    public static FriendsFragment create(int index) {
        FriendsFragment f = new FriendsFragment();    // создание

        // Передача параметра
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

//    // Получить индекс из списка (фактически из параметра)
//    public int getIndex() {
//        int index = getArguments().getInt("index", 0);
//        return index;
//    }
//


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainPresenter.getInstance().getEmail() != null) {
            pathEmail = MainPresenter.getInstance().getEmail().replace(".", "dot");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentList2Binding.inflate(inflater, container, false);
        View view = binding.getRoot();


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Activity req = requireActivity();
        initACTV(req);


        initRecyclerView();
        return view;     // Вместо макета используем сразу картинку
    }

    private void initACTV(Activity req) {

        DatabaseReference myRef2 = database.getReference("UsersEmail");

        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                List<String> listEmail = (ArrayList<String>) dataSnapshot.getValue();
                List<String> list = new ArrayList<>();

                if (listEmail != null) {
                    for (int i = 0; i < listEmail.size(); i++) {
                        if (listEmail.get(i) != null) {
                            list.add(listEmail.get(i));
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(req,
                            android.R.layout.simple_list_item_1, list);
                    binding.actv.setAdapter(adapter);
                    listEmailAll = list;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        binding.btn.setOnClickListener((view -> {
            showFriends();
            binding.actv.clearFocus();
            binding.actv.setText("");
        }));

    }


    private void initRecyclerView() {
        // Эта установка служит для повышения производительности системы
        binding.recycleView.setHasFixedSize(true);

        // Будем работать со встроенным менеджером
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ViewGroup.LayoutParams lp = binding.recycleView.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.recycleView.requestLayout();
        binding.recycleView.setLayoutManager(layoutManager);


        initUpdateAdapter();


    }

    private void initUpdateAdapter() {
        if (MainPresenter.getInstance().getEmail() != null) {
            DatabaseReference usersEmail = database.getReference(MainPresenter.getInstance().getEmail().replace(".", "dot")).child("friends");

            usersEmail.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    List<String> list = (ArrayList<String>) dataSnapshot.getValue();
                    if (list != null) {
                        FriendsAdapter adapter = new FriendsAdapter(list);
                        binding.recycleView.setAdapter(adapter);
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

    private void showFriends() {

        String actvCheckMail = binding.actv.getText().toString();

        if (!actvCheckMail.equals(MainPresenter.getInstance().getEmail()) && !actvCheckMail.equals("")) {

            if (listEmailAll.contains(actvCheckMail)) {

                DatabaseReference usersEmail = database.getReference(MainPresenter.getInstance().getEmail().replace(".", "dot")).child("friends");

                usersEmail.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        List<String> list = (ArrayList<String>) dataSnapshot.getValue();

                        if (list != null && !list.contains(actvCheckMail)) {

                            list.add(actvCheckMail);

                            DatabaseReference myRef = database.getReference(pathEmail);
                            myRef.child("friends").setValue(list);

                            initUpdateAdapter();


                        } else if (list == null) {

                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!WOOO");

                            list = new ArrayList<>();
                            list.add(actvCheckMail);

                            FriendsAdapter adapter = new FriendsAdapter(list);
                            binding.recycleView.setAdapter(adapter);

                            DatabaseReference myRef = database.getReference(pathEmail);
                            myRef.child("friends").setValue(list);

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
    }
}

