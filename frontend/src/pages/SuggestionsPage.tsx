import React, { useState } from 'react';
import { useWorkflow, Suggestion } from '../features/workflow/WorkflowContext';
import { Check, X, Edit, Save, Plus, ArrowRight, ShieldAlert, BadgeCheck } from 'lucide-react';

export const SuggestionsPage: React.FC = () => {
  const { suggestions, updateSuggestions, approveSuggestions, loading, jobDescription } = useWorkflow();
  const [activeFilter, setActiveFilter] = useState<string>('all');
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editTitle, setEditTitle] = useState<string>('');
  const [editBullets, setEditBullets] = useState<string>('');

  const handleToggleApprove = (id: string) => {
    const updated = suggestions.map(sug => {
      if (sug.id === id) {
        return { ...sug, approved: !sug.approved };
      }
      return sug;
    });
    updateSuggestions(updated);
  };

  const handleStartEdit = (sug: Suggestion) => {
    setEditingId(sug.id);
    setEditTitle(sug.title);
    setEditBullets(sug.bullets.join('\n'));
  };

  const handleSaveEdit = (id: string) => {
    const updated = suggestions.map(sug => {
      if (sug.id === id) {
        return {
          ...sug,
          title: editTitle,
          bullets: editBullets.split('\n').filter(b => b.trim() !== '')
        };
      }
      return sug;
    });
    updateSuggestions(updated);
    setEditingId(null);
  };

  const filteredSuggestions = suggestions.filter(sug => {
    if (activeFilter === 'all') return true;
    return sug.section === activeFilter;
  });

  const getScoreClass = (score: number) => {
    return score >= 85 ? 'score-high' : 'score-medium';
  };

  const formatSource = (source: string) => {
    if (source === 'resume') return 'Parsed Resume';
    if (source === 'manual') return 'Manual Input';
    return 'Combined Profile';
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <h2 style={{ fontSize: '1.75rem', marginBottom: '0.5rem' }}>AI Suggestions</h2>
          <p style={{ color: 'var(--text-secondary)' }}>
            We've scored your profile items against the job requirements. Approve, edit, or remove items for the final resume.
          </p>
        </div>
      </div>

      <div style={{ display: 'flex', gap: '0.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '2px', overflowX: 'auto' }}>
        {[
          { id: 'all', label: `All (${suggestions.length})` },
          { id: 'skills', label: `Skills (${suggestions.filter(s => s.section === 'skills').length})` },
          { id: 'projects', label: `Projects (${suggestions.filter(s => s.section === 'projects').length})` },
          { id: 'experience', label: `Experience (${suggestions.filter(s => s.section === 'experience').length})` },
          { id: 'certifications', label: `Certifications (${suggestions.filter(s => s.section === 'certifications').length})` }
        ].map(filter => (
          <button
            key={filter.id}
            onClick={() => setActiveFilter(filter.id)}
            className="btn"
            style={{
              backgroundColor: activeFilter === filter.id ? 'var(--primary-light)' : 'transparent',
              color: activeFilter === filter.id ? 'var(--primary-color)' : 'var(--text-secondary)',
              border: 'none',
              padding: '0.5rem 1rem',
              whiteSpace: 'nowrap'
            }}
          >
            {filter.label}
          </button>
        ))}
      </div>

      <div className="suggestions-grid">
        {filteredSuggestions.map((sug) => {
          const isEditing = editingId === sug.id;
          return (
            <div
              key={sug.id}
              className={`suggestion-card`}
              style={{
                opacity: sug.approved ? 1 : 0.65,
                borderLeft: sug.approved ? '4px solid var(--success-color)' : '4px solid var(--border-color)'
              }}
            >
              <div className={`score-badge ${getScoreClass(sug.score)}`}>
                {sug.score}
              </div>

              <div className="suggestion-content">
                {isEditing ? (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                    <input
                      type="text"
                      value={editTitle}
                      onChange={(e) => setEditTitle(e.target.value)}
                      className="form-control"
                      style={{ fontWeight: 600 }}
                    />
                    {sug.section !== 'skills' && (
                      <textarea
                        value={editBullets}
                        onChange={(e) => setEditBullets(e.target.value)}
                        className="form-control"
                        rows={3}
                        placeholder="Enter achievements (one per line)"
                      />
                    )}
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button
                        type="button"
                        onClick={() => handleSaveEdit(sug.id)}
                        className="btn btn-primary btn-sm"
                        style={{ padding: '0.35rem 0.75rem', fontSize: '0.8rem' }}
                      >
                        <Save size={14} /> Save
                      </button>
                      <button
                        type="button"
                        onClick={() => setEditingId(null)}
                        className="btn btn-secondary btn-sm"
                        style={{ padding: '0.35rem 0.75rem', fontSize: '0.8rem' }}
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <div className="suggestion-header">
                      <div>
                        <div className="suggestion-title">
                          {sug.title} {sug.subtitle && <span style={{ fontWeight: 400, color: 'var(--text-secondary)' }}>at {sug.subtitle}</span>}
                        </div>
                        <div className="suggestion-meta">
                          <span>Section: <strong style={{ textTransform: 'capitalize' }}>{sug.section}</strong></span>
                          <span>•</span>
                          <span>Source: {formatSource(sug.source)}</span>
                        </div>
                      </div>
                      
                      <div className="suggestion-actions">
                        <button
                          type="button"
                          onClick={() => handleToggleApprove(sug.id)}
                          className={`btn-icon ${sug.approved ? 'active-success' : ''}`}
                          title={sug.approved ? 'Approved' : 'Approve'}
                        >
                          <Check size={16} />
                        </button>
                        <button
                          type="button"
                          onClick={() => handleStartEdit(sug)}
                          className="btn-icon"
                          title="Edit wording"
                        >
                          <Edit size={16} />
                        </button>
                      </div>
                    </div>

                    {sug.bullets && sug.bullets.length > 0 && (
                      <ul style={{ paddingLeft: '1.25rem', marginTop: '0.5rem', color: 'var(--text-secondary)' }}>
                        {sug.bullets.map((b, idx) => (
                          <li key={idx} style={{ marginBottom: '0.25rem' }}>{b}</li>
                        ))}
                      </ul>
                    )}

                    <div className="suggestion-reason">
                      <strong>Relevance Reason:</strong> {sug.reason}
                    </div>
                  </>
                )}
              </div>
            </div>
          );
        })}

        {filteredSuggestions.length === 0 && (
          <div className="card" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
            No suggestions available under this section.
          </div>
        )}
      </div>

      <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1.5rem' }}>
        <button
          type="button"
          onClick={approveSuggestions}
          className="btn btn-primary"
          disabled={loading || suggestions.filter(s => s.approved).length === 0}
        >
          Proceed to Review & Reorder <ArrowRight size={18} />
        </button>
      </div>
    </div>
  );
};
export default SuggestionsPage;
