package com.example.airesume.cache;

import com.example.airesume.exception.WorkflowExpiredException;
import com.example.airesume.exception.WorkflowNotFoundException;
import com.example.airesume.model.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WorkflowCacheService implements WorkflowCache {
    private static final Logger log = LoggerFactory.getLogger(WorkflowCacheService.class);
    
    private final Map<String, Workflow> cache = new ConcurrentHashMap<>();
    
    @Value("${app.workflow.cache.ttl-minutes:30}")
    private int ttlMinutes;

    @Override
    public Workflow get(String workflowId) {
        Workflow workflow = cache.get(workflowId);
        if (workflow == null) {
            throw new WorkflowNotFoundException("Workflow not found for ID: " + workflowId);
        }
        if (workflow.isExpired()) {
            cache.remove(workflowId);
            throw new WorkflowExpiredException("Workflow " + workflowId + " has expired");
        }
        // Touch workflow to slide the expiration window (optional, but let's extend expiration on access)
        workflow.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
        return workflow;
    }

    @Override
    public void put(String workflowId, Workflow workflow) {
        if (workflow != null) {
            workflow.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
            cache.put(workflowId, workflow);
        }
    }

    @Override
    public void remove(String workflowId) {
        cache.remove(workflowId);
    }

    @Override
    public boolean exists(String workflowId) {
        Workflow workflow = cache.get(workflowId);
        if (workflow == null) {
            return false;
        }
        if (workflow.isExpired()) {
            cache.remove(workflowId);
            return false;
        }
        return true;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Scheduled(fixedDelayString = "${app.workflow.cache.cleanup-interval-seconds:60}000")
    public void cleanExpiredWorkflows() {
        log.debug("Running eviction scheduler for expired workflows...");
        LocalDateTime now = LocalDateTime.now();
        cache.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();
            if (expired) {
                log.info("Evicting expired workflow: {}", entry.getKey());
            }
            return expired;
        });
    }
}
