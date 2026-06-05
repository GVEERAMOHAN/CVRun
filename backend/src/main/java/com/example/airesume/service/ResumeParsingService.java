package com.example.airesume.service;

import com.example.airesume.exception.InvalidFileException;
import com.example.airesume.model.ResumeData;
import com.example.airesume.parser.DocxResumeParser;
import com.example.airesume.parser.PdfResumeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ResumeParsingService {
    private static final Logger log = LoggerFactory.getLogger(ResumeParsingService.class);

    private final PdfResumeParser pdfParser;
    private final DocxResumeParser docxParser;

    public ResumeParsingService(PdfResumeParser pdfParser, DocxResumeParser docxParser) {
        this.pdfParser = pdfParser;
        this.docxParser = docxParser;
    }

    public ResumeData parse(String filename, byte[] fileBytes) {
        if (filename == null || filename.isBlank()) {
            throw new InvalidFileException("Filename is empty or invalid");
        }

        String ext = getFileExtension(filename).toLowerCase();
        log.info("Parsing resume: {} with extension: {}", filename, ext);
        
        try {
            if ("pdf".equals(ext)) {
                return pdfParser.parse(fileBytes);
            } else if ("docx".equals(ext)) {
                return docxParser.parse(fileBytes);
            } else {
                throw new InvalidFileException("Unsupported file type: ." + ext + ". Please upload .pdf or .docx files.");
            }
        } catch (InvalidFileException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse file: {}", filename, e);
            throw new RuntimeException("Failed to parse resume: " + e.getMessage(), e);
        }
    }

    private String getFileExtension(String filename) {
        int dotIdx = filename.lastIndexOf('.');
        if (dotIdx == -1 || dotIdx == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIdx + 1);
    }
}
