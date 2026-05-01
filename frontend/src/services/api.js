import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export const authAPI = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
};

export const projectAPI = {
  getAll: () => api.get('/projects'),
  getById: (id) => api.get(`/projects/${id}`),
  create: (data) => api.post('/projects', data),
  update: (id, data) => api.put(`/projects/${id}`, data),
  delete: (id) => api.delete(`/projects/${id}`),
};

export const releaseAPI = {
  getAll: () => api.get('/releases'),
  getByProject: (projectId) => api.get(`/releases/project/${projectId}`),
  getById: (id) => api.get(`/releases/${id}`),
  create: (data) => api.post('/releases', data),
  delete: (id) => api.delete(`/releases/${id}`),
};

export const deploymentAPI = {
  getAll: () => api.get('/deployments'),
  getById: (id) => api.get(`/deployments/${id}`),
  getByRelease: (releaseId) => api.get(`/deployments/release/${releaseId}`),
  deploy: (data) => api.post('/deployments', data),
  rollback: (id) => api.post(`/deployments/${id}/rollback`),
  getStats: () => api.get('/deployments/stats'),
};

export const auditAPI = {
  getAll: () => api.get('/audit-logs'),
  getByEntity: (entity, entityId) => api.get(`/audit-logs/entity/${entity}/${entityId}`),
};

export default api;
