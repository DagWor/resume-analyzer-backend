package com.example.resumeanalyzer.service;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class ResumeParserService {
    public String extractText(File file) throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        try (FileInputStream inputStream = new FileInputStream(file)) {
            if (file.getName().endsWith(".docx")) {
                OOXMLParser parser = new OOXMLParser();
                parser.parse(inputStream, handler, metadata, context);
            } else if (file.getName().endsWith(".pdf")) {
                PDFParser parser = new PDFParser();
                parser.parse(inputStream, handler, metadata, context);
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + file.getName());
            }
        }

        return  handler.toString();
    }
}
