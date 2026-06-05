package com.example.airesume.exception;

public class WorkflowExpiredException extends RuntimeException {
    public WorkflowExpiredException(String message) {
        super(message);
    }
}
