package com.example.resumeanalyzer.repository;

import com.example.resumeanalyzer.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByExtractedTextContainingIgnoreCase(String keyword);

    List<Resume> findBySkillsIgnoreCase(String skill);
}
