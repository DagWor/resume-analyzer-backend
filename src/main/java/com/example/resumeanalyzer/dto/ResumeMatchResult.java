package com.example.resumeanalyzer.dto;

import com.example.resumeanalyzer.model.Resume;

import java.util.List;

public class ResumeMatchResult {
    private Resume resume;
    private int score;
    private List<String> matchedSkills;

    public ResumeMatchResult(Resume resume, int score, List<String> matchedSkills) {
        this.resume = resume;
        this.score = score;
        this.matchedSkills = matchedSkills;
    }

    public Resume getResume() {
        return resume;
    }

    public int getScore() {
        return score;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }
}
