package com.bitbees.jobapplier.linkedinjobapplier.services;

import com.bitbees.jobapplier.linkedinjobapplier.easyapply.enums.*;
import com.bitbees.jobapplier.linkedinjobapplier.easyapply.filters.EasyApplyFilter;
import com.bitbees.jobapplier.linkedinjobapplier.models.Page;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Log4j2
@Service
public class EasyApplyService extends Page {

    protected EasyApplyService(WebDriver webDriver, WebDriverWait wait) {
        super(webDriver, wait);
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

        By applyFilterLocation = By.xpath("//div[contains(@class, 'search-reusables__side-panel--open')]//button/span[contains(., 'Show')]");
        WebElement applyFilter = waitForPresenceAndClickable(applyFilterLocation);
        log.info("Clicking apply filter...");
        click(applyFilter);
        pause(Duration.ofSeconds(4));
    }

    private void clickAdvancedFilters() {
        log.info("Clicking advanced filters option...");
        var location = By.xpath("//button[normalize-space()='All filters']");
        WebElement filtersOption = waitForPresenceAndClickable(location);
        click(filtersOption);
    }
}
