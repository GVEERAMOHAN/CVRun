package com.example.airesume.parser;

import com.example.airesume.model.ResumeData;

public interface ResumeParser {
    ResumeData parse(byte[] fileBytes) throws Exception;
}
