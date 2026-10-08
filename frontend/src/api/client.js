import axios from 'axios'

// 開發模式下走 vite 的 proxy（見 vite.config.js），正式部署時可改成完整後端網址
export const api = axios.create({
  baseURL: '/api'
})

export function createJob(payload) {
  return api.post('/jobs', payload).then(res => res.data)
}

export function listJobs() {
  return api.get('/jobs').then(res => res.data)
}

export function getJob(id) {
  return api.get(`/jobs/${id}`).then(res => res.data)
}
