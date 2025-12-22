package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import com.bitbees.jobapplier.linkedinjobapplier.services.LLMService;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface SelectionQuestionSolver extends QuestionsSolver {

    Logger log = LoggerFactory.getLogger(SelectionQuestionSolver.class);

    @Override
    default WebElement getAnswerInput(ShadowRootHelper shadowRootHelper, WebElement questionLabel) {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        return (WebElement) js.executeScript("return arguments[0].closest('fieldset');", questionLabel);
    }

    @Override
    default void solve(ShadowRootHelper shadowRootHelper, WebElement questionLabel, WebElement answerInput) {
        String question = questionLabel.getText();
        List<WebElement> options = answerInput.findElements(By.cssSelector("div[data-test-text-selectable-option]"));

        List<String> optionsText = options.stream()
                .map(WebElement::getText)
                .toList();
        int option = selectOption(question, optionsText);
        WebElement optionElement = options.get(option).findElement(By.cssSelector("label"));
        shadowRootHelper.click(optionElement);
    }

    default int selectOption(String question, List<String> options) {
        log.info("Asking LLM, select option for question: {}, options: {}", question, options);
        int option = getLlmService().askSelectOption(question, options);
        log.info("Chosen option: {}", option);
        return option;
    }

    WebDriver getWebDriver();

    LLMService getLlmService();
}
