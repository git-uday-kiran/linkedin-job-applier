package com.bitbees.jobapplier.linkedinjobapplier.configuration;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.QuestionsSolver;
import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import com.bitbees.jobapplier.linkedinjobapplier.pages.WidGet;
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

//    @Bean
    OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .maxRetries(3)
                .baseUrl("http://localhost:11434/")
                .modelName("llama3.2:3b")  // Larger model for better context handling
                .timeout(Duration.ofSeconds(15))
                .build();
    }

//    @Bean
    OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .modelName("x-ai/grok-4.1-fast")
                .apiKey("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")
                .build();
    }

    @Bean
    GoogleAiGeminiChatModel googleAiGeminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .modelName("gemini-2.5-flash")
                .apiKey("AIzaSyAidXXPMb0ewZfOLp_8JP7phZxBSYgMafI")
                .build();
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
