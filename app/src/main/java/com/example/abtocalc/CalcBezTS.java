package com.example.abtocalc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalcBezTS extends AppCompatActivity {

    private TextView resToplivo, createOstatok2;
    private EditText Pobeg1, Pobeg2, createPath, createNorma, createOstatok;
    private Button ButtonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bezvvoda_calc);


        resToplivo = findViewById(R.id.resToplivo);
        Pobeg1 = findViewById(R.id.Pobeg1);
        Pobeg2 = findViewById(R.id.Pobeg2);
        createPath = findViewById(R.id.createPath);
        createNorma = findViewById(R.id.createNorma);
        createOstatok = findViewById(R.id.createOstatok);
        createOstatok2 = findViewById(R.id.createOstatok2);
        ButtonCreate = findViewById(R.id.ButtonCreate);

        ButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float num1 = 0;
                float num2 = 0;
                float Path = 0;
                float num4 = 0;
                float num5 = 0;


                try {
                    num1 = Float.parseFloat(Pobeg1.getText().toString());
                    num2 = Float.parseFloat(Pobeg2.getText().toString());
                    Path = Float.parseFloat(createPath.getText().toString());
                    num4 = Float.parseFloat(createNorma.getText().toString());
                    num5 = Float.parseFloat(createOstatok.getText().toString());

                } catch (NumberFormatException exception) {

                       if (!TextUtils.isEmpty(Pobeg1.getText().toString()) &&
                               !TextUtils.isEmpty(Pobeg2.getText().toString()))
                     {
                         num1 = Float.parseFloat(Pobeg1.getText().toString());
                         num2 = Float.parseFloat(Pobeg2.getText().toString());
                         num4 = Float.parseFloat(createNorma.getText().toString());
                         num5 = Float.parseFloat(createOstatok.getText().toString());

                          Path = num2 - num1;

                         float res = Path * num4 / 100;
                         float resTop = num5 - res;
                         createPath.setText(String.valueOf(Path));
                         createOstatok2.setText(String.valueOf(resTop));
                         resToplivo.setText(String.valueOf(res));

                } else {
                    if (!TextUtils.isEmpty(createPath.getText().toString()) &&
                            !TextUtils.isEmpty(createNorma.getText().toString()) &&
                            !TextUtils.isEmpty(createOstatok.getText().toString())) {

                        Path = Float.parseFloat(createPath.getText().toString());
                        num4 = Float.parseFloat(createNorma.getText().toString());
                        num5 = Float.parseFloat(createOstatok.getText().toString());
                        float res = Path * num4 / 100;
                        float resTop = num5 - res;
                        resToplivo.setText(String.valueOf(res));
                        createOstatok2.setText(String.valueOf(resTop));
                    } else {

                        resToplivo.setText("Заполните поля");
                    }
                    }
                }
            }

        });
    }
    public void onBack(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}