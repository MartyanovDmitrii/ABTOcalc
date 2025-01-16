package com.example.abtocalc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import java.text.DecimalFormat;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileContentActivity extends AppCompatActivity {

    private EditText editTextCarName; // Поле для названия автомобиля
    private EditText editTextProbeg;   // Поле для пробега
    private EditText editTextNorma;    // Поле для нормы топлива
    private EditText editTextOstatok;  // Поле для остатка топлива
    private TextView textViewFileContent; // Поле для отображения содержимого файла
    private TextView textViewResult;    // Поле для отображения результата
    private EditText editTextPath;      // Поле для пробега
    private EditText editTextZapr;      // Поле для заправленного топлива
    private TextView textViewPathEnd;   // Поле для пробега в конце

    private float calculatedResult;      // Переменная для хранения результата
    private float calculatedPathEnd;      // Переменная для хранения пробега в конце

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_content);

        // Инициализация полей ввода
        editTextCarName = findViewById(R.id.editTextCarName);
        editTextProbeg = findViewById(R.id.editTextProbeg);
        editTextNorma = findViewById(R.id.editTextNorma);
        editTextOstatok = findViewById(R.id.editTextOstatok);
        textViewFileContent = findViewById(R.id.textViewFileContent);
        textViewResult = findViewById(R.id.textViewResult);
        editTextPath = findViewById(R.id.editTextPath);
        editTextZapr = findViewById(R.id.editTextZapr);
        textViewPathEnd = findViewById(R.id.textViewPathEnd);

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

        // Разбиваем содержимое файла на строки и заполняем EditText
        String[] data = fileContent.toString().split("\n");
        if (data.length >= 4) { // Убедитесь, что у вас достаточно строк
            editTextCarName.setText(data[0].replace("Название ТС: ", "").trim());
            editTextProbeg.setText(data[1].replace("Пробег: ", "").trim());
            editTextNorma.setText(data[2].replace("Норма: ", "").trim());
            editTextOstatok.setText(data[3].replace("Остаток: ", "").trim());
        }
    }

    public void calculateAndDisplayResult(View v) {
        String pathStr = editTextPath.getText().toString();
        String normaStr = editTextNorma.getText().toString();
        String zaprStr = editTextZapr.getText().toString();
        String probegStr = editTextProbeg.getText().toString();
        String ostatokStr = editTextOstatok.getText().toString();
        String carName = editTextCarName.getText().toString(); // Получаем название машины

        // Проверка на пустые строки
        if (pathStr.isEmpty() || normaStr.isEmpty() || zaprStr.isEmpty() || probegStr.isEmpty() || ostatokStr.isEmpty() || carName.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Преобразуем строки в числа
            float path = Float.parseFloat(pathStr);
            float norma = Float.parseFloat(normaStr);
            float zapr = Float.parseFloat(zaprStr);
            float ostatok = Float.parseFloat(ostatokStr);
            float probeg = Float.parseFloat(probegStr);

            // Выполняем расчет
            calculatedResult = (zapr + ostatok) - ((path * norma) / 100);
            calculatedPathEnd = probeg + path;

            // Форматирование без нулей
            DecimalFormat decimalFormat = new DecimalFormat("#.##");

            // Отображаем результат
            textViewResult.setText(String.format("Результат: %s", decimalFormat.format(calculatedResult)));
            textViewPathEnd.setText(String.format("Пробег в конце: %s", decimalFormat.format(calculatedPathEnd)));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорректный ввод. Пожалуйста, введите числовые значения.", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveResultsToFile(View v) {
        String carName = editTextCarName.getText().toString(); // Получаем название машины

        // Проверка на пустое название машины
        if (carName.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите название машины перед сохранением.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Заменяем недопустимые символы в имени файла
        String sanitizedCarName = carName.replaceAll("[^a-zA-Z0-9]", "_"); // Заменяем все недопустимые символы на "_"
        String fileName = sanitizedCarName + ".txt"; // Создаем имя файла с расширением .txt
        String fileContent = "Название ТС: " + carName + "\nРезультат: " + calculatedResult + "\nПробег в конце: " + calculatedPathEnd;

        try (FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE)) {
            fos.write(fileContent.getBytes());
            Toast.makeText(this, "Результаты успешно сохранены в " + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при сохранении результатов.", Toast.LENGTH_SHORT).show();
        }
    }

    public void goBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}