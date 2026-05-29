import api from './axios'

export const getDashboard = (jobId) =>
  api.get('/dashboard/quality-summary', { params: { jobId } })
