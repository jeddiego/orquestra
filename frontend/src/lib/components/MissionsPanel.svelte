<script>
  import { missionStatus, missionPhase, missionProgress, isMissionActive } from '../stores/missionStore.js';
</script>

<div class="missions-header">
    <h2>Misiones Activas</h2>
</div>
<div class="missions-list">
    <div class="mission-item" class:active={$isMissionActive}>
        <div class="mission-item-header">
            <h4>Analizar Buyer Personas</h4>
            <span class="status">{$missionStatus}</span>
        </div>
        <div class="mission-item-body">
            <p><strong>Fase Actual:</strong> <span id="mission-phase">{$missionPhase}</span></p>
            <div class="progress-bar">
                <div class="progress" style="width: {$missionProgress}%;"></div>
            </div>
        </div>
    </div>
</div>

<style>
    .missions-header {
        padding: 1.5rem;
        border-bottom: 1px solid var(--border-color);
    }
    .missions-header h2 {
        font-size: 1.25rem;
    }
    .missions-list {
        padding: 1.5rem;
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }
    .mission-item {
        background-color: var(--bg-tertiary);
        border-radius: 8px;
        padding: 1rem;
        border: 1px solid var(--border-color);
        opacity: 0.5;
        transition: all 0.3s ease;
    }
    .mission-item.active {
        opacity: 1;
        border-color: var(--accent-blue);
    }
    .mission-item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }
    .mission-item-header h4 {
        font-weight: 600;
    }
    .mission-item-header .status {
        font-size: 0.875rem;
        padding: 0.25rem 0.5rem;
        border-radius: 12px;
        background-color: var(--bg-secondary);
    }
    .mission-item-body p {
        font-size: 0.875rem;
        color: var(--text-secondary);
        margin-bottom: 0.5rem;
    }
    .progress-bar {
        height: 8px;
        background-color: var(--bg-secondary);
        border-radius: 4px;
        overflow: hidden;
    }
    .progress {
        height: 100%;
        background-color: var(--accent-blue);
        transition: width 0.5s ease;
    }
</style>