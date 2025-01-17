package com.example.abtocalc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class FileClient extends AppCompatActivity {

    private EditText ipEditText;
    private EditText portEditText;
    private EditText fileNameEditText;
    private TextView resultTextView;
    private Button requestButton;
    private Button syncButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_client); // Убедитесь, что этот XML-файл создан

        ipEditText = findViewById(R.id.ipEditText);
        portEditText = findViewById(R.id.portEditText);
        fileNameEditText = findViewById(R.id.fileNameEditText);
        resultTextView = findViewById(R.id.resultTextView);
        requestButton = findViewById(R.id.requestButton);
        syncButton = findViewById(R.id.syncButton); // Добавьте кнопку для синхронизации

        // Установка дефолтных значений
        ipEditText.setText("10.0.2.2"); // Для эмулятора Android
        portEditText.setText("8080"); // Дефолтный порт вашего сервера

        requestButton.setOnClickListener(v -> {
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

            connectToServer(ip, port, fileName);
        });

        syncButton.setOnClickListener(v -> {
            String ip = ipEditText.getText().toString();
            String portString = portEditText.getText().toString();

            if (ip.isEmpty() || portString.isEmpty()) {
                resultTextView.setText("Введите IP и порт для синхронизации");
                return;
            }

            int port;
            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException e) {
                resultTextView.setText("Некорректный порт");
                return;
            }

            syncFilesWithServer(ip, port);
        });
    }

    private void connectToServer(String ip, int port, String fileName) {
        new Thread(() -> {
            try {
                // Подключение к серверу
                Socket socket = new Socket(ip, port);
                runOnUiThread(() -> resultTextView.setText("Подключение успешно!"));

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Чтение содержимого файла
                String filePath = getFilesDir() + "/" + fileName; // Путь к файлу на устройстве
                StringBuilder fileContent = new StringBuilder();

                try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }
                } catch (FileNotFoundException e) {
                    runOnUiThread(() -> resultTextView.setText("Файл не найден: " + filePath));
                    return;
                }

                // Проверка содержимого файла
                if (fileContent.length() == 0) {
                    runOnUiThread(() -> resultTextView.setText("Файл пуст: " + filePath));
                    return;
                }

                // Отправка команды и файла на сервер
                out.println("UPLOAD"); // Команда для сервера
                out.println(fileName); // Имя файла
                out.println(fileContent.toString()); // Содержимое файла

                // Чтение ответа от сервера
                String response = inStream.readLine();
                runOnUiThread(() -> resultTextView.setText(response));

                // Закрытие сокета
                inStream.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> resultTextView.setText("Ошибка: " + e.getMessage()));
            }
        }).start();
    }

    private void syncFilesWithServer(String ip, int port) {
        new Thread(() -> {
            File filesDir = getFilesDir();
            File[] files = filesDir.listFiles(); // Получаем список файлов в директории

            if (files != null && files.length > 0) {
                try {
                    Socket socket = new Socket(ip, port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".txt")) { // Проверка на текстовые файлы
                            // Отладочное сообщение перед отправкой файла
                            System.out.println("Отправка файла: " + file.getName());

                            StringBuilder fileContent = new StringBuilder();
                            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                                String line;
                                while ((line = fileReader.readLine()) != null) {
                                    fileContent.append(line).append("\n");
                                }
                            }

                            // Проверка содержимого файла
                            if (fileContent.length() == 0) {
                                System.out.println("Файл пуст: " + file.getName()); // Отладочное сообщение
                                continue; // Пропустить пустой файл
                            }

                            // Отладочное сообщение о содержимом файла
                            System.out.println("Содержимое файла: " + fileContent.toString());

                            // Отправка команды и файла на сервер
                            out.println("UPLOAD"); // Команда для сервера
                            out.println(file.getName()); // Имя файла
                            out.println(fileContent.toString()); // Содержимое файла

                            // Чтение ответа от сервера
                            String response = inStream.readLine();
                            runOnUiThread(() -> resultTextView.append(response + "\n"));
                        } else {
                            System.out.println("Пропущен файл: " + file.getName()); // Отладочное сообщение
                        }
                    }

                    // Закрытие сокета
                    inStream.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> resultTextView.setText("Ошибка: " + e.getMessage()));
                }
            } else {
                runOnUiThread(() -> resultTextView.setText("Нет файлов для синхронизации"));
            }
        }).start();
    }
}


