package com.example.abtocalc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileContentActivity extends AppCompatActivity {

    private TextView textViewFileContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_content);

        textViewFileContent = findViewById(R.id.textViewFileContent);

        // Получаем имя файла из Intent
        Intent intent = getIntent();
        String fileName = intent.getStringExtra("fileName");

        if (fileName != null) {
            displayFileContent(fileName);
        }
    }

    private void displayFileContent(String fileName) {
        File file = new File(getFilesDir(), fileName);

        StringBuilder fileContent = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            textViewFileContent.setText("Ошибка при чтении файла.");
            return;
        }

        // Отображаем содержимое файла
        textViewFileContent.setText(fileContent.toString());
    }
    public void goBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}