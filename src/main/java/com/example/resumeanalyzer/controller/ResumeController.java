package com.example.resumeanalyzer.controller;

import com.example.resumeanalyzer.dto.JobDescriptionRequest;
import com.example.resumeanalyzer.dto.ResumeMatchResult;
import com.example.resumeanalyzer.model.Resume;
import com.example.resumeanalyzer.repository.ResumeRepository;
import com.example.resumeanalyzer.service.AnalyticsService;
import com.example.resumeanalyzer.service.SkillExtractorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeRepository resumeRepository;
    private final AnalyticsService analyticsService;
    private final SkillExtractorService skillExtractorService;

    public ResumeController(ResumeRepository resumeRepository, AnalyticsService analyticsService, SkillExtractorService skillExtractorService) {
        this.resumeRepository = resumeRepository;
        this.analyticsService = analyticsService;
        this.skillExtractorService = skillExtractorService;
    }

    // Search resumes by keyword
    @GetMapping("/search")
    public ResponseEntity<List<Resume>> searchResumes(@RequestParam("keyword") String keyword) {
        List<Resume> matchingResumes = resumeRepository.findByExtractedTextContainingIgnoreCase(keyword);
        return ResponseEntity.ok(matchingResumes);
    }

    // List all uploaded resumes

    @GetMapping()
    public ResponseEntity<List<Resume>> getAllResumes() {
        List<Resume> matchingResumes = resumeRepository.findAll();
        return ResponseEntity.ok(matchingResumes);
    }

    // fetch resume by ID
    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResumeById(@PathVariable Long id) {
        return resumeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/skills")
    public ResponseEntity<List<Resume>> getResumesBySkill(@RequestParam("skill") String skill) {
        List<Resume> matchingList = resumeRepository.findBySkillsIgnoreCase(skill);
        return ResponseEntity.ok(matchingList);
    }

    @GetMapping("/top-skills")
    public ResponseEntity<Map<String, Integer>> getTopSkill(@RequestParam(defaultValue = "10") int limit) {
        Map<String, Integer> topSkills = analyticsService.getTopSkills(limit);
        return ResponseEntity.ok(topSkills);
    }

    @PostMapping("/score")
    public ResponseEntity<List<ResumeMatchResult>> scoreResumes(@RequestBody JobDescriptionRequest jobRequest) {
        String jobText = jobRequest.getJobDescription().toLowerCase();

        List<String> jobSkills = skillExtractorService.extractSkills(jobText);
        List<Resume> allResumes = resumeRepository.findAll();
        List<ResumeMatchResult> results = new ArrayList<>();

        for (Resume resume : allResumes) {
            List<String> matchedSkills = new ArrayList<>();
            int matchCount = 0;

            for (String skill : resume.getSkills()) {
                if (jobSkills.contains(skill.toLowerCase())) {
                    matchedSkills.add(skill);
                    matchCount++;
                }
            }

            int score = (int) ((matchCount / (double) jobSkills.size()) * 100);
            results.add(new ResumeMatchResult(resume, score, matchedSkills));
        }

        results.sort((a, b) -> b.getScore() - a.getScore());

        return ResponseEntity.ok(results);
    }
}
