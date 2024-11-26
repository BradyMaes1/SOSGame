package com.beginsecure.maventest.sosgamesprint2;

public class LLMClientTest {
    public static void main(String[] args) {
        LLMClient llmClient = new LLMClient();

        try {
            String prompt = "How can a rook move in chess?";
            int maxLength = 256;

            String result = llmClient.generateText(prompt, maxLength);
            System.out.println("Generated Text:\n" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
