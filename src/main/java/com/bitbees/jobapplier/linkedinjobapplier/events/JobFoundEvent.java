package com.bitbees.jobapplier.linkedinjobapplier.events;

import com.bitbees.jobapplier.linkedinjobapplier.pages.JobCard;
import org.springframework.context.ApplicationEvent;

public class JobFoundEvent extends ApplicationEvent {
    public JobFoundEvent(JobCard source) {
        super(source);
    }
}
