import { useState, useEffect } from 'react';
import { releaseAPI, projectAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function Releases() {
  const { user } = useAuth();
  const [releases, setReleases] = useState([]);
  const [projects, setProjects] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ version: '', description: '', releaseNotes: '', artifactUrl: '', projectId: '' });
  const [loading, setLoading] = useState(true);

  useEffect(() => { loadData(); }, []);

  const loadData = async () => {
    try {
      const [relRes, projRes] = await Promise.all([releaseAPI.getAll(), projectAPI.getAll()]);
      setReleases(relRes.data);
      setProjects(projRes.data);
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await releaseAPI.create({ ...form, projectId: Number(form.projectId) });
      setShowModal(false);
      setForm({ version: '', description: '', releaseNotes: '', artifactUrl: '', projectId: '' });
      loadData();
    } catch (err) { alert(err.response?.data?.error || 'Error creating release'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this release?')) return;
    try { await releaseAPI.delete(id); loadData(); }
    catch (err) { alert(err.response?.data?.error || 'Error deleting'); }
  };

  const formatDate = (d) => d ? new Date(d).toLocaleDateString() : '—';

  if (loading) return <div className="empty-state"><div className="icon">⏳</div><h3>Loading...</h3></div>;

  return (
    <div>
      <div className="page-header" style={{display:'flex', justifyContent:'space-between', alignItems:'flex-start'}}>
        <div><h2>Releases</h2><p>Track application versions</p></div>
        <button className="btn btn-primary" onClick={() => { setForm({ version:'', description:'', releaseNotes:'', artifactUrl:'', projectId: projects[0]?.id || '' }); setShowModal(true); }}>
          + New Release
        </button>
      </div>

      {releases.length === 0 ? (
        <div className="glass-card empty-state"><div className="icon">🏷️</div><h3>No releases yet</h3><p>Create a release for one of your projects</p></div>
      ) : (
        <div className="data-table-wrapper">
          <table className="data-table">
            <thead><tr><th>Version</th><th>Project</th><th>Description</th><th>Deployments</th><th>Created By</th><th>Date</th><th>Actions</th></tr></thead>
            <tbody>
              {releases.map(r => (
                <tr key={r.id}>
                  <td style={{fontWeight:700, color:'var(--accent-blue)', fontFamily:'monospace'}}>{r.version}</td>
                  <td>{r.projectName}</td>
                  <td style={{maxWidth:'200px'}} className="truncate">{r.description || '—'}</td>
                  <td><span className="badge in-progress">{r.deploymentCount}</span></td>
                  <td>{r.createdBy}</td>
                  <td style={{whiteSpace:'nowrap'}}>{formatDate(r.createdAt)}</td>
                  <td>
                    {user?.role === 'ADMIN' && (
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(r.id)}>🗑️</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>New Release</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Project</label>
                <select className="form-control" value={form.projectId} onChange={e => setForm({...form, projectId: e.target.value})} required>
                  <option value="">Select project</option>
                  {projects.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Version</label>
                <input type="text" className="form-control" placeholder="e.g. v1.2.0" value={form.version} onChange={e => setForm({...form, version: e.target.value})} required />
              </div>
              <div className="form-group">
                <label>Description</label>
                <input type="text" className="form-control" placeholder="Brief summary" value={form.description} onChange={e => setForm({...form, description: e.target.value})} />
              </div>
              <div className="form-group">
                <label>Release Notes</label>
                <textarea className="form-control" placeholder="What changed..." value={form.releaseNotes} onChange={e => setForm({...form, releaseNotes: e.target.value})} />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Create Release</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
