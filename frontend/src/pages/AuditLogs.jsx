import { useState, useEffect } from 'react';
import { auditAPI } from '../services/api';

export default function AuditLogs() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => { loadLogs(); }, []);

  const loadLogs = async () => {
    try {
      const res = await auditAPI.getAll();
      setLogs(res.data);
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  const formatDate = (d) => d ? new Date(d).toLocaleString() : '—';

  const actionClass = (action) => {
    const map = { CREATE: 'success', UPDATE: 'in-progress', DELETE: 'failed', DEPLOY: 'success', ROLLBACK: 'rolled-back', LOGIN: 'pending', REGISTER: 'developer' };
    return map[action] || 'in-progress';
  };

  if (loading) return <div className="empty-state"><div className="icon">⏳</div><h3>Loading...</h3></div>;

  return (
    <div>
      <div className="page-header">
        <h2>Audit Logs</h2>
        <p>Complete history of system actions</p>
      </div>

      {logs.length === 0 ? (
        <div className="glass-card empty-state"><div className="icon">📋</div><h3>No audit logs</h3><p>Actions will appear here automatically</p></div>
      ) : (
        <div className="data-table-wrapper">
          <div className="data-table-header">
            <h3>📋 {logs.length} Events Recorded</h3>
          </div>
          <table className="data-table">
            <thead><tr><th>Action</th><th>Entity</th><th>Entity ID</th><th>Details</th><th>Performed By</th><th>Timestamp</th></tr></thead>
            <tbody>
              {logs.map(log => (
                <tr key={log.id}>
                  <td><span className={`badge ${actionClass(log.action)}`}>{log.action}</span></td>
                  <td style={{fontWeight:600}}>{log.entity}</td>
                  <td style={{fontFamily:'monospace'}}>#{log.entityId}</td>
                  <td style={{maxWidth:'300px'}} className="truncate">{log.details}</td>
                  <td>{log.performedBy}</td>
                  <td style={{whiteSpace:'nowrap'}}>{formatDate(log.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
