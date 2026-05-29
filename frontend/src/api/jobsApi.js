import api from './axios'

export const listJobs = (page = 0, size = 10) =>
  api.get('/jobs', { params: { page, size } })

export const getJob = (jobId) => api.get(`/jobs/${jobId}`)
