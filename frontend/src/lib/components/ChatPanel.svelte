<script>
  import { onMount } from 'svelte';
  import { messages, addMessage, showTypingIndicator } from '../stores/chatStore.js';
  import { missionStatus, missionPhase, missionProgress, isMissionActive } from '../stores/missionStore.js';
  import { canvasContent } from '../stores/canvasStore.js';

  let chatContainer;
  let userInput = '';
  let inputDisabled = false;

  onMount(() => {
    // Auto-scroll to bottom
    messages.subscribe(() => {
      if (chatContainer) {
        chatContainer.scrollTop = chatContainer.scrollHeight;
      }
    });
  });

  const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

  async function runConversation() {
    if (userInput.trim() === "") return;

    const currentInput = userInput;
    userInput = '';
    inputDisabled = true;

    addMessage('user', `<p>${currentInput}</p>`);

    await sleep(500);
    missionStatus.set("📊 En Ejecución");
    isMissionActive.set(true);
    missionPhase.set("Fase 1: Procesando Solicitud");
    missionProgress.set(25);

    await sleep(1500);
    showTypingIndicator.set(true);
    await sleep(2500);
    showTypingIndicator.set(false);
    const agentResponse1 = `<i class="ph-bold ph-brain agent-icon"></i><div><p class="agent-name">Orquestra</p><p>Recibido. Iniciando análisis para identificar los perfiles de buyer persona más relevantes para tu campaña. Estoy cruzando datos demográficos y de comportamiento.</p></div>`;
    addMessage('agent', agentResponse1);

    await sleep(1000);
    missionPhase.set("Fase 1: Segmentando Audiencias");
    missionProgress.set(66);

    await sleep(3000);
    showTypingIndicator.set(true);
    await sleep(4000);
    showTypingIndicator.set(false);
    const agentResponse2 = `<i class="ph-bold ph-brain agent-icon"></i><div><p class="agent-name">Orquestra</p><p>Análisis completado. He identificado dos perfiles clave con alto potencial de conversión. Puedes ver los detalles en el canvas para afinar tu estrategia de campaña.</p></div>`;
    addMessage('agent', agentResponse2);

    await sleep(500);
    canvasContent.set('personas');
    missionPhase.set("Fase 2: Perfiles Generados");
    missionProgress.set(100);

    await sleep(1500);
    addMessage('user', `<p>Perfecto, esto es justo lo que necesitaba. Muy útil.</p>`);

    inputDisabled = false;
  }

  function handleKeydown(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      runConversation();
    }
  }
</script>

<div class="chat-content-area" bind:this={chatContainer}>
    {#each $messages as message (message.id)}
        <div class="chat-bubble {message.sender}-bubble">
            {@html message.content}
        </div>
    {/each}
    {#if $showTypingIndicator}
        <div class="chat-bubble agent-bubble">
            <i class="ph-bold ph-brain agent-icon"></i><div><p class="agent-name">Orquestra</p><div class="typing-indicator"><span></span><span></span><span></span></div></div>
        </div>
    {/if}
</div>
<div class="chat-input-area">
    <textarea bind:value={userInput} on:keydown={handleKeydown} disabled={inputDisabled} placeholder="Describe tu campaña o idea..."></textarea>
    <button on:click={runConversation} disabled={inputDisabled}>
        <i class="ph-bold ph-paper-plane-tilt"></i>
    </button>
</div>

<style>
    .chat-content-area {
        flex-grow: 1;
        padding: 1.5rem;
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
        overflow-y: auto;
    }
    .chat-bubble {
        display: flex;
        gap: 1rem;
        max-width: 80%;
    }
    .agent-bubble {
        align-self: flex-start;
    }
    .user-bubble {
        align-self: flex-end;
        flex-direction: row-reverse;
    }
    .agent-icon {
        font-size: 1.5rem;
        color: var(--accent-blue);
        background-color: var(--bg-tertiary);
        padding: 0.5rem;
        border-radius: 50%;
        height: fit-content;
    }
    .agent-name {
        font-weight: 600;
        margin-bottom: 0.25rem;
    }
    .chat-input-area {
        padding: 1rem;
        border-top: 1px solid var(--border-color);
        background-color: var(--bg-secondary);
        display: flex;
        gap: 0.5rem;
    }
    .chat-input-area textarea {
        flex-grow: 1;
        background-color: var(--bg-tertiary);
        border: 1px solid #444;
        border-radius: 8px;
        padding: 0.75rem;
        color: var(--text-primary);
        resize: none;
        font-size: 1rem;
    }
    .chat-input-area textarea:focus {
        outline: 1px solid var(--accent-blue);
    }
    .chat-input-area button {
        background-color: var(--accent-blue);
        border: none;
        color: white;
        border-radius: 8px;
        width: 44px;
        height: 44px;
        cursor: pointer;
        font-size: 1.25rem;
    }

    /* Typing Indicator Styles */
    .typing-indicator {
        display: flex;
        padding: 8px 0;
    }
    .typing-indicator span {
        height: 8px;
        width: 8px;
        float: left;
        margin: 0 1px;
        background-color: #9E9EA1;
        display: block;
        border-radius: 50%;
        opacity: 0.4;
        animation: 1s blink infinite;
    }
    .typing-indicator span:nth-child(2) {
        animation-delay: .2s;
    }
    .typing-indicator span:nth-child(3) {
        animation-delay: .4s;
    }
    @keyframes blink {
        50% {
            opacity: 1;
        }
    }
</style>