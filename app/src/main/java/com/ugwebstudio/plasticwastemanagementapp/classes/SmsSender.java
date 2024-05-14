package com.ugwebstudio.plasticwastemanagementapp.classes;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class SmsSender {

    private static final String API_URL = "http://www.egosms.co/api/v1/json/";
    private static final String USERNAME = "nathantugume";
    private static final String PASSWORD = "";
    private static final String SENDERID = "plasticApp";

    public static void sendSms(String number, String message) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInputString = buildJsonPayload(number, message);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.d("Response Code: ", String.valueOf(responseCode));

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d("Response: ", response.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("SmsSender", "Error sending SMS", e);
            }
        });
    }

    private static String buildJsonPayload(String number, String message) {
        String formattedNumber = PhoneFormatter.formatPhoneNumber(number);
        return String.format("{\"method\": \"SendSms\", \"userdata\":{\"username\":\"%s\",\"password\":\"%s\"},\"msgdata\":[{\"number\":\"%s\",\"message\":\"%s\",\"senderid\":\"%s\"}]}",
                USERNAME, PASSWORD, formattedNumber, message, SENDERID);
    }
}
