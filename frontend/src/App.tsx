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
  RefreshCw, Shield, HelpCircle, FileUp, UserCheck, 
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
      {/* SIDEBAR PANEL */}
      <aside className="sidebar">
        <div className="logo-container">
          <div className="logo-icon">
            <Sparkles size={22} />
          </div>
          <div>
            <h1 className="logo-text">CVRun</h1>
            <p className="logo-subtitle">AI-Powered Resume Tailoring</p>
          </div>
        </div>

        <nav style={{ flexGrow: 1 }}>
          <div className="nav-section-title">Workflow State</div>
          <ul className="workflow-status-list">
            {stepsList.map((s) => {
              const isActive = step === s.num;
              const isCompleted = step > s.num;
              return (
                <li
                  key={s.num}
                  onClick={() => goToStep(s.num)}
                  className={`workflow-status-item ${isActive ? 'active' : ''} ${isCompleted ? 'completed' : ''}`}
                  style={{ cursor: (isCompleted || isActive) ? 'pointer' : 'not-allowed' }}
                >
                  <span className="status-dot" />
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    {s.icon} {s.label}
                  </span>
                </li>
              );
            })}
          </ul>
        </nav>

        {/* TTL Session Timer & Data Privacy Badge */}
        <div style={{ marginTop: 'auto', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {workflowId && (
            <div style={{
              backgroundColor: timeLeft < 300 ? 'var(--danger-light)' : '#f8fafc',
              border: '1px solid var(--border-color)',
              padding: '0.75rem',
              borderRadius: 'var(--radius-sm)',
              fontSize: '0.85rem'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 600, color: timeLeft < 300 ? 'var(--danger-color)' : 'var(--text-secondary)' }}>
                <span>Session Expiry</span>
                <span>{formatTime(timeLeft)}</span>
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                Auto-cleaning active cache
              </div>
            </div>
          )}

          <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
            <Shield size={16} style={{ color: 'var(--success-color)' }} />
            <span>Privacy Guard Active</span>
          </div>
        </div>
      </aside>

      {/* MAIN WIZARD AREA */}
      <main className="main-content">
        <header className="top-header">
          <div>
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
          </div>
          
          {workflowId && (
            <button onClick={resetWorkflow} className="btn-reset">
              <RefreshCw size={14} /> Reset Workflow
            </button>
          )}
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
