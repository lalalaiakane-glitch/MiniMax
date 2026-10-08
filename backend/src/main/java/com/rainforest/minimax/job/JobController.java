package com.rainforest.minimax.job;

import com.rainforest.minimax.job.dto.CreateJobRequest;
import com.rainforest.minimax.job.dto.JobResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*") // MVP 先全開，若對外上線再收斂
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public JobResponse create(@Valid @RequestBody CreateJobRequest request) {
        return JobResponse.from(jobService.createAndSubmit(request));
    }

    @GetMapping
    public List<JobResponse> list() {
        return jobService.listAll().stream().map(JobResponse::from).toList();
    }

    @GetMapping("/{id}")
    public JobResponse get(@PathVariable Long id) {
        return JobResponse.from(jobService.getAndRefresh(id));
    }
}
