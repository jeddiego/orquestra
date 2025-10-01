// --- Referencias a elementos del DOM ---
const chatContent = document.getElementById('chat-content-area');
const chatInput = document.getElementById('chat-input');
const sendButton = document.getElementById('send-button');
const canvasPanel = document.getElementById('canvas-panel');
const missionStatus = document.getElementById('mission-status');
const missionItem = document.getElementById('mission-item');
const missionPhase = document.getElementById('mission-phase');
const missionProgress = document.getElementById('mission-progress');

// --- Lógica de la simulación ---
const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Función para añadir una burbuja de mensaje
function addMessage(sender, content) {
    if (!chatContent) return;
    const bubble = document.createElement('div');
    bubble.className = `chat-bubble ${sender}-bubble`;
    bubble.innerHTML = content;
    chatContent.appendChild(bubble);
    chatContent.scrollTop = chatContent.scrollHeight; // Auto-scroll al fondo
}

// Función para mostrar/ocultar el indicador de "escribiendo"
let typingIndicator;
function showTypingIndicator() {
    if (typingIndicator || !chatContent) return;
    typingIndicator = document.createElement('div');
    typingIndicator.className = 'chat-bubble agent-bubble';
    typingIndicator.innerHTML = `<i class="ph-bold ph-brain agent-icon"></i><div><p class="agent-name">Orquestra</p><div class="typing-indicator"><span></span><span></span><span></span></div></div>`;
    chatContent.appendChild(typingIndicator);
    chatContent.scrollTop = chatContent.scrollHeight;
}

function hideTypingIndicator() {
    if (typingIndicator) {
        typingIndicator.remove();
        typingIndicator = null;
    }
}

// Función para cargar los Buyer Personas en el Canvas
function loadBuyerPersonas() {
    if (!canvasPanel) return;
    canvasPanel.innerHTML = `
    <div class="personas-container">
        <div class="persona-card">
            <div class="persona-header">
                <img src="https://placehold.co/60x60/8B5CF6/FFFFFF?text=CT" alt="Carlos, el Tecnólogo">
                <div class="persona-title">
                    <h3>Carlos, el Emprendedor Tecnológico</h3>
                    <p>Arquetipo: Innovador Pragmático</p>
                </div>
            </div>
            <div class="persona-details">
                <h4>Motivaciones</h4>
                <p>Busca constantemente herramientas que ofrezcan eficiencia, escalabilidad y una clara ventaja competitiva. Valora la innovación que resuelve problemas reales.</p>
                <h4>Puntos de Dolor</h4>
                <p>La falta de tiempo es su mayor enemigo. Le frustran las integraciones complejas y las soluciones que no ofrecen un ROI claro y rápido.</p>
                <h4>Canales</h4>
                <div class="tags-list">
                   <span class="tag">LinkedIn</span> <span class="tag">Blogs de Tech</span> <span class="tag">Podcasts de Negocios</span> <span class="tag">Eventos de Networking</span>
                </div>
            </div>
        </div>
        <div class="persona-card">
             <div class="persona-header">
                <img src="https://placehold.co/60x60/10B981/FFFFFF?text=AM" alt="Ana, la Marketera">
                <div class="persona-title">
                    <h3>Ana, la Gerente de Marketing</h3>
                    <p>Arquetipo: Estratega Basada en Datos</p>
                </div>
            </div>
            <div class="persona-details">
                <h4>Motivaciones</h4>
                <p>Su objetivo es la generación de leads cualificados y demostrar el impacto de sus campañas con métricas sólidas. Adopta tecnologías que le permiten optimizar y personalizar.</p>
                <h4>Puntos de Dolor</h4>
                <p>Lucha por justificar el presupuesto de marketing y se siente abrumada por la cantidad de datos y herramientas disponibles.</p>
                <h4>Canales</h4>
                 <div class="tags-list">
                   <span class="tag">Webinars</span> <span class="tag">Email Marketing</span> <span class="tag">SEO/SEM</span> <span class="tag">Comunidades Profesionales</span>
                </div>
            </div>
        </div>
    </div>`;
}

// --- Flujo de la Conversación Asíncrona ---
async function runConversation() {
    const userInput = chatInput.value.trim();
    if (userInput === "") return;

    chatInput.value = "";
    chatInput.disabled = true;
    sendButton.disabled = true;

    addMessage('user', `<p>${userInput}</p>`);

    await sleep(500);
    missionStatus.textContent = "📊 En Ejecución";
    missionItem.classList.add('active');
    missionPhase.textContent = "Fase 1: Procesando Solicitud";
    missionProgress.style.width = '25%';

    await sleep(1500);
    showTypingIndicator();
    await sleep(2500);
    hideTypingIndicator();
    const agentResponse1 = `<i class="ph-bold ph-brain agent-icon"></i><div><p class="agent-name">Orquestra</p><p>Recibido. Iniciando análisis para identificar los perfiles de buyer persona más relevantes para tu campaña. Estoy cruzando datos demográficos y de comportamiento.</p></div>`;
    addMessage('agent', agentResponse1);

    await sleep(1000);
    missionPhase.textContent = "Fase 1: Segmentando Audiencias";
    missionProgress.style.width = '66%';

    await sleep(3000);
    showTypingIndicator();
    await sleep(4000);
    hideTypingIndicator();
    const agentResponse2 = `<i class="ph-bold ph-brain agent-icon"></i><div><p class="agent-name">Orquestra</p><p>Análisis completado. He identificado dos perfiles clave con alto potencial de conversión. Puedes ver los detalles en el canvas para afinar tu estrategia de campaña.</p></div>`;
    addMessage('agent', agentResponse2);

    await sleep(500);
    loadBuyerPersonas();
    missionPhase.textContent = "Fase 2: Perfiles Generados";
    missionProgress.style.width = '100%';

    await sleep(1500);
    addMessage('user', `<p>Perfecto, esto es justo lo que necesitaba. Muy útil.</p>`);

    chatInput.disabled = false;
    sendButton.disabled = false;
    chatInput.placeholder = "Puedes hacer otra solicitud...";
    chatInput.focus();
}

// --- Event Listeners ---
if (sendButton && chatInput) {
    sendButton.addEventListener('click', runConversation);
    chatInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            runConversation();
        }
    });
}

