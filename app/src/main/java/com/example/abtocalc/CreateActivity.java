package com.example.abtocalc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CreateActivity extends AppCompatActivity {

    private TextView logo;
    private EditText CreateCar, createProbeg, createNorma, createOstatok;
    private Button ButtonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        logo = findViewById(R.id.logo);
        CreateCar = findViewById(R.id.CreateCar);
        createProbeg = findViewById(R.id.createProbeg);
        createNorma = findViewById(R.id.createNorma);
        createOstatok = findViewById(R.id.createOstatok);
        ButtonCreate = findViewById(R.id.ButtonCreate);

        ButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float name1 = Float.parseFloat(CreateCar.getText().toString());
                float num1 = Float.parseFloat(createProbeg.getText().toString());
                float num2 = Float.parseFloat(createNorma.getText().toString());
                float num3 = Float.parseFloat(createOstatok.getText().toString());

            }

        });
    }
      public void goBack (View v){
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
}}
