# MiniMax H3 創作工具（應用基礎）

呼叫 MiniMax H3 全模態影音生成模型（透過 fal.ai API）的通用創作工具骨架。

- 需求規劃：見 `PRD.md`
- 技術設計：見 `ARCHITECTURE.md`
- 目前進度／待辦：見 `TASKS.md`
- 給 Claude/協作者的專案說明：見 `CLAUDE.md`

## 快速開始

### 1. 設定 fal.ai API Key（不用系統環境變數）

打開 `backend/src/main/resources/application-local.yml`，把裡面的 `貼上你的-fal-api-key` 換成你真實的 key，存檔：

```yaml
fal:
  api-key: 你真實的fal.ai金鑰
```

這個檔案已經被 `.gitignore` 排除，不會被 commit 上 git，也不需要設定任何系統環境變數——後端啟動時預設會讀這個檔案（Spring 的 `local` profile，見 `application.yml` 的 `spring.profiles.active: local`）。

範本在同資料夾的 `application-local.yml.example`，如果 `application-local.yml` 不見了可以照著複製一份。

### 2. 啟動後端
```bash
cd backend
mvn spring-boot:run
```
預設在 http://localhost:8080，H2 資料庫檔案會建立在 `backend/data/`。

### 3. 啟動前端
```bash
cd frontend
npm install
npm run dev
```
預設在 http://localhost:5173，開發模式下 `/api/*` 會自動轉發到後端 8080（見 `vite.config.js`）。

### 4. 試跑
打開 http://localhost:5173，選「文生視訊 Text-to-Video」，輸入一段 prompt，送出後到「任務清單」頁面觀察狀態（約 1-3 分鐘後應該會變成「已完成」並可播放影片）。

## 目前狀態
這是一個**尚未用真實 API Key 跑通過**的骨架（詳見 `TASKS.md`）。第一次串接時請特別留意 fal.ai 實際回應格式是否與 `client/dto/` 底下的假設一致，有落差就直接調整那幾個 DTO。
