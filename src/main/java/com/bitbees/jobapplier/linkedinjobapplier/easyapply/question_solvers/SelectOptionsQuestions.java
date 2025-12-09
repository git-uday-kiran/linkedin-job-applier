package com.bitbees.jobapplier.linkedinjobapplier.easyapply.question_solvers;


import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;


@Service
public class SelectOptionsQuestions extends Page {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(SelectOptionsQuestions.class);
//    private final QuestionAnswerService qaService;

    private final By questionGroupLocation = By.xpath("//div[@data-test-text-entity-list-form-component='']/select");
    private final By directLabelLocation = By.xpath("..//label/span[1]");
    private final By titleLocation = By.xpath("preceding-sibling::span[contains(@class, 'jobs-easy-apply-form-section__group-title')][1]");
    private final By subTitleLocation = By.xpath("preceding-sibling::span[contains(@class, 'jobs-easy-apply-form-section__group-subtitle')][1]");
    private final By selectionLocation = By.xpath(".");

    protected SelectOptionsQuestions(WebDriver webDriver, WebDriverWait wait) {
        super(webDriver, wait);
    }


    public void scan() {
//        if (isPresentInDOM(questionGroupLocation)) {
//            findAll(questionGroupLocation).stream()
//                    .map(this::scrollIntoView)
//                    .map(this::waitForClickable)
//                    .forEach(this::solveQuestion);
//        }
    }

    private void solveQuestion(WebElement question) {
        if (isDirectQuestion(question)) solveDirectQuestion(question);
        else solveTitleSubTitleQuestion(question);
    }

    private void solveTitleSubTitleQuestion(WebElement question) {
        String title = question.findElement(titleLocation).getText();
        String subTitle = question.findElement(subTitleLocation).getText();
        String label = title + '\n' + subTitle;
//        solveQuestionWithOnlySelectOptions(question, label, Tag.FILL_UP_QA);
    }

    private void solveDirectQuestion(WebElement question) {
        String label = question.findElement(directLabelLocation).getText();
//        solveQuestionWithOnlySelectOptions(question, label, Tag.FILL_UP_QA);
    }

//    private void solveQuestionWithOnlySelectOptions(WebElement question, String label, Tag tag) {
//        Select select = new Select(question.findElement(selectionLocation));
//
//        List<String> options = select.getOptions().stream()
//                .map(WebElement::getText)
//                .toList();
//        String answer = qaService.ask(label, options, tag);
//        select.selectByValue(answer);
//    }

    private boolean isDirectQuestion(WebElement question) {
        return !question.findElements(directLabelLocation).isEmpty();
    }

}
