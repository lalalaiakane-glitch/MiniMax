<template>
  <div>
    <h2>建立生成任務</h2>

    <label class="field">
      <span>生成類型</span>
      <select v-model="form.type">
        <option value="TEXT_TO_VIDEO">文生視訊 Text-to-Video</option>
        <option value="IMAGE_TO_VIDEO">圖生視訊 Image-to-Video</option>
        <option value="REFERENCE_TO_VIDEO">參考生視訊 Reference-to-Video</option>
      </select>
    </label>

    <label class="field">
      <span>Prompt</span>
      <textarea v-model="form.prompt" rows="4" placeholder="描述想要的畫面、動作、風格..."></textarea>
    </label>

    <template v-if="form.type === 'IMAGE_TO_VIDEO'">
      <label class="field">
        <span>起始幀圖片網址</span>
        <input v-model="form.imageUrl" placeholder="https://..." />
      </label>
      <label class="field">
        <span>結束幀圖片網址（選填）</span>
        <input v-model="form.endImageUrl" placeholder="https://..." />
      </label>
    </template>

    <template v-if="form.type === 'REFERENCE_TO_VIDEO'">
      <label class="field">
        <span>參考圖片網址（每行一個，最多 9 個）</span>
        <textarea v-model="referenceImageUrlsText" rows="3" placeholder="https://...&#10;https://..."></textarea>
      </label>
      <label class="field">
        <span>參考影片網址（每行一個，最多 3 個，單段 2-15 秒）</span>
        <textarea v-model="referenceVideoUrlsText" rows="2"></textarea>
      </label>
      <label class="field">
        <span>參考音訊網址（每行一個，最多 3 個，單段 2-15 秒）</span>
        <textarea v-model="referenceAudioUrlsText" rows="2"></textarea>
      </label>
      <p class="hint">提醒：圖片＋影片＋音訊合計不能超過 12 個檔案。</p>
    </template>

    <div class="grid">
      <label class="field">
        <span>時長（秒）</span>
        <input type="number" v-model.number="form.duration" min="1" max="15" />
      </label>
      <label class="field">
        <span>解析度</span>
        <select v-model="form.resolution">
          <option value="480P">480P</option>
          <option value="768P">768P</option>
          <option value="2K">2K</option>
          <option value="4K">4K</option>
        </select>
      </label>
      <label class="field">
        <span>比例</span>
        <select v-model="form.aspectRatio">
          <option value="16:9">16:9</option>
          <option value="9:16">9:16</option>
          <option value="1:1">1:1</option>
          <option value="4:3">4:3</option>
          <option value="3:4">3:4</option>
          <option value="21:9">21:9</option>
          <option v-if="form.type === 'REFERENCE_TO_VIDEO'" value="adaptive">adaptive</option>
        </select>
      </label>
    </div>

    <button :disabled="submitting || !form.prompt" @click="submit">
      {{ submitting ? '送出中...' : '送出生成任務' }}
    </button>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="createdJobId" class="success">
      任務已建立（id={{ createdJobId }}），前往
      <RouterLink to="/jobs">任務清單</RouterLink>
      查看進度。
    </p>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { createJob } from '../api/client'

const form = reactive({
  type: 'TEXT_TO_VIDEO',
  prompt: '',
  duration: 5,
  resolution: '2K',
  aspectRatio: '16:9',
  imageUrl: '',
  endImageUrl: ''
})

const referenceImageUrlsText = ref('')
const referenceVideoUrlsText = ref('')
const referenceAudioUrlsText = ref('')

const submitting = ref(false)
const errorMessage = ref('')
const createdJobId = ref(null)

function linesToList(text) {
  return text
    .split('\n')
    .map(s => s.trim())
    .filter(Boolean)
}

async function submit() {
  submitting.value = true
  errorMessage.value = ''
  createdJobId.value = null

  const payload = {
    type: form.type,
    prompt: form.prompt,
    duration: form.duration,
    resolution: form.resolution,
    aspectRatio: form.aspectRatio
  }

  if (form.type === 'IMAGE_TO_VIDEO') {
    payload.imageUrl = form.imageUrl
    payload.endImageUrl = form.endImageUrl || undefined
  }

  if (form.type === 'REFERENCE_TO_VIDEO') {
    payload.referenceImageUrls = linesToList(referenceImageUrlsText.value)
    payload.referenceVideoUrls = linesToList(referenceVideoUrlsText.value)
    payload.referenceAudioUrls = linesToList(referenceAudioUrlsText.value)
  }

  try {
    const job = await createJob(payload)
    createdJobId.value = job.id
  } catch (e) {
    errorMessage.value = e?.response?.data?.message || e.message || '送出失敗'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.field {
  display: block;
  margin-bottom: 14px;
}
.field span {
  display: block;
  margin-bottom: 4px;
  font-size: 13px;
  color: #9aa1ad;
}
input, select, textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 8px 10px;
  background: #1a1d24;
  border: 1px solid #2c313c;
  border-radius: 6px;
  color: #eaeaea;
  font-size: 14px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
button {
  margin-top: 8px;
  padding: 10px 20px;
  background: #4c6fff;
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
  font-size: 14px;
}
button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.hint {
  font-size: 12px;
  color: #9aa1ad;
}
.error {
  color: #ff6b6b;
}
.success {
  color: #6bffa0;
}
</style>
