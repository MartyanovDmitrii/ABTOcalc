package com.example.abtocalc;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    private EditText editTextCarName;
    private Button buttonFindCar;
    private ListView listViewFiles;
    private ArrayList<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Инициализация элементов интерфейса
        editTextCarName = findViewById(R.id.editTextCarName);
        buttonFindCar = findViewById(R.id.buttonFindCar);
        listViewFiles = findViewById(R.id.listViewFiles);
        fileList = new ArrayList<>();

        // Устанавливаем адаптер для ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        listViewFiles.setAdapter(adapter);

        // Добавляем TextWatcher для поиска файлов
        editTextCarName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не требуется
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFiles(s.toString(), adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Не требуется
            }
        });

        // Обработчик нажатия на кнопку "Найти авто"
        buttonFindCar.setOnClickListener(v -> {
            String carName = editTextCarName.getText().toString().trim();

            if (carName.isEmpty()) {
                Toast.makeText(SecondActivity.this, "Введите название автомобиля.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверка существования файла
            File dir = getFilesDir();
            File file = new File(dir, carName + ".txt");

            if (!file.exists()) {
                Toast.makeText(SecondActivity.this, "Файл с таким названием не найден.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Открываем содержимое файла в новой активности
            Intent intent = new Intent(SecondActivity.this, FileContentActivity.class);
            intent.putExtra("fileName", carName + ".txt");
            startActivity(intent);
        });

        // Устанавливаем обработчик нажатий на элементы списка
        listViewFiles.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFileName = fileList.get(position); // Получаем имя выбранного файла
            Intent intent = new Intent(SecondActivity.this, FileContentActivity.class);
            intent.putExtra("fileName", selectedFileName); // Передаем имя файла в Intent
            startActivity(intent); // Запускаем новую активность
        });
    }

    // Метод для поиска файлов
    private void searchFiles(String query, ArrayAdapter<String> adapter) {
        File dir = getFilesDir();
        File[] files = dir.listFiles();
        fileList.clear(); // Очищаем список перед новым поиском

        if (files != null) {
            for (File file : files) {
                if (file.getName().toLowerCase().contains(query.toLowerCase())) {
                    fileList.add(file.getName());
                }
            }
        }

        // Обновляем адаптер и показываем ListView, если есть результаты
        adapter.notifyDataSetChanged();
        listViewFiles.setVisibility(fileList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // Метод для возврата на главную страницу
    public void goBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
