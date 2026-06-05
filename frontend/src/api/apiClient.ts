import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/workflow';

export const apiClient = {
  uploadResume: async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await axios.post(`${API_BASE_URL}/upload-resume`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  mergeProfile: async (workflowId: string, manualData: any) => {
    const response = await axios.post(`${API_BASE_URL}/merge-profile`, {
      workflowId,
      manualData,
    });
    return response.data;
  },

  analyzeJd: async (workflowId: string, jdText: string) => {
    const response = await axios.post(`${API_BASE_URL}/analyze-jd`, {
      workflowId,
      jdText,
    });
    return response.data;
  },

  getSuggestions: async (workflowId: string) => {
    const response = await axios.post(`${API_BASE_URL}/suggestions`, {
      workflowId,
    });
    return response.data;
  },

  approveContent: async (workflowId: string, approvedContent: any) => {
    const response = await axios.post(`${API_BASE_URL}/approve-content`, {
      workflowId,
      approvedContent,
    });
    return response.data;
  },

  generateResume: async (workflowId: string) => {
    const response = await axios.post(`${API_BASE_URL}/generate`, {
      workflowId,
    });
    return response.data;
  },

  previewResume: async (workflowId: string) => {
    const response = await axios.get(`${API_BASE_URL}/preview/${workflowId}`);
    return response.data;
  },

  exportTextUrl: (workflowId: string) => {
    return `${API_BASE_URL}/export-text?workflowId=${workflowId}`;
  },

  exportPdfUrl: (workflowId: string) => {
    return `${API_BASE_URL}/export-pdf?workflowId=${workflowId}`;
  },

  resetWorkflow: async (workflowId: string) => {
    const response = await axios.delete(`${API_BASE_URL}/reset/${workflowId}`);
    return response.data;
  },
};
export default apiClient;
