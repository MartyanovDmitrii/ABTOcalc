package com.example.abtocalc;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FileClient extends AppCompatActivity {

    private EditText ipEditText;
    private EditText portEditText;
    private EditText fileNameEditText; // Поле для ввода имени файла
    private TextView resultTextView;
    private Button requestButton; // Кнопка для отправки файла
    private ListView listViewFiles; // ListView для отображения найденных файлов
    private ArrayList<String> fileList; // Список файлов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_client); // Убедитесь, что этот XML-файл создан

        ipEditText = findViewById(R.id.ipEditText);
        portEditText = findViewById(R.id.portEditText);
        fileNameEditText = findViewById(R.id.fileNameEditText);
        resultTextView = findViewById(R.id.resultTextView);
        requestButton = findViewById(R.id.requestButton);
        listViewFiles = findViewById(R.id.listViewFiles); // Инициализация ListView
        fileList = new ArrayList<>(); // Инициализация списка файлов

        // Установка адаптера для ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        listViewFiles.setAdapter(adapter);

        // Установка TextWatcher для поиска файлов по имени файла
        fileNameEditText.addTextChangedListener(new TextWatcher() {
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

        // Установка дефолтных значений
        ipEditText.setText("10.0.2.2"); // Для эмулятора Android
        portEditText.setText("8080"); // Дефолтный порт вашего сервера

        requestButton.setOnClickListener(v -> sendFile());

        // Обработчик нажатий на элементы списка
        listViewFiles.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFileName = fileList.get(position); // Получаем имя выбранного файла
            fileNameEditText.setText(selectedFileName); // Устанавливаем имя файла в поле ввода
        });
    }

    private void sendFile() {
        String ip = ipEditText.getText().toString();
        String portString = portEditText.getText().toString();
        String fileName = fileNameEditText.getText().toString();

        if (ip.isEmpty() || portString.isEmpty() || fileName.isEmpty()) {
            resultTextView.setText("Введите IP, порт и имя файла");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            resultTextView.setText("Некорректный порт");
            return;
        }

        // Проверяем существование файла
        File fileToSend = new File(getFilesDir(), fileName); // Получаем файл из внутреннего хранилища
        if (!fileToSend.exists()) {
            resultTextView.setText("Файл не найден: " + fileToSend.getAbsolutePath());
            return; // Прерываем выполнение, если файл не найден
        }

        List<File> filesToSend = new ArrayList<>();
        filesToSend.add(fileToSend); // Добавляем файл для отправки
        connectToServer(ip, port, filesToSend);
    }


    private void connectToServer(String ip, int port, List<File> filesToSend) {
        new Thread(() -> {
            try {
                // Подключение к серверу
                Socket socket = new Socket(ip, port);
                runOnUiThread(() -> resultTextView.setText("Подключение успешно!"));

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Отправка количества файлов
                out.println(filesToSend.size());
                for (File file : filesToSend) {
                    if (!file.exists()) {
                        runOnUiThread(() -> resultTextView.append("\nФайл не найден: " + file.getName()));
                        continue; // Пропускаем файл, если он не найден
                    }

                    String fileName = file.getName();
                    StringBuilder fileContent = new StringBuilder();
                    // Чтение содержимого файла
                    try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = fileReader.readLine()) != null) {
                            fileContent.append(line).append("\n");
                        }
                    } catch (IOException e) {
                        runOnUiThread(() -> resultTextView.append("\nОшибка чтения файла: " + fileName));
                        continue; // Пропускаем файл, если произошла ошибка
                    }

                    // Отправка имени файла
                    out.println(fileName);
                    // Отправка содержимого файла
                    out.println(fileContent.toString());
                    out.println("END"); // Добавляем маркер окончания


                    // Ожидание подтверждения от сервера
                    String response = inStream.readLine();
                    if ("OK".equals(response)) {
                        runOnUiThread(() -> resultTextView.append("\nФайл " + fileName + " успешно отправлен и получен сервером."));
                    } else {
                        runOnUiThread(() -> resultTextView.append("\nОшибка при получении файла " + fileName + " на сервере."));
                    }
                }

                // Закрытие соединения
                socket.close();
                runOnUiThread(() -> resultTextView.append("\nВсе файлы отправлены!"));

            } catch (IOException e) {
                runOnUiThread(() -> resultTextView.setText("Ошибка при подключении: " + e.getMessage()));
            }
        }).start();
    }


// Метод для поиска файлов

    private void searchFiles(String query, ArrayAdapter<String> adapter) {
        File dir = getFilesDir(); // Получаем директорию
        File[] files = dir.listFiles(); // Получаем список файлов
        fileList.clear(); // Очищаем список перед новым поиском

        if (files != null) {
            for (File file : files) {
                if (file.getName().toLowerCase().contains(query.toLowerCase())) {
                    fileList.add(file.getName()); // Добавляем соответствующие файлы в список
                }
            }
        }

        // Обновляем адаптер и показываем ListView, если есть результаты
        adapter.notifyDataSetChanged();
        listViewFiles.setVisibility(fileList.isEmpty() ? View.GONE : View.VISIBLE);
    }
}

