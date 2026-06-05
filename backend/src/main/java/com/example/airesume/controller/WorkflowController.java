package com.example.airesume.controller;

import com.example.airesume.dto.*;
import com.example.airesume.model.*;
import com.example.airesume.cache.WorkflowCache;
import com.example.airesume.service.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class WorkflowController {
    private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);

    private final ResumeParsingService parsingService;
    private final ProfileMergeService mergeService;
    private final JobDescriptionAnalysisService jdService;
    private final SuggestionRankingService rankingService;
    private final ResumeGenerationService generationService;
    private final TextExportService exportService;
    private final PdfExportService pdfExportService;
    private final WorkflowCache cache;

    public WorkflowController(
            ResumeParsingService parsingService,
            ProfileMergeService mergeService,
            JobDescriptionAnalysisService jdService,
            SuggestionRankingService rankingService,
            ResumeGenerationService generationService,
            TextExportService exportService,
            PdfExportService pdfExportService,
            WorkflowCache cache) {
        this.parsingService = parsingService;
        this.mergeService = mergeService;
        this.jdService = jdService;
        this.rankingService = rankingService;
        this.generationService = generationService;
        this.exportService = exportService;
        this.pdfExportService = pdfExportService;
        this.cache = cache;
    }

    // 1. Upload Resume
    @PostMapping(value = "/upload-resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadResume(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String filename = file.getOriginalFilename();
        log.info("Received resume upload: {}", filename);
        
        try {
            byte[] bytes = file.getBytes();
            ResumeData parsedData = parsingService.parse(filename, bytes);
            
            String workflowId = "wf_" + UUID.randomUUID().toString().substring(0, 8);
            Workflow workflow = new Workflow(workflowId);
            workflow.setResumeData(parsedData);
            workflow.setMergedProfile(parsedData); // Initially merged profile equals parsed profile
            workflow.setStatus(WorkflowStatus.RESUME_UPLOADED);
            
            cache.put(workflowId, workflow);
            
            UploadResponse response = new UploadResponse(workflowId, workflow.getStatus(), parsedData);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload and parse resume: " + e.getMessage(), e);
        }
    }

    // 2. Merge Profile
    @PostMapping("/merge-profile")
    public ResponseEntity<Workflow> mergeProfile(@Valid @RequestBody MergeProfileRequest request) {
        log.info("Request to merge profile for workflow: {}", request.getWorkflowId());
        Workflow workflow = cache.get(request.getWorkflowId());
        
        workflow.setManualData(request.getManualData());
        ResumeData merged = mergeService.merge(workflow.getResumeData(), request.getManualData());
        workflow.setMergedProfile(merged);
        workflow.setStatus(WorkflowStatus.PROFILE_MERGED);
        
        cache.put(workflow.getWorkflowId(), workflow);
        return ResponseEntity.ok(workflow);
    }

    // 3. Analyze JD
    @PostMapping("/analyze-jd")
    public ResponseEntity<Workflow> analyzeJd(@Valid @RequestBody JdAnalysisRequest request) {
        log.info("Request to analyze Job Description for workflow: {}", request.getWorkflowId());
        Workflow workflow = cache.get(request.getWorkflowId());
        
        JobDescription analyzedJd = jdService.analyze(request.getJdText());
        workflow.setJobDescription(analyzedJd);
        workflow.setStatus(WorkflowStatus.JD_ANALYZED);
        
        // Auto-trigger suggestions generation and ranking as soon as JD is analyzed
        List<Suggestion> suggestions = rankingService.generateSuggestions(workflow.getMergedProfile(), analyzedJd);
        workflow.setSuggestions(suggestions);
        workflow.setStatus(WorkflowStatus.SUGGESTIONS_READY);
        
        cache.put(workflow.getWorkflowId(), workflow);
        return ResponseEntity.ok(workflow);
    }

    // 4. Get Suggestions (Also triggers ranking if needed, but normally handled in analyze-jd)
    @PostMapping("/suggestions")
    public ResponseEntity<Workflow> getSuggestions(@RequestBody Map<String, String> body) {
        String workflowId = body.get("workflowId");
        if (workflowId == null || workflowId.isBlank()) {
            throw new IllegalArgumentException("Workflow ID is required");
        }
        
        log.info("Request suggestions for workflow: {}", workflowId);
        Workflow workflow = cache.get(workflowId);
        
        List<Suggestion> suggestions = rankingService.generateSuggestions(workflow.getMergedProfile(), workflow.getJobDescription());
        workflow.setSuggestions(suggestions);
        workflow.setStatus(WorkflowStatus.SUGGESTIONS_READY);
        
        cache.put(workflowId, workflow);
        return ResponseEntity.ok(workflow);
    }

    // 5. Approve Content
    @PostMapping("/approve-content")
    public ResponseEntity<Workflow> approveContent(@Valid @RequestBody ApprovalRequest request) {
        log.info("Request to approve content for workflow: {}", request.getWorkflowId());
        Workflow workflow = cache.get(request.getWorkflowId());
        
        workflow.setApprovedContent(request.getApprovedContent());
        workflow.setStatus(WorkflowStatus.USER_REVIEWED);
        
        cache.put(workflow.getWorkflowId(), workflow);
        return ResponseEntity.ok(workflow);
    }

    // 6. Generate Resume
    @PostMapping("/generate")
    public ResponseEntity<Workflow> generateResume(@RequestBody Map<String, String> body) {
        String workflowId = body.get("workflowId");
        if (workflowId == null || workflowId.isBlank()) {
            throw new IllegalArgumentException("Workflow ID is required");
        }
        
        log.info("Request to generate resume for workflow: {}", workflowId);
        Workflow workflow = cache.get(workflowId);
        
        GeneratedResume gen = generationService.generate(workflow.getMergedProfile(), workflow.getApprovedContent(), workflow.getJobDescription());
        workflow.setGeneratedResume(gen);
        workflow.setStatus(WorkflowStatus.RESUME_GENERATED);
        
        cache.put(workflowId, workflow);
        return ResponseEntity.ok(workflow);
    }

    // 7. Preview Resume
    @GetMapping("/preview/{workflowId}")
    public ResponseEntity<GeneratedResume> previewResume(@PathVariable("workflowId") String workflowId) {
        log.info("Request to preview resume for workflow: {}", workflowId);
        Workflow workflow = cache.get(workflowId);
        
        if (workflow.getGeneratedResume() == null) {
            throw new IllegalStateException("Resume not generated yet.");
        }
        
        return ResponseEntity.ok(workflow.getGeneratedResume());
    }

    // 8. Export Text
    @GetMapping("/export-text")
    public ResponseEntity<byte[]> exportText(@RequestParam("workflowId") String workflowId) {
        log.info("Request to export plain text for workflow: {}", workflowId);
        Workflow workflow = cache.get(workflowId);
        
        byte[] bytes = exportService.exportToPlainText(workflow);
        workflow.setStatus(WorkflowStatus.EXPORTED);
        cache.put(workflowId, workflow);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "tailored_resume.txt");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    // 8b. Export PDF
    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestParam("workflowId") String workflowId) {
        log.info("Request to export PDF for workflow: {}", workflowId);
        Workflow workflow = cache.get(workflowId);
        
        byte[] bytes = pdfExportService.exportToPdf(workflow);
        workflow.setStatus(WorkflowStatus.EXPORTED);
        cache.put(workflowId, workflow);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "tailored_resume.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    // 9. Reset Workflow
    @DeleteMapping("/reset/{workflowId}")
    public ResponseEntity<Map<String, String>> resetWorkflow(@PathVariable("workflowId") String workflowId) {
        log.info("Request to reset workflow: {}", workflowId);
        cache.remove(workflowId);
        return ResponseEntity.ok(Map.of("status", "CLEARED"));
    }
}
