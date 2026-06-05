import React, { createContext, useContext, useState, useEffect } from 'react';
import apiClient from '../../api/apiClient';

export interface Project {
  id?: string;
  title: string;
  description: string;
  bullets: string[];
  techStack: string[];
  domainTags: string[];
  source?: string;
}

export interface Experience {
  id?: string;
  company: string;
  role: string;
  location: string;
  startDate: string;
  endDate: string;
  bullets: string[];
  techStack: string[];
  source?: string;
}

export interface Education {
  id?: string;
  institution: string;
  degree: string;
  fieldOfStudy: string;
  startDate: string;
  endDate: string;
  grade: string;
  location: string;
  source?: string;
}

export interface Certification {
  id?: string;
  name: string;
  issuingOrganization: string;
  issueDate: string;
  credentialUrl: string;
  source?: string;
}

export interface Achievement {
  id?: string;
  title: string;
  description: string;
  date: string;
  source?: string;
}

export interface Link {
  id?: string;
  label: string;
  url: string;
}

export interface ResumeData {
  name: string;
  email: string;
  phone: string;
  location: string;
  summary: string;
  skills: string[];
  projects: Project[];
  experience: Experience[];
  education: Education[];
  certifications: Certification[];
  achievements: Achievement[];
  links: Link[];
}

export interface JobDescription {
  rawText: string;
  keywords: string[];
  requiredSkills: string[];
  responsibilities: string[];
  roleEmphasis: string;
}

export interface Suggestion {
  id: string;
  originalItemId?: string;
  section: 'skills' | 'projects' | 'experience' | 'achievements' | 'certifications';
  title: string;
  subtitle?: string;
  description?: string;
  location?: string;
  startDate?: string;
  endDate?: string;
  grade?: string;
  bullets: string[];
  techStack: string[];
  credentialUrl?: string;
  score: number;
  reason: string;
  source: string;
  approved: boolean;
}

export interface ApprovedContent {
  skills: string[];
  projects: Project[];
  experience: Experience[];
  education: Education[];
  certifications: Certification[];
  achievements: Achievement[];
  links: Link[];
}

export interface GeneratedResume {
  plainText: string;
  generatedAt: string;
}

export type WorkflowStatus =
  | 'CREATED'
  | 'RESUME_UPLOADED'
  | 'PROFILE_MERGED'
  | 'JD_ANALYZED'
  | 'SUGGESTIONS_READY'
  | 'USER_REVIEWED'
  | 'RESUME_GENERATED'
  | 'EXPORTED'
  | 'CLEARED';

interface WorkflowContextType {
  step: number;
  workflowId: string | null;
  status: WorkflowStatus;
  resumeData: ResumeData;
  manualData: ResumeData;
  mergedProfile: ResumeData;
  jobDescription: JobDescription;
  suggestions: Suggestion[];
  approvedContent: ApprovedContent;
  generatedResume: GeneratedResume | null;
  loading: boolean;
  error: string | null;
  timeLeft: number; // in seconds
  uploadResume: (file: File) => Promise<void>;
  mergeProfile: (manualData: ResumeData) => Promise<void>;
  analyzeJd: (jdText: string) => Promise<void>;
  updateSuggestions: (suggestions: Suggestion[]) => void;
  approveSuggestions: () => Promise<void>;
  saveApprovedContent: (approved: ApprovedContent) => Promise<void>;
  generateResume: () => Promise<void>;
  resetWorkflow: () => Promise<void>;
  nextStep: () => void;
  prevStep: () => void;
  goToStep: (step: number) => void;
  clearError: () => void;
}

const defaultResumeData = (): ResumeData => ({
  name: '',
  email: '',
  phone: '',
  location: '',
  summary: '',
  skills: [],
  projects: [],
  experience: [],
  education: [],
  certifications: [],
  achievements: [],
  links: [],
});

const defaultJobDescription = (): JobDescription => ({
  rawText: '',
  keywords: [],
  requiredSkills: [],
  responsibilities: [],
  roleEmphasis: '',
});

const defaultApprovedContent = (): ApprovedContent => ({
  skills: [],
  projects: [],
  experience: [],
  education: [],
  certifications: [],
  achievements: [],
  links: [],
});

const WorkflowContext = createContext<WorkflowContextType | undefined>(undefined);

export const WorkflowProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [step, setStep] = useState<number>(1);
  const [workflowId, setWorkflowId] = useState<string | null>(null);
  const [status, setStatus] = useState<WorkflowStatus>('CREATED');
  const [resumeData, setResumeData] = useState<ResumeData>(defaultResumeData());
  const [manualData, setManualData] = useState<ResumeData>(defaultResumeData());
  const [mergedProfile, setMergedProfile] = useState<ResumeData>(defaultResumeData());
  const [jobDescription, setJobDescription] = useState<JobDescription>(defaultJobDescription());
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  const [approvedContent, setApprovedContent] = useState<ApprovedContent>(defaultApprovedContent());
  const [generatedResume, setGeneratedResume] = useState<GeneratedResume | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [timeLeft, setTimeLeft] = useState<number>(1800); // 30 minutes in seconds

  // Handle TTL countdown
  useEffect(() => {
    if (!workflowId) return;

    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          setError('Your session has expired. Please start over.');
          handleResetState();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [workflowId]);

  const handleResetState = () => {
    setStep(1);
    setWorkflowId(null);
    setStatus('CLEARED');
    setResumeData(defaultResumeData());
    setManualData(defaultResumeData());
    setMergedProfile(defaultResumeData());
    setJobDescription(defaultJobDescription());
    setSuggestions([]);
    setApprovedContent(defaultApprovedContent());
    setGeneratedResume(null);
    setTimeLeft(1800);
  };

  const clearError = () => setError(null);

  const syncWorkflowState = (wf: any) => {
    setWorkflowId(wf.workflowId);
    setStatus(wf.status);
    if (wf.resumeData) setResumeData(wf.resumeData);
    if (wf.manualData) setManualData(wf.manualData);
    if (wf.mergedProfile) setMergedProfile(wf.mergedProfile);
    if (wf.jobDescription) setJobDescription(wf.jobDescription);
    if (wf.suggestions) setSuggestions(wf.suggestions);
    if (wf.approvedContent) setApprovedContent(wf.approvedContent);
    if (wf.generatedResume) setGeneratedResume(wf.generatedResume);
    setTimeLeft(1800); // Reset client countdown on successful sync
  };

  const uploadResume = async (file: File) => {
    setLoading(true);
    setError(null);
    try {
      const data = await apiClient.uploadResume(file);
      setWorkflowId(data.workflowId);
      setStatus(data.status);
      setResumeData(data.resumeData);
      setMergedProfile(data.resumeData);
      setTimeLeft(1800);
      setStep(2);
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to upload and parse resume. Please check file format.');
    } finally {
      setLoading(false);
    }
  };

  const mergeProfile = async (data: ResumeData) => {
    if (!workflowId) return;
    setLoading(true);
    setError(null);
    try {
      setManualData(data);
      const res = await apiClient.mergeProfile(workflowId, data);
      syncWorkflowState(res);
      setStep(3);
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to merge profile data.');
    } finally {
      setLoading(false);
    }
  };

  const analyzeJd = async (jdText: string) => {
    if (!workflowId) return;
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.analyzeJd(workflowId, jdText);
      syncWorkflowState(res);
      setStep(4);
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to analyze Job Description.');
    } finally {
      setLoading(false);
    }
  };

  const updateSuggestions = (sugs: Suggestion[]) => {
    setSuggestions(sugs);
  };

  const approveSuggestions = async () => {
    if (!workflowId) return;
    setLoading(true);
    setError(null);
    try {
      // Build approved content structure based on approved suggestions
      const approved: ApprovedContent = defaultApprovedContent();
      
      // Skills
      approved.skills = suggestions
        .filter((s) => s.section === 'skills' && s.approved)
        .map((s) => s.title);

      // Projects
      const approvedProjIds = suggestions
        .filter((s) => s.section === 'projects' && s.approved)
        .map((s) => s.originalItemId);
      approved.projects = mergedProfile.projects.filter((p) => approvedProjIds.includes(p.id));

      // Experience
      const approvedExpIds = suggestions
        .filter((s) => s.section === 'experience' && s.approved)
        .map((s) => s.originalItemId);
      approved.experience = mergedProfile.experience.filter((e) => approvedExpIds.includes(e.id));

      // Achievements
      const approvedAchIds = suggestions
        .filter((s) => s.section === 'achievements' && s.approved)
        .map((s) => s.originalItemId);
      approved.achievements = mergedProfile.achievements.filter((a) => approvedAchIds.includes(a.id));

      // Certifications
      const approvedCertIds = suggestions
        .filter((s) => s.section === 'certifications' && s.approved)
        .map((s) => s.originalItemId);
      approved.certifications = mergedProfile.certifications.filter((c) => approvedCertIds.includes(c.id));

      // Links & Education - keep all by default as they represent standard profile info
      approved.education = mergedProfile.education;
      approved.links = mergedProfile.links;

      const res = await apiClient.approveContent(workflowId, approved);
      syncWorkflowState(res);
      setStep(5);
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to approve suggestions.');
    } finally {
      setLoading(false);
    }
  };

  const saveApprovedContent = async (approved: ApprovedContent) => {
    if (!workflowId) return;
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.approveContent(workflowId, approved);
      syncWorkflowState(res);
      setStep(6);
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to save approved content.');
    } finally {
      setLoading(false);
    }
  };

  const generateResume = async () => {
    if (!workflowId) return;
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.generateResume(workflowId);
      syncWorkflowState(res);
      setStep(7);
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to generate tailored resume.');
    } finally {
      setLoading(false);
    }
  };

  const resetWorkflow = async () => {
    setError(null);
    if (workflowId) {
      try {
        await apiClient.resetWorkflow(workflowId);
      } catch (err) {
        console.error('Failed to reset workflow in backend', err);
      }
    }
    handleResetState();
  };

  const nextStep = () => setStep((prev) => Math.min(prev + 1, 7));
  const prevStep = () => setStep((prev) => Math.max(prev - 1, 1));
  const goToStep = (s: number) => {
    // Only allow navigating backward, or forward if data allows
    if (s < step) {
      setStep(s);
    } else if (s === 2 && workflowId) {
      setStep(2);
    } else if (s === 3 && status !== 'CREATED' && status !== 'RESUME_UPLOADED') {
      setStep(3);
    } else if (s === 4 && status !== 'CREATED' && status !== 'RESUME_UPLOADED' && status !== 'PROFILE_MERGED') {
      setStep(4);
    }
  };

  return (
    <WorkflowContext.Provider
      value={{
        step,
        workflowId,
        status,
        resumeData,
        manualData,
        mergedProfile,
        jobDescription,
        suggestions,
        approvedContent,
        generatedResume,
        loading,
        error,
        timeLeft,
        uploadResume,
        mergeProfile,
        analyzeJd,
        updateSuggestions,
        approveSuggestions,
        saveApprovedContent,
        generateResume,
        resetWorkflow,
        nextStep,
        prevStep,
        goToStep,
        clearError,
      }}
    >
      {children}
    </WorkflowContext.Provider>
  );
};

export const useWorkflow = () => {
  const context = useContext(WorkflowContext);
  if (!context) {
    throw new Error('useWorkflow must be used within a WorkflowProvider');
  }
  return context;
};
