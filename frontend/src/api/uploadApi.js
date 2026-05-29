import api from './axios'

export const uploadVideo = (file, enhanceTitle) => {
  const form = new FormData()
  form.append('file', file)
  form.append('enhanceTitle', enhanceTitle)
  return api.post('/upload-video', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export const uploadCsv = (file, enhanceTitle) => {
  const form = new FormData()
  form.append('file', file)
  form.append('enhanceTitle', enhanceTitle)
  return api.post('/upload-products-csv', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
