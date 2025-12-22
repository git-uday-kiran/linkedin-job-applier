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
        for (int i = 1; i < options.size(); i++) {
            optionsText.append(i).append(": ").append(options.get(i)).append("\n");
        }

        int maxIndex = options.size() - 1;
        String rangeText = maxIndex == 1 ? "1" : "1-" + maxIndex;

        return """
                You are filling out a job application form. Select the option that matches YOUR profile information.
                
                Question: %s
                
                Available Options:
                %s
                Respond with ONLY the number (%s) of the option that matches YOUR profile.
                Do not explain. Just return the number.
                
                Your answer:""".formatted(question, optionsText.toString().trim(), rangeText);
    }

    private LLMUtil() {
    }
}
