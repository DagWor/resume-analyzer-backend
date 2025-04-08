package com.example.resumeanalyzer.controller;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:5176") // Allow CORS for this specific endpoint
public class FileUploadController {

    private final Tika tika;

    public FileUploadController() {
        this.tika = new Tika();
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Extract text from PDF using Apache Tika
            InputStream inputStream = file.getInputStream();
            String extractedText = extractTextFromPDF(inputStream);

            if (extractedText == null || extractedText.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No text found in the PDF. Please upload a valid resume.");
            }

            // Basic resume analysis (e.g., check for skills or key information)
            String analysisResult = analyzeResume(extractedText);

            return ResponseEntity.ok(analysisResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    private String extractTextFromPDF(InputStream inputStream) throws Exception {
        // Parse the PDF file to extract text
        PDFParser pdfParser = new PDFParser();
        BodyContentHandler contentHandler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext parseContext = new ParseContext();
        pdfParser.parse(inputStream, contentHandler, metadata, parseContext);

        return contentHandler.toString();
    }

    private String analyzeResume(String extractedText) {
        StringBuilder feedback = new StringBuilder();

        // Check for key resume sections
        if (extractedText.contains("Skills") || extractedText.contains("Technical Skills")) {
            feedback.append("Skills section found.\n");
        } else {
            feedback.append("No Skills section detected. Consider adding a 'Skills' section.\n");
        }

        if (extractedText.contains("Experience") || extractedText.contains("Work Experience")) {
            feedback.append("Experience section found.\n");
        } else {
            feedback.append("No Experience section detected. Consider adding a 'Work Experience' section.\n");
        }

        if (extractedText.contains("Education")) {
            feedback.append("Education section found.\n");
        } else {
            feedback.append("No Education section detected. Consider adding an 'Education' section.\n");
        }

        // Basic Keyword Analysis (Example: Java as a keyword)
        if (extractedText.contains("Java")) {
            feedback.append("Resume mentions Java, a commonly sought-after skill.\n");
        } else {
            feedback.append("Consider adding Java to the skills section.\n");
        }

        return feedback.toString();
    }


}
