package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.model.Resume;
import com.example.resumeanalyzer.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final ResumeRepository resumeRepository;

    public AnalyticsService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public Map<String, Integer> getTopSkills(int limit) {
        List<Resume> resumes = resumeRepository.findAll();
        Map<String, Integer> skillCounts = new HashMap<>();

        for (Resume resume : resumes) {
            if (resume.getSkills() != null) {
                for (String skill : resume.getSkills()) {
                    skillCounts.put(skill.toLowerCase(), skillCounts.getOrDefault(skill.toLowerCase(), 0) + 1);
                }
            }
        }

        // sort by value and return top N
        return skillCounts.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(limit)
                .collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll);
    }
}
