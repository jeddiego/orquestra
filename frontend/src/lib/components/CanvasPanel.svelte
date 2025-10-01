<script>
  import { canvasContent } from '../stores/canvasStore.js';
</script>

{#if $canvasContent === 'empty'}
<div class="placeholder">
    <i class="ph-bold ph-graph"></i>
    <h3>Canvas de Estrategia</h3>
    <p>Los resultados y artefactos de tus misiones aparecerán aquí.</p>
</div>
{:else if $canvasContent === 'personas'}
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
</div>
{/if}

<style>
    .placeholder {
        text-align: center;
        margin: auto;
        color: var(--text-secondary);
    }
    .placeholder i {
        font-size: 4rem;
        margin-bottom: 1rem;
        color: var(--bg-tertiary);
    }

    .personas-container {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }
    .persona-card {
        background-color: var(--bg-secondary);
        border-radius: 8px;
        padding: 1.5rem;
        border: 1px solid var(--border-color);
    }
    .persona-header {
        display: flex;
        align-items: center;
        gap: 1rem;
        margin-bottom: 1rem;
    }
    .persona-header img {
        width: 60px;
        height: 60px;
        border-radius: 50%;
    }
    .persona-title h3 {
        font-size: 1.125rem;
        font-weight: 600;
    }
    .persona-title p {
        font-size: 0.875rem;
        color: var(--text-secondary);
    }
    .persona-details h4 {
        margin-top: 1rem;
        margin-bottom: 0.5rem;
        font-weight: 600;
        color: var(--text-primary);
    }
    .persona-details p {
        color: var(--text-secondary);
        font-size: 0.9rem;
        line-height: 1.5;
    }
    .tags-list {
        margin-top: 0.5rem;
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
    }
    .tag {
        background-color: var(--bg-tertiary);
        color: var(--text-secondary);
        font-size: 0.75rem;
        padding: 0.25rem 0.75rem;
        border-radius: 12px;
    }
</style>