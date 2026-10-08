<template>
  <div class="app">
    <header class="app-header">
      <h1>MiniMax H3 創作工具</h1>
      <nav>
        <RouterLink to="/">建立任務</RouterLink>
        <RouterLink to="/jobs">任務清單</RouterLink>
      </nav>
    </header>
    <main>
      <RouterView />
    </main>
  </div>
</template>

<script setup>
</script>

<style>
body {
  margin: 0;
  font-family: -apple-system, "Segoe UI", "PingFang TC", "Microsoft JhengHei", sans-serif;
  background: #0f1115;
  color: #eaeaea;
}
.app-header {
  padding: 16px 24px;
  border-bottom: 1px solid #262a33;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.app-header nav a {
  margin-left: 16px;
  color: #9db4ff;
  text-decoration: none;
}
main {
  max-width: 860px;
  margin: 0 auto;
  padding: 24px;
}
</style>
