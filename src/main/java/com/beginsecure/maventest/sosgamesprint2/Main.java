package com.beginsecure.maventest.sosgamesprint2;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
        // Example message to send to the ChatGPT API
        String response = chatGPT("What model version are you?");
        System.out.println(response); // Prints the response from the ChatGPT API
    }

    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "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"; // Replace with your actual API key
        String model = "gpt-4o-mini"; // Adjust the model as needed

        try {
            // Establish a connection and configure the HTTP request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Create the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())) {
                writer.write(body);
                writer.flush();
            }

            // Get the response from the API
            int responseCode = con.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Error: API request failed with response code " + responseCode;
            }

            // Read the response
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            // Extract content from the response
            //System.out.println("Full response: " + response.toString());
            return extractContentFromResponse(response.toString());

        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    // Extracts content using JSON parsing
    public static String extractContentFromResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject messageObject = choices.getJSONObject(0).getJSONObject("message");
                return messageObject.getString("content");
            } else {
                return "Error: No choices found in response.";
            }
        } catch (Exception e) {
            return "Error: Could not parse JSON response - " + e.getMessage();
        }
    }
}
