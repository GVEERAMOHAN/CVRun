import React, { useState } from 'react';
import { useWorkflow } from '../features/workflow/WorkflowContext';
import { FileText, Sparkles, AlertCircle } from 'lucide-react';

export const JobDescriptionPage: React.FC = () => {
  const { jobDescription, analyzeJd, loading } = useWorkflow();
  const [jdText, setJdText] = useState<string>(jobDescription.rawText || '');
  const [validationError, setValidationError] = useState<string | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setValidationError(null);

    if (!jdText.trim()) {
      setValidationError('Please paste the job description text to proceed.');
      return;
    }
    
    if (jdText.trim().length < 50) {
      setValidationError('Job description looks too short. Please paste the full text for accurate scoring.');
      return;
    }

    analyzeJd(jdText);
  };

  if (loading) {
    return (
      <div className="card loader-overlay">
        <div className="spinner"></div>
        <h2>Analyzing Job Description...</h2>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
          Extracting key tech stacks, required skills, and calculating alignment scores.
        </p>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div>
        <h2 style={{ fontSize: '1.75rem', marginBottom: '0.5rem' }}>Job Description</h2>
        <p style={{ color: 'var(--text-secondary)' }}>
          Paste the target job description. We will extract key phrases and match them against your profile.
        </p>
      </div>

      {validationError && (
        <div className="session-warning" style={{ margin: 0 }}>
          <AlertCircle size={20} />
          <span>{validationError}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        <div className="card">
          <div className="form-group" style={{ margin: 0 }}>
            <label className="form-label">Job Description Text *</label>
            <textarea
              required
              value={jdText}
              onChange={(e) => setJdText(e.target.value)}
              className="form-control"
              placeholder="Paste the full job description text here, including requirements, responsibilities, and technologies needed..."
              style={{ minHeight: '300px' }}
            />
            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '0.5rem', color: 'var(--text-muted)', fontSize: '0.8rem' }}>
              {jdText.length} characters pasted
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <button type="submit" className="btn btn-primary">
            <Sparkles size={18} /> Analyze & Score
          </button>
        </div>
      </form>

      <div className="card" style={{ borderLeft: '4px solid var(--primary-color)' }}>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <div style={{ color: 'var(--primary-color)' }}>
            <Sparkles size={24} />
          </div>
          <div>
            <h4 style={{ marginBottom: '0.25rem' }}>Pro Tip</h4>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              Ensure you copy the entire job description—especially sections outlining required technologies, 
              skills, and tools. This will maximize the accuracy of the matching algorithm.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
export default JobDescriptionPage;
