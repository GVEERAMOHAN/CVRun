import React, { useState } from 'react';
import { useWorkflow, ResumeData, Project, Experience, Education, Certification, Achievement, Link } from '../features/workflow/WorkflowContext';
import { Save, Plus, Trash2, HelpCircle, Briefcase, FolderGit, Award, GraduationCap } from 'lucide-react';

export const ProfileCompletionPage: React.FC = () => {
  const { mergedProfile, mergeProfile, loading } = useWorkflow();
  
  // Local editable state initialized from mergedProfile
  const [profile, setProfile] = useState<ResumeData>({
    name: mergedProfile.name || '',
    email: mergedProfile.email || '',
    phone: mergedProfile.phone || '',
    location: mergedProfile.location || '',
    summary: mergedProfile.summary || '',
    skills: [...(mergedProfile.skills || [])],
    projects: [...(mergedProfile.projects || [])],
    experience: [...(mergedProfile.experience || [])],
    education: [...(mergedProfile.education || [])],
    certifications: [...(mergedProfile.certifications || [])],
    achievements: [...(mergedProfile.achievements || [])],
    links: [...(mergedProfile.links || [])],
  });

  const [activeTab, setActiveTab] = useState<string>('personal');
  const [newSkill, setNewSkill] = useState<string>('');

  const handleChange = (field: keyof ResumeData, value: any) => {
    setProfile(prev => ({ ...prev, [field]: value }));
  };

  const handlePersonalChange = (field: string, value: string) => {
    setProfile(prev => ({ ...prev, [field]: value }));
  };

  const handleAddSkill = (e: React.FormEvent) => {
    e.preventDefault();
    if (newSkill.trim() && !profile.skills.includes(newSkill.trim())) {
      setProfile(prev => ({
        ...prev,
        skills: [...prev.skills, newSkill.trim()]
      }));
      setNewSkill('');
    }
  };

  const handleRemoveSkill = (skillToRemove: string) => {
    setProfile(prev => ({
      ...prev,
      skills: prev.skills.filter(s => s !== skillToRemove)
    }));
  };

  // Dynamic Array Handlers
  const handleAddExperience = () => {
    const newExp: Experience = {
      id: 'manual_exp_' + Date.now(),
      company: '',
      role: '',
      location: '',
      startDate: '',
      endDate: '',
      bullets: [],
      techStack: [],
      source: 'manual'
    };
    setProfile(prev => ({ ...prev, experience: [...prev.experience, newExp] }));
  };

  const handleUpdateExperience = (index: number, field: keyof Experience, value: any) => {
    const updated = [...profile.experience];
    updated[index] = { ...updated[index], [field]: value };
    setProfile(prev => ({ ...prev, experience: updated }));
  };

  const handleRemoveExperience = (index: number) => {
    setProfile(prev => ({ ...prev, experience: prev.experience.filter((_, i) => i !== index) }));
  };

  const handleAddProject = () => {
    const newProj: Project = {
      id: 'manual_proj_' + Date.now(),
      title: '',
      description: '',
      bullets: [],
      techStack: [],
      domainTags: [],
      source: 'manual'
    };
    setProfile(prev => ({ ...prev, projects: [...prev.projects, newProj] }));
  };

  const handleUpdateProject = (index: number, field: keyof Project, value: any) => {
    const updated = [...profile.projects];
    updated[index] = { ...updated[index], [field]: value };
    setProfile(prev => ({ ...prev, projects: updated }));
  };

  const handleRemoveProject = (index: number) => {
    setProfile(prev => ({ ...prev, projects: prev.projects.filter((_, i) => i !== index) }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mergeProfile(profile);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div>
        <h2 style={{ fontSize: '1.75rem', marginBottom: '0.5rem' }}>Profile Details</h2>
        <p style={{ color: 'var(--text-secondary)' }}>
          Review and complete your profile. All details here will be used by the AI to align with the job description.
        </p>
      </div>

      <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid var(--border-color)', overflowX: 'auto', paddingBottom: '2px' }}>
        {[
          { id: 'personal', label: 'Personal & Summary' },
          { id: 'skills', label: 'Technical Skills' },
          { id: 'experience', label: 'Work Experience' },
          { id: 'projects', label: 'Projects' }
        ].map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`btn`}
            style={{
              backgroundColor: activeTab === tab.id ? 'var(--primary-light)' : 'transparent',
              color: activeTab === tab.id ? 'var(--primary-color)' : 'var(--text-secondary)',
              border: 'none',
              borderRadius: 'var(--radius-sm)',
              padding: '0.6rem 1.2rem',
              whiteSpace: 'nowrap'
            }}
          >
            {tab.label}
          </button>
        ))}
      </div>

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        <div className="card">
          {/* TAB 1: PERSONAL INFO */}
          {activeTab === 'personal' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
              <h3 style={{ fontSize: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                Personal Information
              </h3>
              
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Full Name *</label>
                  <input
                    type="text"
                    required
                    value={profile.name}
                    onChange={(e) => handlePersonalChange('name', e.target.value)}
                    className="form-control"
                    placeholder="John Doe"
                  />
                </div>
                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Email Address *</label>
                  <input
                    type="email"
                    required
                    value={profile.email}
                    onChange={(e) => handlePersonalChange('email', e.target.value)}
                    className="form-control"
                    placeholder="john.doe@example.com"
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Phone Number</label>
                  <input
                    type="text"
                    value={profile.phone}
                    onChange={(e) => handlePersonalChange('phone', e.target.value)}
                    className="form-control"
                    placeholder="+91 9876543210"
                  />
                </div>
                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Location</label>
                  <input
                    type="text"
                    value={profile.location}
                    onChange={(e) => handlePersonalChange('location', e.target.value)}
                    className="form-control"
                    placeholder="Bangalore, India"
                  />
                </div>
              </div>

              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Professional Summary</label>
                <textarea
                  value={profile.summary}
                  onChange={(e) => handlePersonalChange('summary', e.target.value)}
                  className="form-control"
                  placeholder="Backend Developer with 4+ years of experience designing scalable microservices..."
                  rows={4}
                />
              </div>
            </div>
          )}

          {/* TAB 2: TECHNICAL SKILLS */}
          {activeTab === 'skills' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
              <h3 style={{ fontSize: '1.25rem' }}>Skills Inventory</h3>
              
              <div style={{ display: 'flex', gap: '0.75rem' }}>
                <input
                  type="text"
                  value={newSkill}
                  onChange={(e) => setNewSkill(e.target.value)}
                  className="form-control"
                  placeholder="e.g. Docker, Redis, Kubernetes"
                  style={{ maxWidth: '300px' }}
                />
                <button type="button" onClick={handleAddSkill} className="btn btn-secondary">
                  <Plus size={18} /> Add Skill
                </button>
              </div>

              <div className="tag-list" style={{ marginTop: '1rem' }}>
                {profile.skills.map(skill => (
                  <span key={skill} className="tag-badge primary">
                    {skill}
                    <Trash2
                      size={14}
                      className="tag-badge-remove"
                      onClick={() => handleRemoveSkill(skill)}
                    />
                  </span>
                ))}
                {profile.skills.length === 0 && (
                  <p style={{ color: 'var(--text-muted)' }}>No skills added yet. Add some tech tags above!</p>
                )}
              </div>
            </div>
          )}

          {/* TAB 3: WORK EXPERIENCE */}
          {activeTab === 'experience' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ fontSize: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <Briefcase size={20} /> Work History
                </h3>
                <button type="button" onClick={handleAddExperience} className="btn btn-secondary btn-sm">
                  <Plus size={16} /> Add Position
                </button>
              </div>

              {profile.experience.map((exp, idx) => (
                <div key={exp.id || idx} style={{ border: '1px solid var(--border-color)', borderRadius: 'var(--radius-sm)', padding: '1.5rem', position: 'relative' }}>
                  <button
                    type="button"
                    onClick={() => handleRemoveExperience(idx)}
                    className="btn-icon"
                    style={{ position: 'absolute', top: '15px', right: '15px', color: 'var(--danger-color)' }}
                  >
                    <Trash2 size={16} />
                  </button>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                    <div className="form-group" style={{ margin: 0 }}>
                      <label className="form-label">Company Name</label>
                      <input
                        type="text"
                        value={exp.company}
                        onChange={(e) => handleUpdateExperience(idx, 'company', e.target.value)}
                        className="form-control"
                        placeholder="e.g. Google"
                      />
                    </div>
                    <div className="form-group" style={{ margin: 0 }}>
                      <label className="form-label">Job Title / Role</label>
                      <input
                        type="text"
                        value={exp.role}
                        onChange={(e) => handleUpdateExperience(idx, 'role', e.target.value)}
                        className="form-control"
                        placeholder="e.g. Senior Software Engineer"
                      />
                    </div>
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                    <div className="form-group" style={{ margin: 0 }}>
                      <label className="form-label">Start Date</label>
                      <input
                        type="text"
                        value={exp.startDate}
                        onChange={(e) => handleUpdateExperience(idx, 'startDate', e.target.value)}
                        className="form-control"
                        placeholder="Jan 2021"
                      />
                    </div>
                    <div className="form-group" style={{ margin: 0 }}>
                      <label className="form-label">End Date</label>
                      <input
                        type="text"
                        value={exp.endDate}
                        onChange={(e) => handleUpdateExperience(idx, 'endDate', e.target.value)}
                        className="form-control"
                        placeholder="Present or Dec 2023"
                      />
                    </div>
                    <div className="form-group" style={{ margin: 0 }}>
                      <label className="form-label">Location</label>
                      <input
                        type="text"
                        value={exp.location}
                        onChange={(e) => handleUpdateExperience(idx, 'location', e.target.value)}
                        className="form-control"
                        placeholder="Bangalore, India"
                      />
                    </div>
                  </div>

                  <div className="form-group" style={{ margin: 0 }}>
                    <label className="form-label">Bullet Achievements (one per line)</label>
                    <textarea
                      value={exp.bullets.join('\n')}
                      onChange={(e) => handleUpdateExperience(idx, 'bullets', e.target.value.split('\n'))}
                      className="form-control"
                      placeholder="Designed database schema matching scaling needs&#10;Lead team of 4 junior developers"
                      rows={3}
                    />
                  </div>
                </div>
              ))}
              {profile.experience.length === 0 && (
                <p style={{ color: 'var(--text-muted)' }}>No experiences listed yet. Add one above!</p>
              )}
            </div>
          )}

          {/* TAB 4: PROJECTS */}
          {activeTab === 'projects' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ fontSize: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <FolderGit size={20} /> Personal Projects
                </h3>
                <button type="button" onClick={handleAddProject} className="btn btn-secondary btn-sm">
                  <Plus size={16} /> Add Project
                </button>
              </div>

              {profile.projects.map((proj, idx) => (
                <div key={proj.id || idx} style={{ border: '1px solid var(--border-color)', borderRadius: 'var(--radius-sm)', padding: '1.5rem', position: 'relative' }}>
                  <button
                    type="button"
                    onClick={() => handleRemoveProject(idx)}
                    className="btn-icon"
                    style={{ position: 'absolute', top: '15px', right: '15px', color: 'var(--danger-color)' }}
                  >
                    <Trash2 size={16} />
                  </button>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                    <div className="form-group" style={{ margin: 0 }}>
                      <label className="form-label">Project Title</label>
                      <input
                        type="text"
                        value={proj.title}
                        onChange={(e) => handleUpdateProject(idx, 'title', e.target.value)}
                        className="form-control"
                        placeholder="e.g. AI Resume Tailor"
                      />
                    </div>
                    <div className="form-group" style={{ margin: 0 }}>
                      <label className="form-label">Tech Stack (comma separated)</label>
                      <input
                        type="text"
                        value={proj.techStack.join(', ')}
                        onChange={(e) => handleUpdateProject(idx, 'techStack', e.target.value.split(',').map(s => s.trim()))}
                        className="form-control"
                        placeholder="React, Spring Boot, Java"
                      />
                    </div>
                  </div>

                  <div className="form-group" style={{ margin: 0 }}>
                    <label className="form-label">Project Description</label>
                    <input
                      type="text"
                      value={proj.description}
                      onChange={(e) => handleUpdateProject(idx, 'description', e.target.value)}
                      className="form-control"
                      placeholder="e.g. Web tool designed to dynamically build ATS resumes"
                    />
                  </div>

                  <div className="form-group" style={{ margin: 0, marginTop: '1rem' }}>
                    <label className="form-label">Project Bullets (one per line)</label>
                    <textarea
                      value={proj.bullets.join('\n')}
                      onChange={(e) => handleUpdateProject(idx, 'bullets', e.target.value.split('\n'))}
                      className="form-control"
                      placeholder="Developed REST endpoints using Node.js&#10;Integrated OpenAI GPT-3 APIs"
                      rows={3}
                    />
                  </div>
                </div>
              ))}
              {profile.projects.length === 0 && (
                <p style={{ color: 'var(--text-muted)' }}>No projects listed yet. Add one above!</p>
              )}
            </div>
          )}
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem' }}>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            <Save size={18} /> Save & Continue
          </button>
        </div>
      </form>
    </div>
  );
};
export default ProfileCompletionPage;
