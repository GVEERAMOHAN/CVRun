import React from 'react';
import { useWorkflow } from '../features/workflow/WorkflowContext';
import { apiClient } from '../api/apiClient';
import { Download, RefreshCw, CheckCircle2, ShieldAlert } from 'lucide-react';

export const ExportPage: React.FC = () => {
  const { workflowId, resetWorkflow } = useWorkflow();

  const handleDownload = () => {
    if (!workflowId) return;
    const downloadUrl = apiClient.exportTextUrl(workflowId);
    
    // Create an anchor element and trigger a download
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.setAttribute('download', 'tailored_resume.txt');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem', alignItems: 'center', justifyContent: 'center', minHeight: '60vh', textAlign: 'center' }}>
      <div className="card" style={{ maxWidth: '600px', padding: '3rem', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1.5rem' }}>
        <div style={{ color: 'var(--success-color)' }}>
          <CheckCircle2 size={64} />
        </div>
        
        <div>
          <h2 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>Export Successful!</h2>
          <p style={{ color: 'var(--text-secondary)' }}>
            Your tailored resume has been formatted and is ready for use.
          </p>
        </div>

        <button onClick={handleDownload} className="btn btn-primary" style={{ padding: '1rem 2rem', fontSize: '1rem' }}>
          <Download size={20} /> Download Plain Text (.txt)
        </button>

        <button onClick={resetWorkflow} className="btn btn-secondary">
          <RefreshCw size={16} /> Start New Workflow
        </button>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
        <ShieldAlert size={16} />
        <span>Your data is temporary. Resetting or letting the cache expire will delete all inputs.</span>
      </div>
    </div>
  );
};
export default ExportPage;
