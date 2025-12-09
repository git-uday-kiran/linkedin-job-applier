package com.bitbees.jobapplier.linkedinjobapplier.events;

import org.springframework.context.ApplicationEvent;

public class JobFoundEvent extends ApplicationEvent {
    public JobFoundEvent(String jobUrl) {
        super(jobUrl);
    }
}
