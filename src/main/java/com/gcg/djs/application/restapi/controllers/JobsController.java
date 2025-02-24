package com.gcg.djs.application.restapi.controllers;

import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.interfaces.services.IJobService;
import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.jobs.CreateJob;
import com.gcg.djs.domain.models.jobs.Job;
import com.gcg.djs.domain.models.jobs.UpdateJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/jobs")
public class JobsController {

    private final IJobService jobService;

    @Autowired
    public JobsController(IJobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody CreateJob createJob) {
        try {
            Job job = jobService.addJob(createJob);
            return new ResponseEntity<>(job, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable UUID id, @RequestBody UpdateJob updateJob) {
        try {
            Job job = jobService.modifyJob(id, updateJob);
            return new ResponseEntity<>(job, HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable UUID id) {
        try {
            if (jobService.removeJob(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJob(@PathVariable UUID id) {
        try {
            Job job = jobService.getJobById(id);
            return new ResponseEntity<>(job, HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Page<Job>> searchJobs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String queryParams) {

        try {

            // TODO build a string parser to queryParameters
            Page<Job> jobs = jobService.searchJobs(page, pageSize, null);
            return new ResponseEntity<>(jobs, HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}