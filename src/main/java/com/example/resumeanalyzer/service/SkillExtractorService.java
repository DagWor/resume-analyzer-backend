package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.model.Resume;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SkillExtractorService {

    private static final List<String> KNOWN_SKILLS = Arrays.asList(
            "java", "spring boot", "react", "javascript", "typescript",
            "node.js", "python", "aws", "docker", "kubernetes",
            "sql", "mysql", "postgresql", "mongodb", "git", "jenkins"
    );

    public List<String> extractSkills(String resumeText) {
        List<String> extractedSkills = new ArrayList<>();
        String text = resumeText.toLowerCase();

        for (String skill : KNOWN_SKILLS) {
            if (text.contains(skill.toLowerCase())) {
                extractedSkills.add(skill);
            }
        }
        return extractedSkills;
    }
}
