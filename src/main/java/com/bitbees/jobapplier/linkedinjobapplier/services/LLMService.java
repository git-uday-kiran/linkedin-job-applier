package com.bitbees.jobapplier.linkedinjobapplier.services;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@Service
public class LLMService {

    private final ChatModel chatModel;
    private final String profileContext;
    private final String questionSolvingContext;


    public LLMService(ChatModel chatModel,
                      @Value("classpath:profile-context.txt") Resource profileContextResource,
                      @Value("classpath:question-solving-context.txt") Resource questionSolvingContextResource
    ) {
        this.chatModel = chatModel;
        profileContext = Try.ofCallable(() -> Files.readString(profileContextResource.getFilePath(), StandardCharsets.UTF_8)).getOrNull();
        questionSolvingContext = Try.ofCallable(() -> Files.readString(questionSolvingContextResource.getFilePath(), StandardCharsets.UTF_8)).getOrNull();
        System.out.println("profileContext = " + profileContext);
        System.out.println("questionSolvingContext = " + questionSolvingContext);
    }

    public String askTextResponse(String question) {
        String prompt = getPrompt(question, "TEXT");
        return askLLM(prompt);
    }

    public Double askNumericResponse(String question) {
        String prompt = getPrompt(question, "NUMERIC");
        return Double.valueOf(askLLM(prompt));
    }

    public int askSelectOption(String question, List<String> options) {
        String prompt = getPrompt(question, options);
        System.out.println("prompt = " + prompt);
        String response = askLLM(prompt).trim();

        // Extract just the number from the response (handles cases where LLM adds extra text)
        String numberOnly = response.replaceAll("[^0-9]", "");

        if (numberOnly.isEmpty()) {
            log.error("LLM did not return a valid number. Response: {}", response);
            throw new IllegalStateException("LLM did not return a valid number. Response: " + response);
        }

        int selectedOption = Integer.parseInt(numberOnly);

        // Validate the option is within bounds
        if (selectedOption < 0 || selectedOption >= options.size()) {
            log.warn("LLM returned out-of-bounds option: {}. Defaulting to 0. Options size: {}", selectedOption, options.size());
            throw new IllegalStateException("LLM did not return a valid number. Response: " + response);
        }

        return selectedOption;
    }

    private String askLLM(String prompt) {
        // Combine both contexts into a single system message for better model understanding
        // Profile context first (knowledge base), then instructions (how to use it)
        String combinedSystemContext = profileContext + "\n\n" + questionSolvingContext;

        log.info("System context length: {} chars", combinedSystemContext.length());
        log.info("System context first 500 chars: {}", combinedSystemContext.substring(0, Math.min(500, combinedSystemContext.length())));
        log.info("User prompt: {}", prompt);

        List<ChatMessage> chats = List.of(
                SystemMessage.systemMessage(combinedSystemContext),
                UserMessage.userMessage(prompt)
        );

        ChatResponse chatResponse = chatModel.chat(chats);
        AiMessage aiMessage = chatResponse.aiMessage();

        log.info("LLM Response: {}", aiMessage.text());
        return aiMessage.text();
    }

    private static String getPrompt(String question, String responseFormat) {
        return """
                You are filling out a job application form. Answer this question about yourself using your profile:

                Question: %s
                Response Format Required: %s

                Answer:""".formatted(question, responseFormat);
    }

    private static String getPrompt(String question, List<String> options) {
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
}
