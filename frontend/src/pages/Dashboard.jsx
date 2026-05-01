import { useState, useEffect } from 'react';
import { deploymentAPI, projectAPI, releaseAPI, auditAPI } from '../services/api';

export default function Dashboard() {
  const [stats, setStats] = useState({ total: 0, success: 0, failed: 0, pending: 0, rolledBack: 0 });
  const [projects, setProjects] = useState([]);
  const [recentLogs, setRecentLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const [statsRes, projRes, logsRes] = await Promise.all([
        deploymentAPI.getStats(),
        projectAPI.getAll(),
        auditAPI.getAll(),
      ]);
      setStats(statsRes.data);
      setProjects(projRes.data);
      setRecentLogs(logsRes.data.slice(0, 8));
    } catch (err) {
      console.error('Dashboard load error:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (d) => d ? new Date(d).toLocaleString() : '—';

  if (loading) return <div className="empty-state"><div className="icon">⏳</div><h3>Loading dashboard...</h3></div>;

  return (
    <div>
      <div className="page-header">
        <h2>Dashboard</h2>
        <p>Overview of your release management system</p>
      </div>

      <div className="stat-grid">
        <div className="stat-card blue">
          <div className="stat-icon">📦</div>
          <div className="stat-value">{projects.length}</div>
          <div className="stat-label">Total Projects</div>
        </div>
        <div className="stat-card green">
          <div className="stat-icon">✅</div>
          <div className="stat-value">{stats.success || 0}</div>
          <div className="stat-label">Successful Deploys</div>
        </div>
        <div className="stat-card rose">
          <div className="stat-icon">❌</div>
          <div className="stat-value">{stats.failed || 0}</div>
          <div className="stat-label">Failed Deploys</div>
        </div>
        <div className="stat-card amber">
          <div className="stat-icon">🔄</div>
          <div className="stat-value">{stats.rolledBack || 0}</div>
          <div className="stat-label">Rollbacks</div>
        </div>
      </div>

      <div className="data-table-wrapper">
        <div className="data-table-header">
          <h3>📋 Recent Activity</h3>
        </div>
        {recentLogs.length === 0 ? (
          <div className="empty-state">
            <div className="icon">📭</div>
            <h3>No activity yet</h3>
            <p>Start by creating a project</p>
          </div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Action</th>
                <th>Entity</th>
                <th>Details</th>
                <th>User</th>
                <th>Time</th>
              </tr>
            </thead>
            <tbody>
              {recentLogs.map(log => (
                <tr key={log.id}>
                  <td><span className={`badge ${log.action === 'DEPLOY' ? 'success' : log.action === 'ROLLBACK' ? 'rolled-back' : 'in-progress'}`}>{log.action}</span></td>
                  <td>{log.entity}</td>
                  <td style={{maxWidth: '300px'}} className="truncate">{log.details}</td>
                  <td>{log.performedBy}</td>
                  <td style={{whiteSpace:'nowrap'}}>{formatDate(log.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
