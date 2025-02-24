package com.gcg.djs.application.restapi.controllers;

import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.interfaces.services.IRecurringJobService;
import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.recurringjob.CreateRecurringJob;
import com.gcg.djs.domain.models.recurringjob.RecurringJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/recurring")
public class RecurringJobController {
    private final IRecurringJobService recurringJobService;

    @Autowired
    public RecurringJobController(IRecurringJobService recurringJobService) {
        this.recurringJobService = recurringJobService;
    }

    @PostMapping
    public ResponseEntity<RecurringJob> createJob(@RequestBody CreateRecurringJob createJob) {
        try {
            RecurringJob job = recurringJobService.createRecurringJob(createJob);
            return new ResponseEntity<>(job, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringJob> getRecurringJobById(@PathVariable UUID id) {
        try {
            RecurringJob job = recurringJobService.getRecurringJobById(id);
            return new ResponseEntity<>(job, HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Page<RecurringJob>> getRecurringJobs(
            @RequestParam int page,
            @RequestParam int pageSize) {
        try {
            // TODO build a string parser to queryParameters
            Page<RecurringJob> recurringJobs = recurringJobService.getRecurringJobsPage(
                    page, pageSize, null);
            return new ResponseEntity<>(recurringJobs, HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/next")
    public ResponseEntity<Page<RecurringJob>> getNextRunJobs(
            @RequestParam int page,
            @RequestParam int pageSize) {
        try {
            Page<RecurringJob> recurringJobs = recurringJobService.getNextRun(page, pageSize);
            return new ResponseEntity<>(recurringJobs, HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<Void> pauseRecurringJob(@PathVariable UUID id) {
        try {
            boolean success = recurringJobService.pauseJob(id);
            if (success) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
