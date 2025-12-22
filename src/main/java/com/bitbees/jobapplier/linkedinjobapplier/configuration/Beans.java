package com.bitbees.jobapplier.linkedinjobapplier.configuration;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.QuestionsSolver;
import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import com.bitbees.jobapplier.linkedinjobapplier.pages.WidGet;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Configuration
public class Beans implements ApplicationContextAware {

    private static ApplicationContext context;

    @Bean(destroyMethod = "quit")
    WebDriver webDriver() {
        log.info("Loading web driver... ");
        // Firefox installed as snap - profile and binary paths are different
        String profilePath = System.getProperty("user.home") +
                "/snap/firefox/common/.mozilla/firefox/1cft6ho4.uday-kiran-personal";
        String firefoxBinary = "/snap/firefox/current/usr/lib/firefox/firefox";

        FirefoxProfile profile = new FirefoxProfile(new File(profilePath));
        log.info("Loading Firefox profile from: {}", profilePath);

        var options = new FirefoxOptions()
                .setProfile(profile)
                .setBinary(firefoxBinary);
        log.info("Using Firefox binary: {}", firefoxBinary);
        var firefoxDriver = new FirefoxDriver(options);
        log.info("Web driver loaded");
        return firefoxDriver;
    }

    @Bean
    WebDriverWait webDriverWait(WebDriver webDriver) {
        return new WebDriverWait(webDriver, Duration.ofSeconds(20));
    }

    @Bean
    ChatModel chatModel() {
        log.info("Initializing rotating chat model with multiple API providers...");

        List<ChatModel> providers = new ArrayList<>();
        Duration chatModelTimeout = Duration.ofSeconds(5);

        ChatModel groqModel = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .modelName("llama-3.1-8b-instant")
                .apiKey(System.getenv("GROQ_API_KEY"))
                .timeout(chatModelTimeout)
                .maxRetries(1)
                .build();

        ChatModel geminiModel = GoogleAiGeminiChatModel.builder()
                .modelName("gemini-2.0-flash-exp")
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .timeout(chatModelTimeout)
                .maxRetries(1)
                .build();

        ChatModel openRouterModel = OpenAiChatModel.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .modelName("google/gemini-2.0-flash-exp:free")
                .apiKey(System.getenv("OPENROUTER_API_KEY"))
                .timeout(chatModelTimeout)
                .maxRetries(1)
                .build();

        ChatModel ollamaModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434/")
                .modelName("gemma2:2b")
                .think(false)
                .returnThinking(false)
                .temperature(0.0D)
                .timeout(chatModelTimeout)
                .numPredict(150)
                .maxRetries(1)
                .build();

//        providers.add(groqModel);
        providers.add(geminiModel);
//        providers.add(openRouterModel);
//        providers.add(ollamaModel);

        log.info("Successfully initialized {} API providers", providers.size());
        return new RotatingChatModel(providers, chatModelTimeout.multipliedBy(providers.size()));
    }

    public static WidGet widGet(ShadowRootHelper shadowRootHelper) {
        return new WidGet(
                context.getBean(WebDriver.class),
                context.getBean(WebDriverWait.class),
                shadowRootHelper,
                context.getBeansOfType(QuestionsSolver.class).values()
        );
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
