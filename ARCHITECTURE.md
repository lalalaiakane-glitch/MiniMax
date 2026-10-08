# ARCHITECTURE：MiniMax H3 應用基礎

## 1. 總覽

```
[Vue3 前端 (Vite)]  <--REST/JSON-->  [Spring Boot 後端]  <--REST/queue polling-->  [fal.ai H3 API]
                                            |
                                       [H2/PostgreSQL]
                                     （Job 紀錄持久化）
```

- 前端：Vue 3 + Vite + Vue Router，純 SPA，呼叫後端 REST API，不直接接觸 fal.ai（API Key 不落地前端）
- 後端：Spring Boot（Java 17），負責：
  1. 封裝 fal.ai 的 queue-based API（submit / status / result）
  2. Job 的建立、狀態更新、查詢，用 Spring Data JPA 持久化
  3. 對前端提供簡單、與 fal.ai 無關的 REST 介面（未來若要換供應商或改接官方 API / 自架模型，前端完全不用改）
- 資料庫：先用 H2（檔案模式，方便本機開發、零安裝），未來要多人共用/上正式環境時換 PostgreSQL（JPA 讓這件事只是改設定）

## 2. 為什麼這樣設計

- **後端當防腐層（anti-corruption layer）**：fal.ai 的 API 是「先 submit 拿 request_id，再輪詢 status，最後取 result」的非同步隊列模式，且不同生成類型（text-to-video / image-to-video / reference-to-video）參數形狀略有不同。把這些細節封裝在後端的 `FalH3Client`，前端只看到統一的 `POST /api/jobs`、`GET /api/jobs/{id}`。
- **不把 API Key 放前端**：fal.ai 用 API Key 驗證，Key 只存在後端的 `application-local.yml`（不進 git），前端完全不知道 Key 的存在。
- **先 API 後自架**：H3-Base 開源權重雖然存在，但本地推論門檻高（33B 參數、多 GPU），MVP 先用 fal.ai serverless API 驗證產品方向，把「是否自架」留給有明確流量與成本誘因時再評估。
- **輪詢優先於 Webhook**：fal.ai 支援兩種模式，但 webhook 需要對外可達的網址（內網開發環境不方便），MVP 先用後端主動輪詢（前端打 `GET /api/jobs/{id}` 時，若任務仍在進行中，後端順便去 fal.ai 問一次最新狀態），之後正式上線再考慮 webhook 降低輪詢成本。

## 3. 後端模組

```
backend/
  src/main/java/com/rainforest/minimax/
    MinimaxApplication.java          # Spring Boot 啟動類
    config/
      FalProperties.java             # 讀取 fal.api-key / fal.base-url 設定
      WebClientConfig.java           # 建立呼叫 fal.ai 用的 WebClient
    client/
      FalH3Client.java               # 封裝 submit / status / result 三個動作
      dto/
        FalSubmitResponse.java       # { request_id, status_url, response_url }
        FalStatusResponse.java       # { status, ... }
        FalVideoResult.java          # { video: { url, file_name, file_size, content_type }, expanded_prompt }
    job/
      JobType.java                   # enum: TEXT_TO_VIDEO, IMAGE_TO_VIDEO, REFERENCE_TO_VIDEO
      JobStatus.java                 # enum: PENDING, IN_PROGRESS, SUCCEEDED, FAILED
      GenerationJob.java             # JPA entity
      JobRepository.java             # Spring Data JPA repository
      JobService.java                # 建立任務、呼叫 FalH3Client、更新狀態的商業邏輯
      JobController.java             # REST endpoints
      dto/
        CreateJobRequest.java        # 前端送出的建立任務請求
        JobResponse.java             # 回給前端的任務資料
  src/main/resources/
    application.yml                  # server port、DB 設定、預設 profile=local
    application-local.yml            # 真實 fal.ai API Key（不進 git，見 .gitignore）
    application-local.yml.example    # 範本，可安心 commit
```

### 3.1 資料模型（GenerationJob）

| 欄位 | 型別 | 說明 |
|---|---|---|
| id | Long | 主鍵 |
| type | JobType | TEXT_TO_VIDEO / IMAGE_TO_VIDEO / REFERENCE_TO_VIDEO |
| prompt | String (TEXT) | 使用者輸入的文字描述 |
| paramsJson | String (TEXT) | 該次請求送給 fal.ai 的完整參數（JSON 序列化），方便重現與除錯 |
| falRequestId | String | fal.ai 回傳的 request_id，用於後續查詢狀態 |
| statusUrl / responseUrl | String | fal.ai 回傳的輪詢網址 |
| status | JobStatus | PENDING / IN_PROGRESS / SUCCEEDED / FAILED |
| videoUrl | String | 生成成功後的影片網址 |
| errorMessage | String | 失敗原因 |
| createdAt / updatedAt | Instant | 時間戳記 |

### 3.2 REST API

- `POST /api/jobs` — 建立並送出一個生成任務
  - Request: `{ type, prompt, resolution, aspectRatio, duration, seed, imageUrl, referenceImageUrls[], referenceVideoUrls[], referenceAudioUrls[] }`
  - Response: `JobResponse`（含 id、status=PENDING 或 IN_PROGRESS）
- `GET /api/jobs` — 列出所有任務（分頁，預設依 createdAt desc）
- `GET /api/jobs/{id}` — 取得單一任務；若狀態為 IN_PROGRESS，後端會先向 fal.ai 查一次最新狀態再回傳
- `POST /api/jobs/{id}/retry` — 用同樣參數重新送出一個新任務（v2 可做）

### 3.3 呼叫 fal.ai 的流程（FalH3Client）

依 fal.ai 官方文件，H3 三種 model id：
- `minimax/h3/text-to-video`
- `minimax/h3/image-to-video`
- `minimax/h3/reference-to-video`

流程：
1. `submit(modelId, payload)` → `POST https://queue.fal.run/{modelId}`，Header `Authorization: Key ${FAL_KEY}`，回傳 `{ request_id, status_url, response_url }`
2. `pollStatus(statusUrl)` → `GET status_url`，回傳目前狀態（IN_QUEUE / IN_PROGRESS / COMPLETED 等，實際欄位以官方為準，程式上用防禦性解析）
3. 狀態完成後 `getResult(responseUrl)` → `GET response_url`，取回 `{ video: { url, ... }, expanded_prompt }`，把 `video.url` 存進 `GenerationJob.videoUrl`

## 4. 前端模組

```
frontend/
  src/
    api/client.js        # axios 實例，baseURL 指向後端
    router/index.js       # / -> CreateJobView, /jobs -> JobListView
    views/
      CreateJobView.vue   # 建立任務表單（依類型切換欄位）
      JobListView.vue     # 任務清單／畫廊，輪詢 IN_PROGRESS 任務
    App.vue
    main.js
```

- 狀態管理先不上 Pinia，用元件內 `ref`/`reactive` 即可，等功能變複雜再抽
- 影片預覽直接用原生 `<video controls>` 標籤指向 `videoUrl`

## 5. 設定與密鑰管理

- `FAL_KEY` 放在 `backend/src/main/resources/application-local.yml`（已加進 .gitignore，不會進 git），**不要**寫進其他程式碼或 commit 進 git；正式上線環境再視需要改用系統環境變數或密鑰管理服務
- `application.yml`（會進 git）只留 `${FAL_KEY:}` 空值佔位，真實金鑰放在同資料夾的 `application-local.yml`（不進 git），範本是 `application-local.yml.example`

## 6. 已知風險與待驗證事項

- fal.ai 實際回傳的狀態欄位名稱／輪詢節奏，需要用真實 API Key 跑一次才能百分之百確認（文件描述可能與實際回應有細節差異），`FalH3Client` 的解析要寫得寬鬆一點、加 log
- 費用：pay-per-use，需要在串接前到 fal.ai 後台確認單價，避免測試時無限重試造成非預期花費
- H3 目前為新發布模型，API 介面未來可能調整，`FalH3Client` 集中封裝就是為了讓這類變動只影響一個檔案
