package com.example.airesume.service;

import com.example.airesume.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProfileMergeService {

    public ResumeData merge(ResumeData parsed, ResumeData manual) {
        ResumeData merged = new ResumeData();
        
        // 1. Personal Info - Prefer Manual if non-empty, otherwise Parsed
        merged.setName(selectPreferred(manual.getName(), parsed.getName(), "John Doe"));
        merged.setEmail(selectPreferred(manual.getEmail(), parsed.getEmail(), ""));
        merged.setPhone(selectPreferred(manual.getPhone(), parsed.getPhone(), ""));
        merged.setLocation(selectPreferred(manual.getLocation(), parsed.getLocation(), ""));
        merged.setSummary(selectPreferred(manual.getSummary(), parsed.getSummary(), ""));

        // 2. Skills - Merge lists, deduplicate case-insensitively
        merged.setSkills(mergeSkills(parsed.getSkills(), manual.getSkills()));

        // 3. Projects
        merged.setProjects(mergeProjects(parsed.getProjects(), manual.getProjects()));

        // 4. Experience
        merged.setExperience(mergeExperiences(parsed.getExperience(), manual.getExperience()));

        // 5. Education
        merged.setEducation(mergeEducation(parsed.getEducation(), manual.getEducation()));

        // 6. Certifications
        merged.setCertifications(mergeCertifications(parsed.getCertifications(), manual.getCertifications()));

        // 7. Achievements
        merged.setAchievements(mergeAchievements(parsed.getAchievements(), manual.getAchievements()));

        // 8. Links
        merged.setLinks(mergeLinks(parsed.getLinks(), manual.getLinks()));

        return merged;
    }

    private String selectPreferred(String manualVal, String parsedVal, String defaultVal) {
        if (manualVal != null && !manualVal.isBlank()) {
            return manualVal.trim();
        }
        if (parsedVal != null && !parsedVal.isBlank()) {
            return parsedVal.trim();
        }
        return defaultVal;
    }

    private List<String> mergeSkills(List<String> parsed, List<String> manual) {
        Set<String> seen = new HashSet<>();
        List<String> result = new ArrayList<>();
        
        // Manual skills get priority on ordering and case preservation
        if (manual != null) {
            for (String s : manual) {
                if (s != null && !s.isBlank()) {
                    String skill = s.trim();
                    if (seen.add(skill.toLowerCase())) {
                        result.add(skill);
                    }
                }
            }
        }
        if (parsed != null) {
            for (String s : parsed) {
                if (s != null && !s.isBlank()) {
                    String skill = s.trim();
                    if (seen.add(skill.toLowerCase())) {
                        result.add(skill);
                    }
                }
            }
        }
        return result;
    }

    private List<Project> mergeProjects(List<Project> parsed, List<Project> manual) {
        Map<String, Project> manualMap = new LinkedHashMap<>();
        if (manual != null) {
            for (Project p : manual) {
                if (p.getId() == null || p.getId().isBlank()) {
                    p.setId(UUID.randomUUID().toString());
                }
                p.setSource("manual");
                manualMap.put(p.getTitle().toLowerCase().trim(), p);
            }
        }

        List<Project> mergedList = new ArrayList<>();
        if (parsed != null) {
            for (Project p : parsed) {
                String key = p.getTitle().toLowerCase().trim();
                if (manualMap.containsKey(key)) {
                    // Match found: Merge details, prefer manual text
                    Project manualProject = manualMap.get(key);
                    Project mergedProj = new Project(
                        manualProject.getId(),
                        manualProject.getTitle(),
                        selectPreferred(manualProject.getDescription(), p.getDescription(), ""),
                        manualProject.getBullets().isEmpty() ? p.getBullets() : manualProject.getBullets(),
                        manualProject.getTechStack().isEmpty() ? p.getTechStack() : manualProject.getTechStack(),
                        manualProject.getDomainTags().isEmpty() ? p.getDomainTags() : manualProject.getDomainTags(),
                        "both"
                    );
                    mergedList.add(mergedProj);
                    manualMap.remove(key);
                } else {
                    p.setSource("resume");
                    if (p.getId() == null || p.getId().isBlank()) {
                        p.setId(UUID.randomUUID().toString());
                    }
                    mergedList.add(p);
                }
            }
        }

        // Add remaining manual projects
        mergedList.addAll(manualMap.values());
        return mergedList;
    }

    private List<Experience> mergeExperiences(List<Experience> parsed, List<Experience> manual) {
        Map<String, Experience> manualMap = new LinkedHashMap<>();
        if (manual != null) {
            for (Experience e : manual) {
                if (e.getId() == null || e.getId().isBlank()) {
                    e.setId(UUID.randomUUID().toString());
                }
                e.setSource("manual");
                manualMap.put(e.getCompany().toLowerCase().trim() + "|" + e.getRole().toLowerCase().trim(), e);
            }
        }

        List<Experience> mergedList = new ArrayList<>();
        if (parsed != null) {
            for (Experience e : parsed) {
                String key = e.getCompany().toLowerCase().trim() + "|" + e.getRole().toLowerCase().trim();
                if (manualMap.containsKey(key)) {
                    Experience manualExp = manualMap.get(key);
                    Experience mergedExp = new Experience(
                        manualExp.getId(),
                        manualExp.getCompany(),
                        manualExp.getRole(),
                        selectPreferred(manualExp.getLocation(), e.getLocation(), ""),
                        selectPreferred(manualExp.getStartDate(), e.getStartDate(), ""),
                        selectPreferred(manualExp.getEndDate(), e.getEndDate(), ""),
                        manualExp.getBullets().isEmpty() ? e.getBullets() : manualExp.getBullets(),
                        manualExp.getTechStack().isEmpty() ? e.getTechStack() : manualExp.getTechStack(),
                        "both"
                    );
                    mergedList.add(mergedExp);
                    manualMap.remove(key);
                } else {
                    e.setSource("resume");
                    if (e.getId() == null || e.getId().isBlank()) {
                        e.setId(UUID.randomUUID().toString());
                    }
                    mergedList.add(e);
                }
            }
        }

        mergedList.addAll(manualMap.values());
        return mergedList;
    }

    private List<Education> mergeEducation(List<Education> parsed, List<Education> manual) {
        Map<String, Education> manualMap = new LinkedHashMap<>();
        if (manual != null) {
            for (Education edu : manual) {
                if (edu.getId() == null || edu.getId().isBlank()) {
                    edu.setId(UUID.randomUUID().toString());
                }
                edu.setSource("manual");
                manualMap.put(edu.getInstitution().toLowerCase().trim() + "|" + edu.getDegree().toLowerCase().trim(), edu);
            }
        }

        List<Education> mergedList = new ArrayList<>();
        if (parsed != null) {
            for (Education edu : parsed) {
                String key = edu.getInstitution().toLowerCase().trim() + "|" + edu.getDegree().toLowerCase().trim();
                if (manualMap.containsKey(key)) {
                    Education manualEdu = manualMap.get(key);
                    Education mergedEdu = new Education(
                        manualEdu.getId(),
                        manualEdu.getInstitution(),
                        manualEdu.getDegree(),
                        selectPreferred(manualEdu.getFieldOfStudy(), edu.getFieldOfStudy(), ""),
                        selectPreferred(manualEdu.getStartDate(), edu.getStartDate(), ""),
                        selectPreferred(manualEdu.getEndDate(), edu.getEndDate(), ""),
                        selectPreferred(manualEdu.getGrade(), edu.getGrade(), ""),
                        selectPreferred(manualEdu.getLocation(), edu.getLocation(), ""),
                        "both"
                    );
                    mergedList.add(mergedEdu);
                    manualMap.remove(key);
                } else {
                    edu.setSource("resume");
                    if (edu.getId() == null || edu.getId().isBlank()) {
                        edu.setId(UUID.randomUUID().toString());
                    }
                    mergedList.add(edu);
                }
            }
        }

        mergedList.addAll(manualMap.values());
        return mergedList;
    }

    private List<Certification> mergeCertifications(List<Certification> parsed, List<Certification> manual) {
        Map<String, Certification> manualMap = new LinkedHashMap<>();
        if (manual != null) {
            for (Certification c : manual) {
                if (c.getId() == null || c.getId().isBlank()) {
                    c.setId(UUID.randomUUID().toString());
                }
                c.setSource("manual");
                manualMap.put(c.getName().toLowerCase().trim(), c);
            }
        }

        List<Certification> mergedList = new ArrayList<>();
        if (parsed != null) {
            for (Certification c : parsed) {
                String key = c.getName().toLowerCase().trim();
                if (manualMap.containsKey(key)) {
                    Certification manualCert = manualMap.get(key);
                    Certification mergedCert = new Certification(
                        manualCert.getId(),
                        manualCert.getName(),
                        selectPreferred(manualCert.getIssuingOrganization(), c.getIssuingOrganization(), ""),
                        selectPreferred(manualCert.getIssueDate(), c.getIssueDate(), ""),
                        selectPreferred(manualCert.getCredentialUrl(), c.getCredentialUrl(), ""),
                        "both"
                    );
                    mergedList.add(mergedCert);
                    manualMap.remove(key);
                } else {
                    c.setSource("resume");
                    if (c.getId() == null || c.getId().isBlank()) {
                        c.setId(UUID.randomUUID().toString());
                    }
                    mergedList.add(c);
                }
            }
        }

        mergedList.addAll(manualMap.values());
        return mergedList;
    }

    private List<Achievement> mergeAchievements(List<Achievement> parsed, List<Achievement> manual) {
        Map<String, Achievement> manualMap = new LinkedHashMap<>();
        if (manual != null) {
            for (Achievement a : manual) {
                if (a.getId() == null || a.getId().isBlank()) {
                    a.setId(UUID.randomUUID().toString());
                }
                a.setSource("manual");
                manualMap.put(a.getTitle().toLowerCase().trim(), a);
            }
        }

        List<Achievement> mergedList = new ArrayList<>();
        if (parsed != null) {
            for (Achievement a : parsed) {
                String key = a.getTitle().toLowerCase().trim();
                if (manualMap.containsKey(key)) {
                    Achievement manualAch = manualMap.get(key);
                    Achievement mergedAch = new Achievement(
                        manualAch.getId(),
                        manualAch.getTitle(),
                        selectPreferred(manualAch.getDescription(), a.getDescription(), ""),
                        selectPreferred(manualAch.getDate(), a.getDate(), ""),
                        "both"
                    );
                    mergedList.add(mergedAch);
                    manualMap.remove(key);
                } else {
                    a.setSource("resume");
                    if (a.getId() == null || a.getId().isBlank()) {
                        a.setId(UUID.randomUUID().toString());
                    }
                    mergedList.add(a);
                }
            }
        }

        mergedList.addAll(manualMap.values());
        return mergedList;
    }

    private List<Link> mergeLinks(List<Link> parsed, List<Link> manual) {
        Map<String, Link> manualMap = new LinkedHashMap<>();
        if (manual != null) {
            for (Link l : manual) {
                if (l.getId() == null || l.getId().isBlank()) {
                    l.setId(UUID.randomUUID().toString());
                }
                manualMap.put(l.getLabel().toLowerCase().trim(), l);
            }
        }

        List<Link> mergedList = new ArrayList<>();
        if (parsed != null) {
            for (Link l : parsed) {
                String key = l.getLabel().toLowerCase().trim();
                if (manualMap.containsKey(key)) {
                    mergedList.add(manualMap.get(key));
                    manualMap.remove(key);
                } else {
                    if (l.getId() == null || l.getId().isBlank()) {
                        l.setId(UUID.randomUUID().toString());
                    }
                    mergedList.add(l);
                }
            }
        }

        mergedList.addAll(manualMap.values());
        return mergedList;
    }
}
