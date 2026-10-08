import { createRouter, createWebHistory } from 'vue-router'
import CreateJobView from '../views/CreateJobView.vue'
import JobListView from '../views/JobListView.vue'

const routes = [
  { path: '/', name: 'create', component: CreateJobView },
  { path: '/jobs', name: 'jobs', component: JobListView }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
