package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.databinding.ActivityMainBinding;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.button.setOnClickListener((v) -> {
            Intent intent = new Intent(this, MainActivity2.class);
            startActivity(intent);
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String text = getIntent().getExtras().getString("1");
            binding.textView1.setText(text);
            String URI = getIntent().getExtras().getString("2");
            System.out.println("0000000000000000000000000000000" + URI);
            binding.imageView1.setImageURI(Uri.parse(URI));
        }

//        Button backToFirstActivity = findViewById(R.id.buttonBack);
//        backToFirstActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

    }
}