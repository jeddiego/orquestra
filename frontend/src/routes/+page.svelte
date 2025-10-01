<script>
  import Header from "../lib/components/Header.svelte";
  import MissionsPanel from "../lib/components/MissionsPanel.svelte";
  import ChatPanel from "../lib/components/ChatPanel.svelte";
  import CanvasPanel from "../lib/components/CanvasPanel.svelte";
</script>

<div class="top-header-dark">
  <Header />
</div>

<main class="main-grid-container">
  <div class="panel missions-panel">
    <MissionsPanel />
  </div>
  <div class="panel chat-panel">
    <ChatPanel />
  </div>
  <div class="panel canvas-panel">
    <CanvasPanel />
  </div>
</main>

<style>
  .top-header-dark {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 1.5rem;
    height: 60px;
    background-color: var(--bg-primary);
    border-bottom: 1px solid var(--border-color);
    flex-shrink: 0;
  }

  .main-grid-container {
    display: grid;
    grid-template-columns: 350px 1fr 1fr; /* Izquierda más ancha */
    flex-grow: 1;
    height: calc(100vh - 60px);
  }

  .panel {
    height: 100%;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
  }

  .missions-panel {
    background-color: var(--bg-secondary);
    border-right: 1px solid var(--border-color);
  }

  .chat-panel {
    border-right: 1px solid var(--border-color);
  }

  .canvas-panel {
    padding: 1.5rem;
  }
</style>