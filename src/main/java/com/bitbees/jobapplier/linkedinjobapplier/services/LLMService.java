package com.bitbees.jobapplier.linkedinjobapplier.services;

import com.bitbees.jobapplier.linkedinjobapplier.utils.LLMUtil;
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
    private final String jobSuitableContext;

    public LLMService(ChatModel chatModel,
                      @Value("classpath:profile-context.txt") Resource profileContextResource,
                      @Value("classpath:question-solving-context.txt") Resource questionSolvingContextResource,
                      @Value("classpath:job-suitable-context.txt") Resource jobSuitableContextResource
    ) {
        this.chatModel = chatModel;
        profileContext = Try.ofCallable(() -> Files.readString(profileContextResource.getFilePath(), StandardCharsets.UTF_8)).getOrNull();
        questionSolvingContext = Try.ofCallable(() -> Files.readString(questionSolvingContextResource.getFilePath(), StandardCharsets.UTF_8)).getOrNull();
        jobSuitableContext = Try.ofCallable(() -> Files.readString(jobSuitableContextResource.getFilePath(), StandardCharsets.UTF_8)).getOrNull();
    }

    public String askTextResponse(String question) {
        String prompt = LLMUtil.getPrompt(question, "TEXT");
        return askLLMQuestion(prompt);
    }

    public String askNumericResponse(String question) {
        String prompt = LLMUtil.getPrompt(question, "NUMERIC");
        return askLLMQuestion(prompt).replaceAll("\\.0*", "");
    }

    public int askSelectOption(String question, List<String> options) {
        String prompt = LLMUtil.getPrompt(question, options);
        String response = askLLMQuestion(prompt).trim();

        // Extract just the number from the response (handles cases where LLM adds extra text)
        String numberOnly = response.replaceAll("\\D", "");

        if (numberOnly.isEmpty()) {
            log.error("LLM did not return a valid number. Response: {}", response);
            throw new IllegalStateException("LLM did not return a valid number. Response: " + response);
        }

        int selectedOption = Integer.parseInt(numberOnly);

        // Validate the option is within bounds
        if (selectedOption < 0 || selectedOption >= options.size()) {
            log.warn("LLM returned out-of-bounds option: {}", selectedOption);
            throw new IllegalStateException("LLM did not return a valid number. Response: " + response);
        }

        return selectedOption;
    }

    public boolean askJobIsSuitable(String jobDescription) {
        String combinedSystemContext = profileContext + "\n\n" + jobSuitableContext;
        String prompt = "Job Description: \n" + jobDescription +
                "\n\n\nIs this job suitable? You MUST answer with ONLY 'Yes' or 'No' on the first line. If 'No', then must return the 'Reason: ' on next line.";

        String aiResponse = askLLM(combinedSystemContext, prompt).trim();

        if (aiResponse.startsWith("Yes")) {
            log.info("Job is suitable");
            return true;
        }
        if (aiResponse.startsWith("No")) {
            int reasonStartIndex = aiResponse.indexOf("Reason: ");
            if (reasonStartIndex < 0) {
                throw new IllegalStateException("LLM did not return a reason for job not suitable response. LLM Response: " + aiResponse);
            }
            String reason = aiResponse.substring(reasonStartIndex + 8);
            log.info("Job is not suitable, Reason: {}", reason);
            return false;
        }
        throw new IllegalStateException("LLM did not return a valid job suitable decision. LLM Response: " + aiResponse);
    }


    private String askLLMQuestion(String prompt) {
        // Combine both contexts into a single system message for better model understanding
        // Profile context first (knowledge base), then instructions (how to use it)
        String combinedSystemContext = profileContext + "\n\n" + questionSolvingContext;

        log.debug("System context length: {} chars", combinedSystemContext.length());
        log.debug("System context first 500 chars: {}", combinedSystemContext.substring(0, Math.min(500, combinedSystemContext.length())));
        log.debug("User prompt: {}", prompt);

        return askLLM(combinedSystemContext, prompt);
    }

    private String askLLM(String systemContext, String prompt) {
        log.debug("Asking LLM, Prompt: {}", prompt);
        List<ChatMessage> chats = List.of(
                SystemMessage.systemMessage(systemContext),
                UserMessage.userMessage(prompt)
        );
        ChatResponse chatResponse = chatModel.chat(chats);
        AiMessage aiMessage = chatResponse.aiMessage();
        log.debug("LLM Response: {}", aiMessage.text());
        return aiMessage.text();
    }
}
