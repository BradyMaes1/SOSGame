package com.beginsecure.maventest.sosgamesprint2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service for communicating with an LLM API to make moves for the computer player in the SOS game.
 */
public class LLMService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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"; // Replace with your actual API key
    private static final String MODEL = "gpt-3.5-turbo"; // Specify the model to use

    /**
     * Sends a message to the LLM API and retrieves a response.
     *
     * @param prompt the prompt message to send
     * @return the response from the LLM API
     */
    public String getMoveFromLLM(String prompt) {
        try {
            System.out.println("Sending prompt to LLM: " + prompt); // Debugging print

            // Establish connection
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Construct request body using JSON library
            String requestBody = new JSONObject()
                    .put("model", MODEL)
                    .put("messages", new JSONArray().put(
                            new JSONObject().put("role", "user").put("content", prompt)
                    ))
                    .toString();

            System.out.println("Request body: " + requestBody); // Debugging print

            // Send request body
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(requestBody);
                writer.flush();
            }

            // Get response
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Error: API request failed with response code " + responseCode); // Debugging print
                return "Error: API request failed with response code " + responseCode;
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            System.out.println("Received response from LLM: " + response.toString()); // Debugging print

            return extractContentFromResponse(response.toString());

        } catch (IOException e) {
            System.out.println("Error during LLM API request: " + e.getMessage()); // Debugging print
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Extracts content from the API's JSON response.
     *
     * @param response the raw JSON response
     * @return the extracted content
     */
    private String extractContentFromResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject messageObject = choices.getJSONObject(0).getJSONObject("message");
                String content = messageObject.getString("content");
                System.out.println("Extracted content from LLM response: " + content); // Debugging print
                return content;
            } else {
                System.out.println("Error: No choices found in LLM response."); // Debugging print
                return "Error: No choices found in response.";
            }
        } catch (Exception e) {
            System.out.println("Error parsing LLM response: " + e.getMessage()); // Debugging print
            return "Error: Could not parse JSON response - " + e.getMessage();
        }
    }
}