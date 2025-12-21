package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers.input_solvers.NumericQuestionsSolver;
import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import com.bitbees.jobapplier.linkedinjobapplier.models.ShadowRootHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
public class InputQuestions extends Page {

    private static final Logger log = LogManager.getLogger(InputQuestions.class);

    private final NumericQuestionsSolver numericQuestionsSolver;

//    private final QuestionAnswerService qaService;

    private final By inputQuestionGroupLocation = By.xpath("//div[@data-test-single-line-text-form-component=\"\"]");
    private final By directLabelLocation = By.xpath(".//*[self::label/span[1] or self::label]");
    private final By titleLocation = By.xpath("preceding-sibling::span[contains(@class, 'jobs-easy-apply-form-section__group-title')][1]");
    private final By subTitleLocation = By.xpath("preceding-sibling::span[contains(@class, 'jobs-easy-apply-form-section__group-subtitle')][1]");
    private final By inputLocation = By.xpath(".//*[self::input[@type='text'] or self::textarea]");
    private final By citySuggestionsLocation = By.xpath("following-sibling::div[1]");

    protected InputQuestions(WebDriver webDriver, WebDriverWait wait, NumericQuestionsSolver numericQuestionsSolver) {
        super(webDriver, wait);
        this.numericQuestionsSolver = numericQuestionsSolver;
    }


    public void scan(ShadowRootHelper shadowRootHelper) {
        numericQuestionsSolver.scan(shadowRootHelper);
    }

    private void solveQuestion(WebElement question) {
        if (isDirectQuestion(question)) solveDirectQuestion(question);
        else solveTitleSubTitleQuestion(question);
    }

    private void solveTitleSubTitleQuestion(WebElement question) {
        String title = question.findElement(titleLocation).getText();
        String subTitle = question.findElement(subTitleLocation).getText();
        String label = title + '\n' + subTitle;
        solveQuestionWithOnlyInput(question, label);
    }

    private void solveDirectQuestion(WebElement question) {
        String label = question.findElement(directLabelLocation).getText();
        if (isCityQuestion(label)) solveCityQuestion(question, label);
        else solveQuestionWithOnlyInput(question, label);
    }

    private static boolean isCityQuestion(String label) {
        String lowerCase = label.toLowerCase();
        return lowerCase.startsWith("city") || lowerCase.startsWith("location (city)");
    }

    private void solveQuestionWithOnlyInput(WebElement question, String label) {
//        String answer = qaService.ask(label, Tag.FILL_UP_QA);
//        WebElement input = question.findElement(inputLocation);
//        input.clear();
//        input.sendKeys(answer);
    }

    private void solveCityQuestion(WebElement question, String label) {
//        String city = qaService.ask(label, Tag.FILL_UP_QA);
//        WebElement input = question.findElement(inputLocation);
//        input.sendKeys(city, Keys.ENTER);

//        WebElement citySuggestions = waitForCitySuggestionsPresence(input);
//        waitForVisible(citySuggestions).sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
    }

    private boolean isDirectQuestion(WebElement question) {
        return !question.findElements(directLabelLocation).isEmpty();
    }

    private WebElement waitForCitySuggestionsPresence(WebElement input) {
//        wait.until(e -> Try.run(() -> input.findElement(citySuggestionsLocation)).isSuccess());
        return input.findElement(citySuggestionsLocation);
    }

}
