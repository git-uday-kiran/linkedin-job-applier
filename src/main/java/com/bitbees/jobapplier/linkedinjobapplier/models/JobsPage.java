package com.bitbees.jobapplier.linkedinjobapplier.models;

import com.bitbees.jobapplier.linkedinjobapplier.components.UnorderedList;
import com.bitbees.jobapplier.linkedinjobapplier.easyapply.enums.*;
import com.bitbees.jobapplier.linkedinjobapplier.easyapply.filters.EasyApplyFilter;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class JobsPage extends Page {

    private final By searchLocation = By.xpath("//header/div/div/div/div[2]/div/div/div/div/div/div/div/input[1]");
    private final By addressLocation = By.xpath("//div[contains(@class, 'jobs-search-box')]/div[2]//input[1]");
    private final By locationCompletePopupLocation = By.xpath("//div[contains(@class, 'jobs-search-box__typeahead-results--location')]/div/ul");

    public JobsPage(WebDriver webDriver, WebDriverWait wait) {
        super(webDriver, wait);
    }

    public void navigate() {
        log.info("Navigating to jobs page in another tab");
        webDriver.switchTo().newWindow(WindowType.TAB);
        String jobsPageUrl = "https://www.linkedin.com/jobs/";
        webDriver.get(jobsPageUrl);
        waitForPageReady();
    }

    public void search(String query) {
        log.info("Searching for jobs with query: {}", query);
        WebElement searchElement = waitForPresenceAndClickable(searchLocation);
        pause(Duration.ofSeconds(1));
        searchElement.clear();
        searchElement.sendKeys(query, Keys.ENTER);
        pause(Duration.ofSeconds(1));
    }

    public void searchLocation(String location) {
        log.info("Searching for jobs with location: {}", location);
        // Click location search input box
        WebElement addressLocationElement = waitForPresenceAndClickable(addressLocation);
        pause(Duration.ofSeconds(4));
        addressLocationElement.clear();
        addressLocationElement.sendKeys(location);

        new Actions(webDriver)
                .click(addressLocationElement)
                .pause(Duration.ofSeconds(1))
                .perform();

        // Get autocompletion popup
        Try.of(() -> waitForPresenceAndClickable(locationCompletePopupLocation))
                .andThen(this::resolveLocationsAutoCompletion)
                .orElseRun(logError("Location auto completion fillup failed"));
    }

    public void applyFilter(EasyApplyFilter filter) {
        log.info("Applying easy apply filter: {}", filter);
        clickAdvancedFilters();

        findAndClick(filter.getSortBy().getLocation());
        findAndClick(filter.getDatePosted().getLocation());

        filter.getExperienceLevels().stream()
                .map(ExperienceLevel::getLocation)
                .forEach(this::findAndClick);

        filter.getJobTypes().stream()
                .map(JobType::getLocation)
                .forEach(this::findAndClick);

        filter.getWorkTypes().stream()
                .map(WorkType::getLocation)
                .forEach(this::findAndClick);

        if (filter.getEasyApply() == EasyApplyOption.ENABLE) {
            findAndClick(filter.getEasyApply().getLocation());
        }

        filter.getLocations().stream()
                .map(Location::getLocation)
                .map(this::tryFindElement)
                .flatMap(Optional::stream)
                .forEach(this::click);

        if (filter.getUnder10Applicants() == Under10Applicants.ENABLE) {
            findAndClick(filter.getUnder10Applicants().getLocation());
        }

        By applyFilterLocation = By.xpath("/html/body/div[4]/div/div/div[3]/div/button[2]/span");
        WebElement applyFilter = waitForPresenceAndClickable(applyFilterLocation);
        log.info("Clicking apply filter...");
        click(applyFilter);
        pause(Duration.ofSeconds(4));
    }

    public void clickAdvancedFilters() {
        log.info("Clicking advanced filters option...");
        var location = By.xpath("//button[normalize-space()='All filters']");
        WebElement filtersOption = waitForPresenceAndClickable(location);
        click(filtersOption);
    }

    private void resolveLocationsAutoCompletion(WebElement autoCompletionPopupElement) {
        log.info("Resolving location auto completion");
        // All autocompletion location
        List<WebElement> suggestedLocations = (new UnorderedList(autoCompletionPopupElement)).getLiElements();
        suggestedLocations.getFirst().click();
        pause(Duration.ofSeconds(2));
    }

}
