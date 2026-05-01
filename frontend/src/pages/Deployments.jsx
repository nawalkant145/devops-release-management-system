import { useState, useEffect } from 'react';
import { deploymentAPI, releaseAPI } from '../services/api';

export default function Deployments() {
  const [deployments, setDeployments] = useState([]);
  const [releases, setReleases] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ releaseId: '', environment: 'DEVELOPMENT' });
  const [loading, setLoading] = useState(true);

  useEffect(() => { loadData(); }, []);

  const loadData = async () => {
    try {
      const [depRes, relRes] = await Promise.all([deploymentAPI.getAll(), releaseAPI.getAll()]);
      setDeployments(depRes.data);
      setReleases(relRes.data);
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  const handleDeploy = async (e) => {
    e.preventDefault();
    try {
      await deploymentAPI.deploy({ releaseId: Number(form.releaseId), environment: form.environment });
      setShowModal(false);
      loadData();
    } catch (err) { alert(err.response?.data?.error || 'Deploy failed'); }
  };

  const handleRollback = async (id) => {
    if (!confirm('Rollback this deployment?')) return;
    try { await deploymentAPI.rollback(id); loadData(); }
    catch (err) { alert(err.response?.data?.error || 'Rollback failed'); }
  };

  const statusClass = (s) => {
    const map = { SUCCESS: 'success', FAILED: 'failed', PENDING: 'pending', IN_PROGRESS: 'in-progress', ROLLED_BACK: 'rolled-back' };
    return map[s] || '';
  };

  const formatDate = (d) => d ? new Date(d).toLocaleString() : '—';

  if (loading) return <div className="empty-state"><div className="icon">⏳</div><h3>Loading...</h3></div>;

  return (
    <div>
      <div className="page-header" style={{display:'flex', justifyContent:'space-between', alignItems:'flex-start'}}>
        <div><h2>Deployments</h2><p>Deploy releases and manage rollbacks</p></div>
        <button className="btn btn-success" onClick={() => { setForm({ releaseId: releases[0]?.id || '', environment: 'DEVELOPMENT' }); setShowModal(true); }}>
          🚀 New Deployment
        </button>
      </div>

      {deployments.length === 0 ? (
        <div className="glass-card empty-state"><div className="icon">🚀</div><h3>No deployments yet</h3><p>Deploy a release to get started</p></div>
      ) : (
        <div className="data-table-wrapper">
          <table className="data-table">
            <thead><tr><th>Release</th><th>Project</th><th>Environment</th><th>Status</th><th>Deployed By</th><th>Deployed At</th><th>Actions</th></tr></thead>
            <tbody>
              {deployments.map(d => (
                <tr key={d.id}>
                  <td style={{fontWeight:700, fontFamily:'monospace', color:'var(--accent-blue)'}}>{d.releaseVersion}</td>
                  <td>{d.projectName}</td>
                  <td><span className="badge developer">{d.environment}</span></td>
                  <td><span className={`badge ${statusClass(d.status)}`}><span className="dot"></span>{d.status.replace('_', ' ')}</span></td>
                  <td>{d.deployedBy}</td>
                  <td style={{whiteSpace:'nowrap'}}>{formatDate(d.deployedAt)}</td>
                  <td>
                    {d.status === 'SUCCESS' && (
                      <button className="btn btn-warning btn-sm" onClick={() => handleRollback(d.id)}>↩ Rollback</button>
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
              <h3>🚀 New Deployment</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
            </div>
            <form onSubmit={handleDeploy}>
              <div className="form-group">
                <label>Release</label>
                <select className="form-control" value={form.releaseId} onChange={e => setForm({...form, releaseId: e.target.value})} required>
                  <option value="">Select release</option>
                  {releases.map(r => <option key={r.id} value={r.id}>{r.projectName} — {r.version}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Environment</label>
                <select className="form-control" value={form.environment} onChange={e => setForm({...form, environment: e.target.value})}>
                  <option value="DEVELOPMENT">Development</option>
                  <option value="STAGING">Staging</option>
                  <option value="PRODUCTION">Production</option>
                </select>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-success">🚀 Deploy</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
