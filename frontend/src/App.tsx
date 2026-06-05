import React from 'react';
import { WorkflowProvider, useWorkflow } from './features/workflow/WorkflowContext';
import ResumeUploadPage from './pages/ResumeUploadPage';
import ProfileCompletionPage from './pages/ProfileCompletionPage';
import JobDescriptionPage from './pages/JobDescriptionPage';
import SuggestionsPage from './pages/SuggestionsPage';
import ReviewApprovalPage from './pages/ReviewApprovalPage';
import ResumePreviewPage from './pages/ResumePreviewPage';
import ExportPage from './pages/ExportPage';

import { 
  Sparkles, FileText, CheckCircle2, AlertTriangle, 
  RefreshCw, Shield, FileUp, UserCheck, 
  Terminal, SearchCode, Edit3, Eye, Download 
} from 'lucide-react';

const MainAppContent: React.FC = () => {
  const { step, workflowId, status, resetWorkflow, timeLeft, goToStep } = useWorkflow();

  const renderActivePage = () => {
    switch (step) {
      case 1:
        return <ResumeUploadPage />;
      case 2:
        return <ProfileCompletionPage />;
      case 3:
        return <JobDescriptionPage />;
      case 4:
        return <SuggestionsPage />;
      case 5:
        return <ReviewApprovalPage />;
      case 6:
        return <ResumePreviewPage />;
      case 7:
        return <ExportPage />;
      default:
        return <ResumeUploadPage />;
    }
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  const stepsList = [
    { num: 1, label: 'Upload', statusVal: 'RESUME_UPLOADED', icon: <FileUp size={16} /> },
    { num: 2, label: 'Profile', statusVal: 'PROFILE_MERGED', icon: <UserCheck size={16} /> },
    { num: 3, label: 'Job Description', statusVal: 'JD_ANALYZED', icon: <SearchCode size={16} /> },
    { num: 4, label: 'Suggestions', statusVal: 'SUGGESTIONS_READY', icon: <Terminal size={16} /> },
    { num: 5, label: 'Review', statusVal: 'USER_REVIEWED', icon: <Edit3 size={16} /> },
    { num: 6, label: 'Generate', statusVal: 'RESUME_GENERATED', icon: <Eye size={16} /> },
    { num: 7, label: 'Export', statusVal: 'EXPORTED', icon: <Download size={16} /> },
  ];

  return (
    <div className="app-container">
      {/* MAIN WIZARD AREA */}
      <main className="main-content" style={{ width: '100%' }}>
        <header className="top-header" style={{ flexWrap: 'wrap', gap: '1rem' }}>
          {/* Logo Branding */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div className="logo-icon" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <Sparkles size={20} />
            </div>
            <div>
              <h1 className="logo-text" style={{ fontSize: '1.5rem', margin: 0, lineHeight: 1 }}>CVRun</h1>
              <p className="logo-subtitle" style={{ margin: 0, fontSize: '0.75rem', color: 'var(--text-secondary)' }}>AI-Powered Resume Tailoring</p>
            </div>
          </div>

          {/* Session controls & widgets */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem', flexWrap: 'wrap' }}>
            {workflowId && (
              <div style={{ display: 'flex', gap: '0.4rem', alignItems: 'center', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                <Shield size={14} style={{ color: 'var(--success-color)' }} />
                <span>Privacy Guard Active</span>
              </div>
            )}

            {workflowId && (
              <div style={{
                backgroundColor: timeLeft < 300 ? 'var(--danger-light)' : '#f8fafc',
                border: '1px solid var(--border-color)',
                padding: '0.4rem 0.75rem',
                borderRadius: 'var(--radius-sm)',
                fontSize: '0.85rem',
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem'
              }}>
                <span style={{ fontWeight: 600, color: timeLeft < 300 ? 'var(--danger-color)' : 'var(--text-secondary)' }}>Session Expiry:</span>
                <span style={{ fontWeight: 700, color: timeLeft < 300 ? 'var(--danger-color)' : 'var(--text-primary)' }}>{formatTime(timeLeft)}</span>
              </div>
            )}

            {workflowId ? (
              <div className="workflow-session-badge">
                <span>Workflow ID:</span>
                <strong>{workflowId}</strong>
              </div>
            ) : (
              <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem', fontWeight: 500 }}>
                No active session
              </div>
            )}
            
            {workflowId && (
              <button onClick={resetWorkflow} className="btn-reset">
                <RefreshCw size={14} /> Reset Workflow
              </button>
            )}
          </div>
        </header>

        {timeLeft < 300 && workflowId && (
          <div className="session-warning">
            <AlertTriangle size={20} />
            <span>Warning: Your session will expire in less than {Math.floor(timeLeft / 60)} minutes. Save your work to avoid losing edits!</span>
          </div>
        )}

        {/* Wizard step dots bar for visual responsiveness */}
        <div className="wizard-tracker">
          {stepsList.map((s) => {
            const isActive = step === s.num;
            const isCompleted = step > s.num;
            return (
              <div
                key={s.num}
                className={`wizard-step-node ${isActive ? 'active' : ''} ${isCompleted ? 'completed' : ''}`}
                onClick={() => goToStep(s.num)}
              >
                <div className="step-circle">
                  {isCompleted ? <CheckCircle2 size={16} /> : s.num}
                </div>
                <span className="step-label">{s.label}</span>
              </div>
            );
          })}
        </div>

        {/* Active step screen */}
        <div style={{ flexGrow: 1 }}>
          {renderActivePage()}
        </div>
      </main>
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <WorkflowProvider>
      <MainAppContent />
    </WorkflowProvider>
  );
};

export default App;
