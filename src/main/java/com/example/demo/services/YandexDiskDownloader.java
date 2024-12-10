package com.example.demo.services;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class YandexDiskDownloader {
    private static final String OAUTH_TOKEN = "y0_AgAAAAArShsWAAzjjQAAAAEa8SICAADxY0NfVK1ILaoGkCYjz7NQV92P-w";
    private static final String PUBLIC_FOLDER = "nrUVdWaKSOLnVA";

    public static void download() throws IOException {
        String requestString = "https://cloud-api.yandex.net/v1/disk/public/resources?public_key=https://yadi.sk/d/"+ PUBLIC_FOLDER + "&sort";
        URL url = new URL(requestString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // Настраиваем запрос
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "OAuth " + OAUTH_TOKEN);

        // Проверяем код ответа
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {

            // Читаем ответ
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject jsonEmbedded = jsonResponse.getJSONObject("_embedded");
            JSONArray jsonArray = jsonEmbedded.getJSONArray("items");
            JSONObject jsonFirstFile = jsonArray.getJSONObject(0);
            String jsonFirstFileLink = jsonFirstFile.getString("file");

            URL downloadLink = new URL(jsonFirstFileLink);
            HttpURLConnection downloadConnection = (HttpURLConnection) downloadLink.openConnection();
            downloadConnection.setRequestMethod("GET");

            // Сохраняем файл на диск
            InputStream inputStream = new BufferedInputStream(downloadConnection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream("downloaded_file.doc");
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.close();
            inputStream.close();

        } else {
            System.out.println("Ошибка: " + responseCode);
        }
    }

    public static void main(String[] args) throws IOException {
        YandexDiskDownloader downloader = new YandexDiskDownloader();
        downloader.download();
    }
}
