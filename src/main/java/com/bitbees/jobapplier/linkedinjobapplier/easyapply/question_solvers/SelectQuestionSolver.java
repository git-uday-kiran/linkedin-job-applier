package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import com.bitbees.jobapplier.linkedinjobapplier.services.LLMService;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class SelectQuestionSolver implements QuestionsSolver {
    private final LLMService lLMService;

    public SelectQuestionSolver(LLMService lLMService) {
        this.lLMService = lLMService;
    }

    @Override
    public By getQuestionLabelLocation() {
        return By.cssSelector("label[for*='text-entity-list-form-component-formElement']");
    }

    @Override
    public void solve(ShadowRootHelper shadowRootHelper, WebElement questionLabel, WebElement answerInput) {
        String question = questionLabel.findElement(By.cssSelector("span")).getText();
        Select select = new Select(answerInput);
        solveQuestion(question, select);
    }

    public void solveQuestion(String question, Select select) {
        List<WebElement> options = select.getOptions();

        List<String> optionsText = options.stream()
                .map(WebElement::getText)
                .toList();
        int option = selectOption(question, optionsText);

        select.selectByVisibleText(options.get(option).getText());
    }

    public int selectOption(String question, List<String> options) {
        log.info("Asking LLM, select option for question: {}, options: {}", question, options);
        int option = lLMService.askSelectOption(question, options);
        log.info("Chosen option: {}", option);
        return option;
    }
}
