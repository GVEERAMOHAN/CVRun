import React, { useEffect } from 'react';
import { useWorkflow } from '../features/workflow/WorkflowContext';
import { ArrowLeft, Download, CheckCircle, RefreshCw } from 'lucide-react';

export const ResumePreviewPage: React.FC = () => {
  const { generatedResume, generateResume, loading, error, prevStep, nextStep, workflowId } = useWorkflow();

  useEffect(() => {
    // Generate resume automatically when the page loads if it hasn't been generated yet
    if (!generatedResume && workflowId) {
      generateResume();
    }
  }, [generatedResume, workflowId]);

  if (loading) {
    return (
      <div className="card loader-overlay">
        <div className="spinner"></div>
        <h2>Generating Tailored Resume...</h2>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
          Aligning wording and structuring sections into an ATS-friendly layout.
        </p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
        <h3 style={{ color: 'var(--danger-color)', marginBottom: '1rem' }}>Generation Failed</h3>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>{error}</p>
        <button onClick={generateResume} className="btn btn-primary">
          <RefreshCw size={16} /> Retry Generation
        </button>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <h2 style={{ fontSize: '1.75rem', marginBottom: '0.5rem' }}>Resume Preview</h2>
          <p style={{ color: 'var(--text-secondary)' }}>
            Review the final tailored ATS resume. It is formatted in a single-column, clean layout.
          </p>
        </div>
      </div>

      <div className="preview-layout" style={{ gridTemplateColumns: '1fr' }}>
        <div className="card" style={{ padding: '1.5rem', backgroundColor: '#f1f5f9' }}>
          <div className="preview-paper">
            {generatedResume?.plainText || 'No text generated.'}
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '1rem' }}>
        <button type="button" onClick={prevStep} className="btn btn-secondary">
          <ArrowLeft size={18} /> Back to Review
        </button>
        <button type="button" onClick={nextStep} className="btn btn-primary">
          Proceed to Export <CheckCircle size={18} />
        </button>
      </div>
    </div>
  );
};
export default ResumePreviewPage;
