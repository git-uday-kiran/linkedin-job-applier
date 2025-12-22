package com.bitbees.jobapplier.linkedinjobapplier.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "config.job-finder")
public class JobFinderConfig {
    boolean skipViewedJobs;
}
