# TASKS：MiniMax H3 應用基礎

> 狀態標記：`[ ]` 待辦　`[x]` 已完成（本次由 Claude 建立骨架的部分會標記 [x]，實際商業邏輯／串接仍需你補上 API Key 並跑通）

## Phase 0 — 專案基礎
- [x] 建立 PRD.md / ARCHITECTURE.md / TASKS.md / CLAUDE.md
- [x] 建立 backend（Spring Boot）與 frontend（Vue3 + Vite）目錄骨架

## Phase 1 — 後端骨架
- [x] pom.xml（Spring Boot Web, Data JPA, H2, Validation, WebFlux for WebClient）
- [x] GenerationJob 實體、JobType / JobStatus enum、JobRepository
- [x] FalH3Client（submit / pollStatus / getResult 骨架，含防禦性解析）
- [x] JobService、JobController、DTOs
- [x] application.yml（H2 檔案模式、預設啟用 local profile）＋ application-local.yml.example（金鑰範本）
- [ ] 申請 fal.ai 帳號與 API Key，填入 `backend/src/main/resources/application-local.yml`
- [x] 修掉 FalH3Client 的 URL 組裝 bug（modelId 含 `/` 被 Spring 當成路徑變數編碼成 `%2F`，打 fal.ai 回 404）
- [ ] 用 `POST /api/jobs`（Text-to-Video）重新跑通第一個真實請求，確認 fal.ai 實際回應格式與文件一致
- [x] 403 Forbidden 排查（第一輪）：懷疑一般版 H3 需要帳號餘額，改成 h3-max 測試
- [x] 403 Forbidden 排查（第二輪，正解）：H3 Max「每日 5 次免費」只開放給 fal.ai 網站互動工具，API 呼叫（不管 h3 或 h3-max）一律按用量付費，沒有免費額度。根本原因是帳號沒有餘額/付款方式
- [ ] 去 fal.ai dashboard 加值/綁定付款方式（H3 Max 促銷價約 480p $0.025/秒、768p $0.04/秒，測試成本很低），完成後重測
- [ ] 決定正式使用一般版 `minimax/h3/*` 還是先用促銷價的 `minimax/h3-max/*`
- [ ] `reference-to-video` 的 h3-max model id 未 100% 核對過，真的要用這條路徑時記得先確認
- [ ] 依實際回應調整 FalStatusResponse / FalVideoResult 的欄位解析
- [ ] Image-to-Video、Reference-to-Video 兩條路徑跑通

## Phase 2 — 前端骨架
- [x] Vite + Vue3 專案結構、Router、axios client
- [x] CreateJobView（表單，依類型切換欄位）
- [x] JobListView（清單、輪詢、影片預覽）
- [ ] 串接後端跑通建立任務 → 顯示結果的完整流程
- [ ] 基本 UI 打磨（loading、錯誤提示、空狀態）

## Phase 3 — 驗證與收斂
- [ ] 三種生成類型至少各跑通一次，記錄實際生成時間與品質，附幾個範例 prompt 存成 README 的「快速上手範例」
- [ ] 確認費用計算方式，評估目前用量下的預估月成本
- [ ] 決定下一步方向（見 PRD 第 7 節：場景化模板 / 批次生成 / 對外服務化）

## 待你決定的事項
1. fal.ai 的 API Key 從哪裡申請、要不要用公司或個人帳號
2. 資料庫要不要一開始就上 PostgreSQL（若之後要多人共用建議直接上，H2 只適合單機測試）
3. 是否要接你目前的 GitLab（app-goods 同一個內部 GitLab）來管理這個新專案的版本
