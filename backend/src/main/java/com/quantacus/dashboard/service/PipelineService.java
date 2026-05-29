package com.quantacus.dashboard.service;

import com.quantacus.dashboard.entity.Job;
import com.quantacus.dashboard.entity.Product;
import com.quantacus.dashboard.enums.JobStatus;
import com.quantacus.dashboard.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PipelineService {

    private final JobService jobService;
    private final ExtractionService extractionService;
    private final ValidationService validationService;
    private final TitleEnhancementService titleEnhancementService;
    private final CompetitorPriceService competitorPriceService;
    private final ProductRepository productRepository;

    // -------------------------------------------------------------------------
    // Entry points (both are @Async so they run off the Tomcat request thread)
    // -------------------------------------------------------------------------

    // Triggered immediately after a video upload.
    // Runs extraction; if confidence is high enough the full pipeline continues,
    // otherwise the job waits in EXTRACTION_INCOMPLETE for user fallback.
    @Async("pipelineExecutor")
    public void runForVideoUpload(UUID jobId) {
        log.info("Pipeline started — job {}", jobId);
        try {
            Job job = jobService.updateStatus(jobId, JobStatus.EXTRACTING);

            ExtractionService.ExtractionResult result = extractionService.extract(job);
            List<Product> saved = productRepository.saveAll(result.products());

            if (result.incomplete()) {
                jobService.updateStatus(jobId, JobStatus.EXTRACTION_INCOMPLETE);
                log.info("Job {} extraction incomplete — pipeline paused, awaiting user fallback", jobId);
                return;
            }

            runFromValidation(jobId, saved);

        } catch (Exception e) {
            log.error("Pipeline failed — job {}", jobId, e);
            jobService.fail(jobId, e.getMessage());
        }
    }

    // Triggered after the user provides a CSV or manual product data.
    // Reloads products fresh from the DB to avoid detached-entity issues,
    // then continues the pipeline from the validation stage.
    @Async("pipelineExecutor")
    public void resumeAfterFallback(UUID jobId) {
        log.info("Pipeline resuming after fallback — job {}", jobId);
        try {
            List<Product> products = productRepository.findByJobId(jobId);
            runFromValidation(jobId, products);
        } catch (Exception e) {
            log.error("Pipeline failed after fallback — job {}", jobId, e);
            jobService.fail(jobId, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Shared pipeline stages (validation → title enhancement → prices → done)
    // -------------------------------------------------------------------------

    // This method is private and called only from within the same bean,
    // so @Async does not apply — it runs in whichever thread the caller is on.
    private void runFromValidation(UUID jobId, List<Product> products) {

        // Stage: Validate
        Job job = jobService.updateStatus(jobId, JobStatus.VALIDATING);
        validationService.validateAndScore(products, job);

        // Stage: Title enhancement (only if the user opted in at upload time)
        if (job.isEnhanceTitle()) {
            jobService.updateStatus(jobId, JobStatus.ENHANCING_TITLE);
            for (Product product : products) {
                String enhanced = titleEnhancementService.enhance(product);
                product.setEnhancedTitle(enhanced);
                productRepository.save(product);
            }
        }

        // Stage: Competitor prices
        jobService.updateStatus(jobId, JobStatus.FETCHING_PRICES);
        for (Product product : products) {
            competitorPriceService.simulateAndSave(product);
        }

        // Done
        jobService.updateStatus(jobId, JobStatus.COMPLETED);
        log.info("Pipeline completed — job {}", jobId);
    }
}
