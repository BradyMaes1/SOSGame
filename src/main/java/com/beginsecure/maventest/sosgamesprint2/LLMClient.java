package com.beginsecure.maventest.sosgamesprint2;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class LLMClient {
    private static final String LLM_URL = "http://127.0.0.1:5000/generate";
    private final OkHttpClient client;

    public LLMClient() {
        client = new OkHttpClient();
    }

    /**
     * Sends a prompt to the LLM server and retrieves the generated text.
     *
     * @param prompt     The input prompt for the LLM.
     * @param maxLength  The maximum length of the generated text.
     * @return The generated text from the LLM.
     * @throws IOException If an error occurs during the HTTP request.
     */

    public String generateText(String prompt, int maxLength) throws IOException {
        // Prepare the JSON body
        JSONObject json = new JSONObject();
        json.put("prompt", prompt);
        json.put("max_length", maxLength);

        // Create the request
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(LLM_URL)
                .post(body)
                .build();

        // Execute the request and handle the response
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Parse the JSON response
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.getString("generated_text");
            } else {
                throw new IOException("Unexpected HTTP code " + response.code());
            }
        }
    }
}
