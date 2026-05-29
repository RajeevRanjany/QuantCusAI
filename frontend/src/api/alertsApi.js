import api from './axios'

export const listAlerts = (jobId) =>
  api.get('/alerts', { params: { jobId } })

export const resolveAlert = (alertId) =>
  api.post(`/alerts/${alertId}/resolve`)
