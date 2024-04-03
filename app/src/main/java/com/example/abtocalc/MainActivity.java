package com.example.abtocalc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });}

        public void startNewActivity(View v){
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        }
    public void startCreate(View v) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }
    public void startCalc(View v){
        Intent intent = new Intent(this, CalcBezTS.class);
        startActivity(intent);
    }
    }