package com.gcg.djs.domain.handlers;

import com.gcg.djs.domain.interfaces.services.IJobService;

public class JobExecuteHandler {
    private final IJobService jobsService;

    public JobExecuteHandler(IJobService jobsService) {
        this.jobsService = jobsService;
    }

    public void Execute() {
    }
}
