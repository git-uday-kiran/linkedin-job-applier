package com.bitbees.jobapplier.linkedinjobapplier.configuration;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.exception.LangChain4jException;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;

@Slf4j
public class RotatingChatModel implements ChatModel {

    private final Duration timeout;
    private final List<ChatModel> chatModels;

    public RotatingChatModel(List<ChatModel> chatModels, Duration timeout) {
        if (chatModels == null || chatModels.isEmpty()) {
            throw new IllegalArgumentException("At least one chat model must be provided");
        }
        this.chatModels = chatModels;
        this.timeout = timeout;
        log.info("Initialized rotating chat model with {} providers", chatModels.size());
    }

    @Override
    public ChatResponse chat(List<ChatMessage> messages) {
        long startTime = System.currentTimeMillis();
        while (currentDuration(startTime).compareTo(timeout) < 0) {
            for (ChatModel chatModel : chatModels) {
                try {
                    log.info("Asking {}", chatModel.getClass().getSimpleName() + ", Provider: " + chatModel.provider());
                    return chatModel.chat(messages);
                } catch (Exception exception) {
                    log.warn("LLM Exception: {}", exception.getMessage());
                }
            }
        }
        throw new LangChain4jException("Failed to get response from any API provider");
    }

    private Duration currentDuration(long startTime) {
        long currentTime = System.currentTimeMillis();
        return Duration.ofMillis(currentTime - startTime);
    }
}
