package com.bitbees.jobapplier.linkedinjobapplier.configuration;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.InputQuestions;
import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import com.bitbees.jobapplier.linkedinjobapplier.pages.WidGet;
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

    public static WidGet widGet(ShadowRootHelper shadowRootHelper) {
        return new WidGet(
                context.getBean(WebDriver.class),
                context.getBean(WebDriverWait.class),
                shadowRootHelper,
                context.getBean(InputQuestions.class)
        );
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
