import React, { useState } from 'react';
import { useWorkflow, ApprovedContent, Project, Experience } from '../features/workflow/WorkflowContext';
import { ArrowUp, ArrowDown, Trash2, Plus, Edit, FileText, ChevronDown, ChevronUp } from 'lucide-react';

export const ReviewApprovalPage: React.FC = () => {
  const { approvedContent, saveApprovedContent, loading } = useWorkflow();
  const [content, setContent] = useState<ApprovedContent>({
    skills: [...(approvedContent.skills || [])],
    projects: [...(approvedContent.projects || [])],
    experience: [...(approvedContent.experience || [])],
    education: [...(approvedContent.education || [])],
    certifications: [...(approvedContent.certifications || [])],
    achievements: [...(approvedContent.achievements || [])],
    links: [...(approvedContent.links || [])],
  });

  const [expandedSections, setExpandedSections] = useState<Record<string, boolean>>({
    skills: true,
    experience: true,
    projects: true,
    education: true,
    certifications: true,
  });

  const toggleSection = (sec: string) => {
    setExpandedSections(prev => ({ ...prev, [sec]: !prev[sec] }));
  };

  // Reordering functions
  const moveItem = <T,>(list: T[], index: number, direction: 'up' | 'down'): T[] => {
    const newList = [...list];
    if (direction === 'up' && index > 0) {
      const temp = newList[index];
      newList[index] = newList[index - 1];
      newList[index - 1] = temp;
    } else if (direction === 'down' && index < list.length - 1) {
      const temp = newList[index];
      newList[index] = newList[index + 1];
      newList[index + 1] = temp;
    }
    return newList;
  };

  const handleMoveSkill = (index: number, direction: 'up' | 'down') => {
    setContent(prev => ({ ...prev, skills: moveItem(prev.skills, index, direction) }));
  };

  const handleMoveExperience = (index: number, direction: 'up' | 'down') => {
    setContent(prev => ({ ...prev, experience: moveItem(prev.experience, index, direction) }));
  };

  const handleMoveProject = (index: number, direction: 'up' | 'down') => {
    setContent(prev => ({ ...prev, projects: moveItem(prev.projects, index, direction) }));
  };

  // Deletion functions
  const handleDeleteItem = <T,>(list: T[], index: number): T[] => {
    return list.filter((_, i) => i !== index);
  };

  const handleRemoveSkill = (index: number) => {
    setContent(prev => ({ ...prev, skills: handleDeleteItem(prev.skills, index) }));
  };

  const handleRemoveExperience = (index: number) => {
    setContent(prev => ({ ...prev, experience: handleDeleteItem(prev.experience, index) }));
  };

  const handleRemoveProject = (index: number) => {
    setContent(prev => ({ ...prev, projects: handleDeleteItem(prev.projects, index) }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    saveApprovedContent(content);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div>
        <h2 style={{ fontSize: '1.75rem', marginBottom: '0.5rem' }}>Review & Reorder</h2>
        <p style={{ color: 'var(--text-secondary)' }}>
          Review the final content of your resume. Reorder sections or individual items to structure your profile correctly.
        </p>
      </div>

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        {/* SKILLS SECTION */}
        <div className="card" style={{ padding: '1.5rem' }}>
          <div
            style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer' }}
            onClick={() => toggleSection('skills')}
          >
            <h3 style={{ fontSize: '1.25rem' }}>Skills ({content.skills.length})</h3>
            {expandedSections.skills ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
          </div>

          {expandedSections.skills && (
            <div style={{ marginTop: '1.25rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {content.skills.map((skill, idx) => (
                <div
                  key={skill}
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '0.6rem 0.8rem',
                    border: '1px solid var(--border-color)',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: '#f8fafc'
                  }}
                >
                  <span style={{ fontWeight: 600 }}>{skill}</span>
                  <div style={{ display: 'flex', gap: '0.4rem' }}>
                    <button type="button" onClick={() => handleMoveSkill(idx, 'up')} className="btn-icon" disabled={idx === 0}>
                      <ArrowUp size={14} />
                    </button>
                    <button type="button" onClick={() => handleMoveSkill(idx, 'down')} className="btn-icon" disabled={idx === content.skills.length - 1}>
                      <ArrowDown size={14} />
                    </button>
                    <button type="button" onClick={() => handleRemoveSkill(idx)} className="btn-icon" style={{ color: 'var(--danger-color)' }}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
              ))}
              {content.skills.length === 0 && <p style={{ color: 'var(--text-muted)' }}>No skills approved.</p>}
            </div>
          )}
        </div>

        {/* EXPERIENCE SECTION */}
        <div className="card" style={{ padding: '1.5rem' }}>
          <div
            style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer' }}
            onClick={() => toggleSection('experience')}
          >
            <h3 style={{ fontSize: '1.25rem' }}>Experience ({content.experience.length})</h3>
            {expandedSections.experience ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
          </div>

          {expandedSections.experience && (
            <div style={{ marginTop: '1.25rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {content.experience.map((exp, idx) => (
                <div
                  key={exp.id || idx}
                  style={{
                    border: '1px solid var(--border-color)',
                    borderRadius: 'var(--radius-sm)',
                    padding: '1.25rem',
                    backgroundColor: '#f8fafc',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'flex-start'
                  }}
                >
                  <div>
                    <h4 style={{ fontSize: '1.05rem' }}>
                      {exp.role} <span style={{ fontWeight: 400, color: 'var(--text-secondary)' }}>at {exp.company}</span>
                    </h4>
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                      {exp.startDate} - {exp.endDate}
                    </span>
                    <ul style={{ paddingLeft: '1.25rem', marginTop: '0.5rem', color: 'var(--text-secondary)' }}>
                      {exp.bullets.map((b, i) => (
                        <li key={i}>{b}</li>
                      ))}
                    </ul>
                  </div>

                  <div style={{ display: 'flex', gap: '0.4rem', flexShrink: 0 }}>
                    <button type="button" onClick={() => handleMoveExperience(idx, 'up')} className="btn-icon" disabled={idx === 0}>
                      <ArrowUp size={14} />
                    </button>
                    <button type="button" onClick={() => handleMoveExperience(idx, 'down')} className="btn-icon" disabled={idx === content.experience.length - 1}>
                      <ArrowDown size={14} />
                    </button>
                    <button type="button" onClick={() => handleRemoveExperience(idx)} className="btn-icon" style={{ color: 'var(--danger-color)' }}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
              ))}
              {content.experience.length === 0 && <p style={{ color: 'var(--text-muted)' }}>No experience items approved.</p>}
            </div>
          )}
        </div>

        {/* PROJECTS SECTION */}
        <div className="card" style={{ padding: '1.5rem' }}>
          <div
            style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer' }}
            onClick={() => toggleSection('projects')}
          >
            <h3 style={{ fontSize: '1.25rem' }}>Projects ({content.projects.length})</h3>
            {expandedSections.projects ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
          </div>

          {expandedSections.projects && (
            <div style={{ marginTop: '1.25rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {content.projects.map((proj, idx) => (
                <div
                  key={proj.id || idx}
                  style={{
                    border: '1px solid var(--border-color)',
                    borderRadius: 'var(--radius-sm)',
                    padding: '1.25rem',
                    backgroundColor: '#f8fafc',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'flex-start'
                  }}
                >
                  <div>
                    <h4 style={{ fontSize: '1.05rem' }}>{proj.title}</h4>
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                      Tech Stack: {proj.techStack.join(', ')}
                    </span>
                    <ul style={{ paddingLeft: '1.25rem', marginTop: '0.5rem', color: 'var(--text-secondary)' }}>
                      {proj.bullets.map((b, i) => (
                        <li key={i}>{b}</li>
                      ))}
                    </ul>
                  </div>

                  <div style={{ display: 'flex', gap: '0.4rem', flexShrink: 0 }}>
                    <button type="button" onClick={() => handleMoveProject(idx, 'up')} className="btn-icon" disabled={idx === 0}>
                      <ArrowUp size={14} />
                    </button>
                    <button type="button" onClick={() => handleMoveProject(idx, 'down')} className="btn-icon" disabled={idx === content.projects.length - 1}>
                      <ArrowDown size={14} />
                    </button>
                    <button type="button" onClick={() => handleRemoveProject(idx)} className="btn-icon" style={{ color: 'var(--danger-color)' }}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
              ))}
              {content.projects.length === 0 && <p style={{ color: 'var(--text-muted)' }}>No projects approved.</p>}
            </div>
          )}
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem' }}>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            <FileText size={18} /> Generate Tailored Resume
          </button>
        </div>
      </form>
    </div>
  );
};
export default ReviewApprovalPage;
