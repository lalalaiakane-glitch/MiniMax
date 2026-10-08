# CLAUDE.md — MiniMax H3 應用基礎

給未來在這個 repo 工作的 Claude（或任何協作者）看的專案說明。

## 這是什麼
一個呼叫 MiniMax H3（全模態影音生成模型）API 的通用創作工具基礎架構。後端 Spring Boot、前端 Vue3，先接 fal.ai 的 serverless API，不自架模型。詳見 `PRD.md`（需求）與 `ARCHITECTURE.md`（技術設計）。目前進度見 `TASKS.md`。

## 重要慣例
- 密鑰（`FAL_KEY`）放在 `backend/src/main/resources/application-local.yml`（已被 .gitignore 排除，不用系統環境變數），絕不 commit 進 git 或寫死在其他程式碼裡
- fal.ai 的呼叫全部集中在 `backend/.../client/FalH3Client.java`，其他地方不要直接打 fal.ai 的網址
- 三種生成類型（text-to-video / image-to-video / reference-to-video）共用同一組 Job 資料表與 API，用 `JobType` 區分，不要為每種類型各開一套 controller/entity
- 資料庫先用 H2 檔案模式（見 `application.yml`），schema 用 JPA `ddl-auto: update` 自動產生，方便開發期迭代；正式環境要換 PostgreSQL 時只需改 `application.yml` 的 datasource

## 常用指令
```bash
# 後端
cd backend
mvn spring-boot:run

# 前端
cd frontend
npm install
npm run dev
```

## 尚未串接、需要人工確認的部分
fal.ai 實際回應格式（狀態欄位名稱、輪詢時機）以官方文件為準寫了防禦性解析，但**尚未用真實 API Key 跑過**，第一次串接時很可能要依實際回應微調 `FalStatusResponse` / `FalVideoResult`。這是目前優先度最高的待辦（見 `TASKS.md` Phase 1）。

## 文件更新原則
- 需求變了先改 `PRD.md`
- 技術決策變了先改 `ARCHITECTURE.md`（尤其是「為什麼這樣設計」段落，決策的理由比結論更重要）
- 每次完成一批工作，回來勾選 `TASKS.md`
