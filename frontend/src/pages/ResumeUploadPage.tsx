import React, { useState, useRef } from 'react';
import { useWorkflow } from '../features/workflow/WorkflowContext';
import { Upload, FileText, AlertCircle, Sparkles } from 'lucide-react';

export const ResumeUploadPage: React.FC = () => {
  const { uploadResume, loading, error } = useWorkflow();
  const [dragActive, setDragActive] = useState<boolean>(false);
  const [fileError, setFileError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const validateAndUpload = (file: File) => {
    setFileError(null);
    const validExtensions = ['pdf', 'docx'];
    const ext = file.name.split('.').pop()?.toLowerCase();
    
    if (!ext || !validExtensions.includes(ext)) {
      setFileError('Invalid file type. Please upload a PDF or DOCX file.');
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      setFileError('File size exceeds 10MB limit.');
      return;
    }

    uploadResume(file);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      validateAndUpload(e.dataTransfer.files[0]);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    if (e.target.files && e.target.files[0]) {
      validateAndUpload(e.target.files[0]);
    }
  };

  const triggerFileInput = () => {
    fileInputRef.current?.click();
  };

  if (loading) {
    return (
      <div className="card loader-overlay">
        <div className="spinner"></div>
        <h2>Parsing Resume...</h2>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
          Extracted details will be populated in the next step.
        </p>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div>
        <h2 style={{ fontSize: '1.75rem', marginBottom: '0.5rem' }}>Upload Resume</h2>
        <p style={{ color: 'var(--text-secondary)' }}>
          Upload your existing resume. We support PDF or DOCX formats up to 10MB.
        </p>
      </div>

      {(error || fileError) && (
        <div className="session-warning" style={{ margin: 0 }}>
          <AlertCircle size={20} />
          <span>{fileError || error}</span>
        </div>
      )}

      <div className="card" style={{ padding: '3rem' }}>
        <div
          className={`upload-drop-zone ${dragActive ? 'active' : ''}`}
          onDragEnter={handleDrag}
          onDragOver={handleDrag}
          onDragLeave={handleDrag}
          onDrop={handleDrop}
          onClick={triggerFileInput}
        >
          <input
            ref={fileInputRef}
            type="file"
            className="hidden-input"
            style={{ display: 'none' }}
            accept=".pdf,.docx"
            onChange={handleChange}
          />
          <div className="upload-icon">
            <Upload size={32} />
          </div>
          <h3 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>
            Drag & drop your resume here
          </h3>
          <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
            or click to browse your files
          </p>
          <button className="btn btn-primary" type="button">
            Choose File
          </button>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginTop: '1.5rem' }}>
            PDF or DOCX (Max 10MB)
          </p>
        </div>
      </div>

      <div className="card" style={{ borderLeft: '4px solid var(--primary-color)' }}>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <div style={{ color: 'var(--primary-color)' }}>
            <Sparkles size={24} />
          </div>
          <div>
            <h4 style={{ marginBottom: '0.25rem' }}>Why upload first?</h4>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              Parsing your resume extracts details like contact info, skills, projects, and work history.
              You will be able to review, edit, or add manual details before tailoring.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
export default ResumeUploadPage;
