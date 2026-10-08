<template>
  <div>
    <h2>任務清單</h2>
    <button class="refresh" @click="loadJobs">重新整理</button>

    <p v-if="jobs.length === 0" class="empty">還沒有任務，去「建立任務」頁面送出第一個吧。</p>

    <div v-for="job in jobs" :key="job.id" class="job-card">
      <div class="job-header">
        <span class="job-type">{{ typeLabel(job.type) }}</span>
        <span :class="['job-status', job.status.toLowerCase()]">{{ statusLabel(job.status) }}</span>
      </div>
      <p class="job-prompt">{{ job.prompt }}</p>
      <p class="job-time">建立於 {{ formatTime(job.createdAt) }}</p>

      <video v-if="job.status === 'SUCCEEDED' && job.videoUrl" :src="job.videoUrl" controls></video>
      <p v-if="job.status === 'FAILED'" class="error">{{ job.errorMessage || '生成失敗' }}</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { listJobs, getJob } from '../api/client'

const jobs = ref([])
let pollTimer = null

async function loadJobs() {
  jobs.value = await listJobs()
}

async function pollInProgress() {
  const inProgress = jobs.value.filter(j => j.status === 'IN_PROGRESS' || j.status === 'PENDING')
  for (const job of inProgress) {
    try {
      const updated = await getJob(job.id)
      const idx = jobs.value.findIndex(j => j.id === job.id)
      if (idx !== -1) jobs.value[idx] = updated
    } catch (e) {
      // 單一任務查詢失敗不影響其他任務，靜默略過，下一輪再試
    }
  }
}

function typeLabel(type) {
  return {
    TEXT_TO_VIDEO: '文生視訊',
    IMAGE_TO_VIDEO: '圖生視訊',
    REFERENCE_TO_VIDEO: '參考生視訊'
  }[type] || type
}

function statusLabel(status) {
  return {
    PENDING: '等待中',
    IN_PROGRESS: '生成中',
    SUCCEEDED: '已完成',
    FAILED: '失敗'
  }[status] || status
}

function formatTime(iso) {
  return new Date(iso).toLocaleString('zh-TW')
}

onMounted(async () => {
  await loadJobs()
  pollTimer = setInterval(pollInProgress, 5000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style scoped>
.refresh {
  margin-bottom: 16px;
  padding: 6px 14px;
  background: #1a1d24;
  border: 1px solid #2c313c;
  border-radius: 6px;
  color: #eaeaea;
  cursor: pointer;
}
.empty {
  color: #9aa1ad;
}
.job-card {
  border: 1px solid #262a33;
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 12px;
  background: #14161c;
}
.job-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}
.job-type {
  font-size: 12px;
  color: #9db4ff;
}
.job-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
}
.job-status.pending { background: #3a3d1e; color: #e4e08a; }
.job-status.in_progress { background: #1e2f3a; color: #8ac6e4; }
.job-status.succeeded { background: #1e3a24; color: #8ae4a0; }
.job-status.failed { background: #3a1e1e; color: #e48a8a; }
.job-prompt {
  margin: 6px 0;
}
.job-time {
  font-size: 12px;
  color: #6b7280;
}
video {
  width: 100%;
  max-height: 360px;
  border-radius: 6px;
  margin-top: 8px;
}
.error {
  color: #ff6b6b;
}
</style>
