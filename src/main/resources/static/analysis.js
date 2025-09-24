document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('analysis-modal');
    const closeModalButton = document.getElementById('close-analysis-modal');
    const modalContent = document.querySelector('.analysis-modal-content');

    // Función para cerrar el modal
    const closeModal = () => {
        modal.classList.remove('visible');
    };

    closeModalButton.addEventListener('click', closeModal);
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            closeModal();
        }
    });

    // Función global para abrir y popular el modal
    window.showAnalysis = (analysisData, fileName) => {
        const { descriptiveCard, transcription } = analysisData;

        if (!descriptiveCard || !transcription) {
            alert("Los datos de análisis están incompletos.");
            return;
        }

        modalContent.innerHTML = `
            <div class="descriptive-card">
                <div class="card-header">
                    <h3>Ficha Descriptiva</h3>
                    <button class="copy-button" data-copy-type="card">📋</button>
                </div>
                <div class="info-grid">
                    <div class="info-item"><h4>Archivo</h4><p>${fileName || 'N/A'}</p></div>
                    <div class="info-item"><h4>Hablantes</h4><p>${descriptiveCard.detectedSpeakers || 'N/A'}</p></div>
                    <div class="info-item"><h4>Tipo</h4><p>${descriptiveCard.recordingType || 'N/A'}</p></div>
                    <div class="info-item"><h4>Fecha</h4><p>${descriptiveCard.detectedDate || 'No detectado'}</p></div>
                    <div class="info-item"><h4>Lugar</h4><p>${descriptiveCard.detectedPlace || 'No detectado'}</p></div>
                    <div class="info-item"><h4>Proyecto</h4><p>${descriptiveCard.detectedProject || 'No detectado'}</p></div>
                </div>
                <div class="summary">
                    <h4>Resumen</h4>
                    <p>${descriptiveCard.summary || 'No disponible.'}</p>
                </div>
                <div class="key-points" style="margin-top: 1rem;">
                    <h4>Puntos Clave</h4>
                    <ul>
                        ${(descriptiveCard.keyPoints || []).map(p => `<li>${p}</li>`).join('')}
                    </ul>
                </div>
            </div>
            <div class="transcription-card">
                 <div class="card-header">
                    <h3>Transcripción</h3>
                    <button class="copy-button" data-copy-type="transcription">📋</button>
                </div>
                <div class="transcription-content">
                    ${transcription}
                </div>
            </div>
        `;
        modal.classList.add('visible');
    };

    // Lógica de copiado
     modalContent.addEventListener('click', (e) => {
        if (e.target.classList.contains('copy-button')) {
            const button = e.target;
            const type = button.dataset.copyType;
            let textToCopy = '';

            if (type === 'card') {
                const card = modalContent.querySelector('.descriptive-card');
                const fileName = card.querySelector('.info-grid .info-item:nth-child(1) p').textContent;
                const speakers = card.querySelector('.info-grid .info-item:nth-child(2) p').textContent;
                const summary = card.querySelector('.summary p').textContent;
                const keyPoints = Array.from(card.querySelectorAll('.key-points li')).map(li => `- ${li.textContent}`).join('\n');
                textToCopy = `FICHA DESCRIPTIVA\nArchivo: ${fileName}\nHablantes: ${speakers}\n\nResumen:\n${summary}\n\nPuntos Clave:\n${keyPoints}`;

            } else if (type === 'transcription') {
                textToCopy = modalContent.querySelector('.transcription-content').innerText;
            }

            if (textToCopy) {
                navigator.clipboard.writeText(textToCopy).then(() => {
                    button.textContent = '✅';
                    setTimeout(() => { button.textContent = '📋'; }, 2000);
                }).catch(err => console.error('Error al copiar:', err));
            }
        }
    });
});
