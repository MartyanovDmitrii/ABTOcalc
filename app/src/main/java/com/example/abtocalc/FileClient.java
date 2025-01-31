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

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class FileClient extends AppCompatActivity {

    private EditText ipEditText;
    private EditText portEditText;
    private EditText fileNameEditText; // Поле для ввода имени файла
    private TextView resultTextView;
    private Button requestButton; // Кнопка для отправки файла
    private Button sendAllFilesButton; // Кнопка для отправки всех файлов
    private ListView listViewFiles; // ListView для отображения найденных файлов
    private ArrayList<String> fileList; // Список файлов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_client); // Убедитесь, что этот XML-файл создан

        // Инициализация элементов пользовательского интерфейса
        ipEditText = findViewById(R.id.ipEditText);
        portEditText = findViewById(R.id.portEditText);
        fileNameEditText = findViewById(R.id.fileNameEditText);
        resultTextView = findViewById(R.id.resultTextView);
        requestButton = findViewById(R.id.requestButton);
        sendAllFilesButton = findViewById(R.id.sendAllFilesButton);
        listViewFiles = findViewById(R.id.listViewFiles);
        fileList = new ArrayList<>();

        // Установка адаптера для ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        listViewFiles.setAdapter(adapter);

        // Установка TextWatcher для поиска файлов по имени файла
        fileNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFiles(s.toString(), adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
        // Обработчик нажатия на кнопку "Отправить все файлы на сервер"
        sendAllFilesButton.setOnClickListener(v -> sendAllFiles());
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

        new Thread(() -> {
            try {
                String boundary = "***"; // Разделитель для multipart
                String serverUrl = "http://" + ip + ":" + port + "/api/files/upload";
                HttpURLConnection connection = (HttpURLConnection) new URL(serverUrl).openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setRequestProperty("Connection", "Keep-Alive");

                // Начало отправки файла
                try (OutputStream outputStream = connection.getOutputStream();
                     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {

                    // Отправка имени файла
                    bufferedOutputStream.write(("--" + boundary + "\r\n").getBytes());
                    bufferedOutputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileToSend.getName() + "\"\r\n").getBytes());
                    bufferedOutputStream.write(("Content-Type: " + URLConnection.guessContentTypeFromName(fileToSend.getName()) + "\r\n").getBytes());
                    bufferedOutputStream.write(("\r\n").getBytes());

                    // Чтение файла и отправка его на сервер
                    try (FileInputStream fileInputStream = new FileInputStream(fileToSend)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            bufferedOutputStream.write(buffer, 0, bytesRead);
                        }
                    }

                    bufferedOutputStream.write(("\r\n").getBytes());
                    bufferedOutputStream.write(("--" + boundary + "--\r\n").getBytes());
                    bufferedOutputStream.flush();
                }

                // Получение ответа от сервера
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> resultTextView.setText("Файл успешно отправлен: " + fileToSend.getName()));
                } else {
                    runOnUiThread(() -> resultTextView.setText("Ошибка при отправке файла: " + fileToSend.getName() + " (код ответа: " + responseCode + ")"));
                }

                connection.disconnect();
            } catch (IOException e) {
                runOnUiThread(() -> resultTextView.setText("Ошибка при подключении: " + e.getMessage()));
            }
        }).start();
    }

    private void sendAllFiles() {
        String ip = ipEditText.getText().toString();
        String portString = portEditText.getText().toString();

        if (ip.isEmpty() || portString.isEmpty()) {
            resultTextView.setText("Введите IP и порт");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            resultTextView.setText("Некорректный порт");
            return;
        }

        File dir = getFilesDir(); // Получаем директорию
        File[] filesToSendArray = dir.listFiles(); // Получаем список файлов
        List<File> filesToSend = new ArrayList<>();

        if (filesToSendArray != null) {
            for (File file : filesToSendArray) {
                if (file.isFile()) { // Убедимся, что это файл
                    filesToSend.add(file); // Добавляем файл в список для отправки
                }
            }
        }

        if (filesToSend.isEmpty()) {
            resultTextView.setText("Нет файлов для отправки.");
            return; // Прерываем выполнение, если нет файлов
        }

        // Отправляем все файлы на сервер
        for (File file : filesToSend) {
            sendSingleFile(file, ip, port); // Отправляем каждый файл
        }
    }

    private void sendSingleFile(File fileToSend, String ip, int port) {
        new Thread(() -> {
            try {
                String boundary = "***"; // Разделитель для multipart
                String serverUrl = "http://" + ip + ":" + port + "/api/files/upload";
                HttpURLConnection connection = (HttpURLConnection) new URL(serverUrl).openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setRequestProperty("Connection", "Keep-Alive");

                // Начало отправки файла
                try (OutputStream outputStream = connection.getOutputStream();
                     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {

                    // Отправка имени файла
                    bufferedOutputStream.write(("--" + boundary + "\r\n").getBytes());
                    bufferedOutputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileToSend.getName() + "\"\r\n").getBytes());
                    bufferedOutputStream.write(("Content-Type: " + URLConnection.guessContentTypeFromName(fileToSend.getName()) + "\r\n").getBytes());
                    bufferedOutputStream.write(("\r\n").getBytes());

                    // Чтение файла и отправка его на сервер
                    try (FileInputStream fileInputStream = new FileInputStream(fileToSend)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            bufferedOutputStream.write(buffer, 0, bytesRead);
                        }
                    }

                    bufferedOutputStream.write(("\r\n").getBytes());
                    bufferedOutputStream.write(("--" + boundary + "--\r\n").getBytes());
                    bufferedOutputStream.flush();
                }

                // Получение ответа от сервера
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> resultTextView.append("\nФайл успешно отправлен: " + fileToSend.getName()));
                } else {
                    runOnUiThread(() -> resultTextView.append("\nОшибка при отправке файла: " + fileToSend.getName() + " (код ответа: " + responseCode + ")"));
                }

                connection.disconnect();
            } catch (IOException e) {
                runOnUiThread(() -> resultTextView.append("\nОшибка при подключении: " + e.getMessage()));
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

