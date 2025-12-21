package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.input_solvers;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class NumericQuestionsSolver implements QuestionsSolver {
    @Override
    public By getQuestionLabelLocation() {
        return By.cssSelector("label[for^='single-line-text-form-component'][for$='numeric']");
    }

    @Override
    public String solveQuestion(String question) {
        return "0";
    }
}
