package com.example.resumeanalyzer.controller;

import com.example.resumeanalyzer.model.Resume;
import com.example.resumeanalyzer.repository.ResumeRepository;
import com.example.resumeanalyzer.service.ResumeParserService;
import com.example.resumeanalyzer.service.SkillExtractorService;
import org.apache.tika.exception.TikaException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads/";
    private final ResumeParserService resumeParserService;
    private final ResumeRepository resumeRepository;
    private final SkillExtractorService skillExtractorService;

    public FileUploadController(ResumeParserService resumeParserService, ResumeRepository resumeRepository, SkillExtractorService skillExtractorService) {
        this.resumeParserService = resumeParserService;
        this.resumeRepository = resumeRepository;
        this.skillExtractorService = skillExtractorService;
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file type
            if (!file.getContentType().equals("application/pdf") &&
                    !file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                return ResponseEntity.badRequest().body("Only PDF and DOCX files are allowed!");
            }

            // Ensure directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the file
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            Files.write(filePath, file.getBytes());

            // Extract text from the resume
            File uploadedFile = filePath.toFile();
            String extractedText = resumeParserService.extractText(uploadedFile);

            // extract skills
            List<String> extractedSkills = skillExtractorService.extractSkills(extractedText);

            // Save to database
            Resume resume = new Resume();
            resume.setFileName(file.getOriginalFilename());
            resume.setExtractedText(extractedText);
            resume.setSkills(extractedSkills);
            resumeRepository.save(resume);

            return ResponseEntity.ok("Resume uploaded and processed successfully!");
        } catch (IOException | TikaException | SAXException e) {
            return ResponseEntity.internalServerError().body("Error processing the file: " + e.getMessage());
        }
    }
}
