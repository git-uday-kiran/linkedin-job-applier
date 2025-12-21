package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.input_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.services.LLMService;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class TextQuestionsSolver implements InputQuestionSolver {
    private final LLMService lLMService;

    public TextQuestionsSolver(LLMService lLMService) {
        this.lLMService = lLMService;
    }

    @Override
    public By getQuestionLabelLocation() {
        return By.cssSelector("label[for^='single-line-text-form-component'][for$='text']");
    }

    @Override
    public String solveQuestion(String question) {
        log.info("Asking LLM question {}", question);
        String response= lLMService.askTextResponse(question);
        log.info("Response: {}", response);
        return response;
    }
}
