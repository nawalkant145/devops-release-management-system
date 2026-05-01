import { useState, useEffect } from 'react';
import { projectAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function Projects() {
  const { user } = useAuth();
  const [projects, setProjects] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ name: '', description: '', repoUrl: '' });
  const [editId, setEditId] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => { loadProjects(); }, []);

  const loadProjects = async () => {
    try {
      const res = await projectAPI.getAll();
      setProjects(res.data);
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editId) {
        await projectAPI.update(editId, form);
      } else {
        await projectAPI.create(form);
      }
      setShowModal(false);
      setForm({ name: '', description: '', repoUrl: '' });
      setEditId(null);
      loadProjects();
    } catch (err) { alert(err.response?.data?.error || 'Error saving project'); }
  };

  const handleEdit = (p) => {
    setForm({ name: p.name, description: p.description || '', repoUrl: p.repoUrl || '' });
    setEditId(p.id);
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this project and all its releases?')) return;
    try {
      await projectAPI.delete(id);
      loadProjects();
    } catch (err) { alert(err.response?.data?.error || 'Error deleting project'); }
  };

  const formatDate = (d) => d ? new Date(d).toLocaleDateString() : '—';

  if (loading) return <div className="empty-state"><div className="icon">⏳</div><h3>Loading...</h3></div>;

  return (
    <div>
      <div className="page-header" style={{display:'flex', justifyContent:'space-between', alignItems:'flex-start'}}>
        <div>
          <h2>Projects</h2>
          <p>Manage your application projects</p>
        </div>
        <button className="btn btn-primary" onClick={() => { setForm({ name:'', description:'', repoUrl:'' }); setEditId(null); setShowModal(true); }}>
          + New Project
        </button>
      </div>

      {projects.length === 0 ? (
        <div className="glass-card empty-state">
          <div className="icon">📁</div>
          <h3>No projects yet</h3>
          <p>Create your first project to get started</p>
        </div>
      ) : (
        <div className="data-table-wrapper">
          <table className="data-table">
            <thead>
              <tr>
                <th>Project</th>
                <th>Description</th>
                <th>Releases</th>
                <th>Created By</th>
                <th>Updated</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {projects.map(p => (
                <tr key={p.id}>
                  <td style={{fontWeight:600, color:'var(--text-primary)'}}>{p.name}</td>
                  <td style={{maxWidth:'250px'}} className="truncate">{p.description || '—'}</td>
                  <td><span className="badge in-progress">{p.releaseCount} releases</span></td>
                  <td>{p.createdBy || '—'}</td>
                  <td style={{whiteSpace:'nowrap'}}>{formatDate(p.updatedAt)}</td>
                  <td>
                    <div style={{display:'flex', gap:'8px'}}>
                      <button className="btn btn-secondary btn-sm" onClick={() => handleEdit(p)}>✏️ Edit</button>
                      {user?.role === 'ADMIN' && (
                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(p.id)}>🗑️</button>
                      )}
                    </div>
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
              <h3>{editId ? 'Edit Project' : 'New Project'}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Project Name</label>
                <input type="text" className="form-control" placeholder="e.g. payment-service"
                  value={form.name} onChange={e => setForm({...form, name: e.target.value})} required />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea className="form-control" placeholder="Brief description..."
                  value={form.description} onChange={e => setForm({...form, description: e.target.value})} />
              </div>
              <div className="form-group">
                <label>Repository URL</label>
                <input type="text" className="form-control" placeholder="https://github.com/..."
                  value={form.repoUrl} onChange={e => setForm({...form, repoUrl: e.target.value})} />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">{editId ? 'Save Changes' : 'Create Project'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
