package com.example.abtocalc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalcBezTS extends AppCompatActivity {

    private TextView resToplivo;
    private EditText CreateCar, createProbeg, createPath, createNorma, createOstatok, createOstatok2;
    private Button ButtonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bezvvoda_calc);

        resToplivo= findViewById(R.id.resToplivo);
        CreateCar = findViewById(R.id.CreateCar);
        createProbeg = findViewById(R.id.createProbeg);
        createPath = findViewById(R.id.createPath);
        createNorma = findViewById(R.id.createNorma);
        createOstatok = findViewById(R.id.createOstatok);
        createOstatok2 = findViewById(R.id.createOstatok2);
        ButtonCreate = findViewById(R.id.ButtonCreate);

        ButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float num1 = Float.parseFloat(CreateCar.getText().toString());
                float num2 = Float.parseFloat(createProbeg.getText().toString());
                float Path = Float.parseFloat(createPath.getText().toString());
                float num4 = Float.parseFloat(createNorma.getText().toString());
                float num5 = Float.parseFloat(createOstatok.getText().toString());
                float resPath = num2 - num1;
                float res = Path * num4 / 100;
                float resTop = num5 - res;
                createPath.setText(String.valueOf(resPath));
                resToplivo.setText(String.valueOf(res));
                createOstatok2.setText(String.valueOf(resTop));
            }

        });
    }
    public void goBack (View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }}
