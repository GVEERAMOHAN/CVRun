package com.example.airesume.service;

import com.example.airesume.model.Workflow;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfExportService {
    private static final Logger log = LoggerFactory.getLogger(PdfExportService.class);

    public byte[] exportToPdf(Workflow workflow) {
        if (workflow.getGeneratedResume() == null || workflow.getGeneratedResume().getPlainText() == null) {
            throw new IllegalStateException("No resume has been generated yet for this workflow.");
        }

        String text = workflow.getGeneratedResume().getPlainText();
        
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            float margin = 54; // 0.75 inch margin
            float yStart = 738; // 792 - 54
            float width = 504; // 612 - 108
            
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Use Helvetica from Standard14Fonts in PDFBox 3.x
            PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            
            float fontSize = 10;
            float leading = 14;
            
            contentStream.beginText();
            contentStream.setFont(fontNormal, fontSize);
            contentStream.newLineAtOffset(margin, yStart);
            
            float yPosition = yStart;
            String[] lines = text.split("\\r?\\n");
            
            for (String line : lines) {
                String sanitizedLine = sanitizeText(line);
                
                boolean boldHeader = isHeader(sanitizedLine);
                PDType1Font currentFont = boldHeader ? fontBold : fontNormal;
                float currentFontSize = boldHeader ? fontSize + 1 : fontSize;
                
                List<String> wrappedLines = wrapLine(sanitizedLine, currentFont, currentFontSize, width);
                
                for (String wLine : wrappedLines) {
                    if (yPosition < margin + leading) {
                        contentStream.endText();
                        contentStream.close();
                        
                        page = new PDPage(PDRectangle.LETTER);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        
                        contentStream.beginText();
                        contentStream.setFont(currentFont, currentFontSize);
                        contentStream.newLineAtOffset(margin, yStart);
                        yPosition = yStart;
                    }
                    
                    contentStream.setFont(currentFont, currentFontSize);
                    contentStream.showText(wLine);
                    contentStream.newLineAtOffset(0, -leading);
                    yPosition -= leading;
                }
            }
            
            contentStream.endText();
            contentStream.close();
            
            document.save(baos);
            return baos.toByteArray();
            
        } catch (IOException e) {
            log.error("Failed to generate PDF resume: ", e);
            throw new RuntimeException("Failed to generate PDF resume: " + e.getMessage(), e);
        }
    }
    
    private boolean isHeader(String line) {
        String trimmed = line.trim();
        return trimmed.equals("SUMMARY") || trimmed.equals("SKILLS") || trimmed.equals("EXPERIENCE") ||
               trimmed.equals("PROJECTS") || trimmed.equals("EDUCATION") || 
               trimmed.equals("CERTIFICATIONS & ACHIEVEMENTS") || trimmed.equals("CERTIFICATIONS");
    }
    
    private List<String> wrapLine(String text, PDType1Font font, float fontSize, float width) throws IOException {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            result.add("");
            return result;
        }

        int leadingSpaces = 0;
        while (leadingSpaces < text.length() && text.charAt(leadingSpaces) == ' ') {
            leadingSpaces++;
        }
        String prefix = " ".repeat(leadingSpaces);
        String trimmedText = text.substring(leadingSpaces);

        String[] words = trimmedText.split(" ");
        StringBuilder currentLine = new StringBuilder(prefix);
        
        for (String word : words) {
            if (word.isEmpty()) continue;
            
            String testLine = currentLine.toString().equals(prefix) ? currentLine + word : currentLine + " " + word;
            float testLineWidth = fontSize * font.getStringWidth(testLine) / 1000f;
            
            if (testLineWidth > width) {
                if (currentLine.length() > prefix.length()) {
                    result.add(currentLine.toString());
                    currentLine = new StringBuilder(prefix + word);
                } else {
                    result.add(prefix + word);
                }
            } else {
                currentLine.append(currentLine.toString().equals(prefix) ? "" : " ").append(word);
            }
        }
        
        if (currentLine.length() > prefix.length()) {
            result.add(currentLine.toString());
        }
        
        if (result.isEmpty()) {
            result.add("");
        }
        
        return result;
    }
    
    private String sanitizeText(String text) {
        if (text == null) return "";
        
        String result = text.replace("•", "*")
                            .replace("—", "-")
                            .replace("–", "-")
                            .replace("“", "\"")
                            .replace("”", "\"")
                            .replace("‘", "'")
                            .replace("’", "'");
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            char c = result.charAt(i);
            if ((c >= 32 && c <= 126) || c == '\t') {
                sb.append(c);
            } else {
                sb.append(' ');
            }
        }
        
        return sb.toString();
    }
}
