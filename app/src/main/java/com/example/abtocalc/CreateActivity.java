package com.example.abtocalc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateActivity extends AppCompatActivity {

    private EditText createCar, createProbeg, createNorma, createOstatok;
    private Button buttonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        createCar = findViewById(R.id.CreateCar);
        createProbeg = findViewById(R.id.createProbeg);
        createNorma = findViewById(R.id.createNorma);
        createOstatok = findViewById(R.id.createOstatok);
        buttonCreate = findViewById(R.id.ButtonCreate);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем данные из полей ввода
                String carName = createCar.getText().toString();
                String probeg = createProbeg.getText().toString();
                String norma = createNorma.getText().toString();
                String ostatok = createOstatok.getText().toString();

                // Проверка на пустые поля
                if (carName.isEmpty() || probeg.isEmpty() || norma.isEmpty() || ostatok.isEmpty()) {
                    Toast.makeText(CreateActivity.this, "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Проверка существования файла
                File file = new File(getFilesDir(), carName + ".txt");
                if (file.exists()) {
                    Toast.makeText(CreateActivity.this, "Файл с таким названием уже существует. Изменение невозможно.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Форматируем данные для записи
                String dataToWrite = "Гос №:" + carName + "\nПробег: " + probeg + "\nНорма: " + norma + "\nОстаток: " + ostatok + "\n";

                // Записываем данные в файл
                try (FileOutputStream fos = openFileOutput(carName + ".txt", MODE_PRIVATE)) {
                    fos.write(dataToWrite.getBytes());
                    Toast.makeText(CreateActivity.this, "Данные успешно записаны в файл: " + carName + ".txt", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateActivity.this, "Ошибка при записи в файл.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void goBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
