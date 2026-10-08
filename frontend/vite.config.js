import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // 開發時前端呼叫 /api/* 會被轉發到後端 8080，避免 CORS 麻煩
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
