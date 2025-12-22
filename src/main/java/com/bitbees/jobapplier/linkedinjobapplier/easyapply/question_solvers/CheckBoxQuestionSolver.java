package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.services.LLMService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

@Getter
@Log4j2
@Service
public class CheckBoxQuestionSolver implements SelectionQuestionSolver {
    private final WebDriver webDriver;
    private final LLMService llmService;

    public CheckBoxQuestionSolver(WebDriver webDriver, LLMService llmService) {
        this.webDriver = webDriver;
        this.llmService = llmService;
    }

    @Override
    public By getQuestionLabelLocation() {
        return By.cssSelector("fieldset[data-test-checkbox-form-component='true'] legend>div>span:first-child");
    }
}
