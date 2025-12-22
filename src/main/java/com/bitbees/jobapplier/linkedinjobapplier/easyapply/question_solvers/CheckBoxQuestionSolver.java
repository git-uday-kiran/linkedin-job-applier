package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import com.bitbees.jobapplier.linkedinjobapplier.services.LLMService;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class CheckBoxQuestionSolver implements QuestionsSolver{

    private final WebDriver driver;
    private final LLMService llmService;

    public CheckBoxQuestionSolver(WebDriver driver, LLMService llmService) {
        this.driver = driver;
        this.llmService = llmService;
    }

    @Override
    public By getQuestionLabelLocation() {
        return By.cssSelector("fieldset[data-test-checkbox-form-component='true'] legend>div>span:first-child");
    }

    @Override
    public WebElement getAnswerInput(ShadowRootHelper shadowRootHelper, WebElement questionLabel) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (WebElement) js.executeScript("return arguments[0].closest('fieldset');", questionLabel);
    }

    @Override
    public void solve(ShadowRootHelper shadowRootHelper, WebElement questionLabel, WebElement answerInput) {
        String question = questionLabel.getText();
        List<WebElement> options = answerInput.findElements(By.cssSelector("div[data-test-text-selectable-option]"));

        List<String> optionsText = options.stream()
                .map(WebElement::getText)
                .toList();
        int option = selectOption(question, optionsText);
        WebElement optionElement = options.get(option).findElement(By.cssSelector("label"));
        shadowRootHelper.click(optionElement);
    }

    public int selectOption(String question, List<String> options) {
        log.info("Asking LLM, select option for question: {}, options: {}", question, options);
        int option = llmService.askSelectOption(question, options);
        log.info("Chosen option: {}", option);
        return option;
    }
}
