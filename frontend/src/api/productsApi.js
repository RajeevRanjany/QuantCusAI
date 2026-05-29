import api from './axios'

export const listProducts = (jobId) =>
  api.get('/products', { params: { jobId } })

export const getProduct = (productId) => api.get(`/products/${productId}`)

export const updateProduct = (productId, data) =>
  api.put(`/products/${productId}`, data)

export const enhanceTitle = (productId) =>
  api.post(`/products/${productId}/enhance-title`)
