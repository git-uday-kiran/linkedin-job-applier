package com.bitbees.jobapplier.linkedinjobapplier.utils;

import java.util.List;

public final class LLMUtil {

    public static String getPrompt(String question, String responseFormat) {
        return """
                You are filling out a job application form. Answer this question about yourself using your profile:
                
                Question: %s
                Response Format Required: %s
                
                Answer:""".formatted(question, responseFormat);
    }

    public static String getPrompt(String question, List<String> options) {
        StringBuilder optionsText = new StringBuilder();
        for (int i = 0; i < options.size(); i++) {
            optionsText
                    .append(i).append(": ")
                    .append("\"").append(options.get(i)).append("\"")
                    .append("\n");
        }

        int maxIndex = options.size() - 1;
        String rangeText = maxIndex == 0 ? "0" : "0-" + maxIndex;

        return """
                Select the option index (%s) that matches the candidate profile.

                Question: %s

                Options:
                %s

                RESPONSE FORMAT - YOU MUST FOLLOW EXACTLY:
                - Return ONLY the index number: %s
                - NO explanations
                - NO reasoning
                - NO text before or after the number
                - NO country codes, phone numbers, or any other numbers
                - Just the single digit index

                WRONG: "Based on profile, +91, so 1" ❌
                WRONG: "1 because it matches" ❌
                WRONG: "The answer is 1" ❌
                CORRECT: "1" ✓

                If option 0 is a placeholder like "Select an option", skip it.

                Answer:""".formatted(rangeText, question, optionsText.toString().trim(), rangeText);
    }

    private LLMUtil() {
    }
}
