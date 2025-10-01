document.addEventListener('DOMContentLoaded', () => {
    // --- State Management ---
    const COLOR_PALETTE = ['#3b82f6', '#14b8a6', '#f97316', '#8b5cf6', '#ef4444', '#f59e0b'];
    let state = {
        devices: [],
        currentPhase: 1,
        isRecording: false,
    };
    let animationFrameId = null;

    // --- DOM Elements ---
    const detectButton = document.getElementById('detect-devices-button');
    const toPhase3Button = document.getElementById('to-phase-3-button');
    const startStopButton = document.getElementById('start-stop-button');
    const finishButton = document.getElementById('finish-button');
    const phaseElements = { 1: document.getElementById('phase-1'), 2: document.getElementById('phase-2'), 3: document.getElementById('phase-3') };
    const phaseContentElements = { 1: document.getElementById('phase-1-content'), 2: document.getElementById('phase-2-content'), 3: document.getElementById('phase-3-content') };
    const assignmentListContainer = document.getElementById('assignment-list-container');
    const deviceStatusList = document.getElementById('device-status-list');
    const monitorSubtitle = document.getElementById('monitor-subtitle');
    const modelSelector = document.getElementById('model-selector');

    // --- Prevent Accidental Navigation ---
    window.addEventListener('beforeunload', (event) => {
        if (state.isRecording) {
            event.preventDefault();
            event.returnValue = '';
        }
    });

    // --- NEW: Load AI Models ---
    const loadModels = async () => {
        try {
            console.log("[loadModels] Solicitando lista de modelos desde /api/models...");
            const response = await fetch('/api/models');
            if (!response.ok) {
                throw new Error(`Error del servidor al cargar modelos: ${response.statusText}`);
            }
            const models = await response.json();
            console.log("[loadModels] Modelos recibidos:", models);

            modelSelector.innerHTML = ''; // Clear "loading" option
            if (models.length === 0) {
                 modelSelector.innerHTML = '<option value="">No se encontraron modelos</option>';
                 return;
            }

            models.forEach(modelName => {
                const option = document.createElement('option');
                const friendlyName = modelName.split('/').pop();
                option.value = modelName;
                option.textContent = friendlyName;
                modelSelector.appendChild(option);
            });
        } catch (error) {
            console.error("[loadModels] Falló la carga de modelos:", error);
            modelSelector.innerHTML = '<option value="">Error al cargar modelos</option>';
        }
    };

    // --- Real Analysis Service (Calling your Ktor Backend) ---
    const analyzeAudio = async (audioBlob, device, modelName) => {
        const YOUR_BACKEND_URL = "/api/analyze";

        console.log(`[analyzeAudio] Iniciando análisis para el dispositivo: ${device.deviceId}`);
        console.log(`[analyzeAudio] URL del backend: ${YOUR_BACKEND_URL}`);
        console.log(`[analyzeAudio] Modelo seleccionado: ${modelName}`);

        const formData = new FormData();
        const fileName = `${device.participantName.replace(/\s+/g, '_') || device.deviceId}.wav`;

        formData.append("model", modelName);
        formData.append("audio", audioBlob, fileName);

        try {
            console.log("[analyzeAudio] Enviando petición fetch...");
            const response = await fetch(YOUR_BACKEND_URL, {
                method: 'POST',
                body: formData
            });
            console.log(`[analyzeAudio] Respuesta recibida del backend con estado: ${response.status}`);

            if (!response.ok) {
                 const result = await response.json();
                 console.error("[analyzeAudio] Error en la respuesta del backend (response.ok = false):", result);
                 throw new Error(result.error || `Error del servidor: ${response.statusText}`);
            }

            console.log("[analyzeAudio] La respuesta del backend fue exitosa. Procesando JSON...");
            const responseData = await response.json();
            console.log("[analyzeAudio] Datos del análisis recibidos:", responseData);
            return responseData;

        } catch (error) {
            console.error("[analyzeAudio] Falló la llamada a tu backend. Error completo:", error);
            return { error: `Error de conexión o del script. Revisa la consola (F12) para más detalles. Error: ${error.message}` };
        }
    };

    // --- UI Rendering ---
    const render = () => {
        for (const phaseNum in phaseElements) {
            phaseElements[phaseNum].classList.remove('active', 'completed');
            if (phaseNum < state.currentPhase) phaseElements[phaseNum].classList.add('completed');
            else if (phaseNum == state.currentPhase) phaseElements[phaseNum].classList.add('active');
        }
        for (const phaseNum in phaseContentElements) {
            phaseContentElements[phaseNum].classList.toggle('active', phaseNum == state.currentPhase);
        }

        deviceStatusList.innerHTML = '';
        if (state.devices.length === 0 && state.currentPhase === 1) {
            monitorSubtitle.textContent = 'Esperando detección de dispositivos...';
        } else {
            monitorSubtitle.textContent = 'Estado de los micrófonos en la sesión.';
            state.devices.forEach(device => {
                const li = document.createElement('li');
                li.className = 'device-status-item';
                if (device.selected) {
                    li.classList.add('selected');
                    li.style.borderLeftColor = device.color;
                }

                let statusHTML;
                switch(device.status) {
                    case 'recording':
                        statusHTML = `<div class="device-status status-recording">
                                        <div class="vu-meter-container"><div class="vu-meter-bar" id="vu-${device.deviceId}"></div></div>
                                        <span>Grabando...</span>
                                     </div>`;
                        break;
                    case 'analyzing':
                        statusHTML = `<div class="device-status status-transcribing"><i class="ph-bold ph-spinner-gap animate-spin"></i><span>Analizando...</span></div>`;
                        break;
                    case 'finished':
                        statusHTML = `<button class="analyze-button" data-device-id="${device.deviceId}"><i class="ph-bold ph-chart-bar"></i> Ver Análisis</button>`;
                        break;
                    case 'ready_to_analyze':
                         statusHTML = `<button class="analyze-button" data-device-id="${device.deviceId}">Analizar Audio</button>`;
                         break;
                    default: // ready
                        statusHTML = '<div class="device-status status-ready"><i class="ph-bold ph-check-circle"></i><span>Listo</span></div>';
                }

                li.innerHTML = `
                    <div class="device-info">
                        <div class="participant-name">${device.participantName || 'Sin asignar'}</div>
                    </div>
                    ${statusHTML}
                `;
                deviceStatusList.appendChild(li);
            });

            deviceStatusList.querySelectorAll('.analyze-button').forEach(button => {
                button.addEventListener('click', handleAnalyzeClick);
            });
        }
    };

    // --- Event Handlers ---
    const handleAnalyzeClick = async (e) => {
        const deviceId = e.target.closest('button').dataset.deviceId;
        const device = state.devices.find(d => d.deviceId === deviceId);

        if (!device) return;

        if (device.analysisData) {
            window.showAnalysis(device.analysisData, `${device.participantName.replace(/\s+/g, '_') || device.deviceId}.wav`);
            return;
        }

        if (!device.audioBlob) return;

        const selectedModel = modelSelector.value;
        if (!selectedModel || selectedModel === "") {
            alert("Por favor, selecciona un modelo de IA antes de analizar.");
            return;
        }

        device.status = 'analyzing';
        render();

        const analysisResult = await analyzeAudio(device.audioBlob, device, selectedModel);

        if (analysisResult.error) {
             alert(`Error en el análisis: ${analysisResult.error}`);
             device.status = 'ready_to_analyze';
        } else {
            device.analysisData = analysisResult;
            device.status = 'finished';
        }
        render();
    };

    // --- VU Meter Animation Loop ---
    const updateMeters = () => {
        if (!state.isRecording) {
            if (animationFrameId) cancelAnimationFrame(animationFrameId);
            animationFrameId = null;
            return;
        }
        state.devices.forEach(device => {
            if (device.status === 'recording' && device.analyser) {
                const dataArray = new Uint8Array(device.analyser.frequencyBinCount);
                device.analyser.getByteTimeDomainData(dataArray);
                let sumSquares = 0.0;
                for (const amplitude of dataArray) { const a = (amplitude / 128.0) - 1.0; sumSquares += a * a; }
                const rms = Math.sqrt(sumSquares / dataArray.length);
                const vuBar = document.getElementById(`vu-${device.deviceId}`);
                if (vuBar) vuBar.style.width = `${rms * 150}%`;
            }
        });
        animationFrameId = requestAnimationFrame(updateMeters);
    };

    // --- Phase Logic ---
    const goToPhase = (phaseNum) => {
        state.currentPhase = phaseNum;
        if (phaseNum === 2) renderAssignmentList();
        render();
    };

    const renderAssignmentList = () => {
        const ul = document.createElement('ul');
        ul.className = 'assignment-list';
        state.devices.forEach(device => {
            const li = document.createElement('li');
            li.className = 'assignment-item';
            li.innerHTML = `
                <div class="device-selector">
                    <span class="color-dot" style="background-color: ${device.color};"></span>
                    <input type="checkbox" data-device-id="${device.deviceId}" id="check-${device.deviceId}" ${device.selected ? 'checked' : ''}>
                </div>
                <input type="text" class="participant-input" placeholder="Nombre del Participante..." value="${device.participantName || ''}" data-device-id="${device.deviceId}">
            `;
            ul.appendChild(li);
        });
        assignmentListContainer.innerHTML = '';
        assignmentListContainer.appendChild(ul);

        assignmentListContainer.querySelectorAll('input').forEach(input => {
            input.addEventListener('change', (e) => {
                const device = state.devices.find(d => d.deviceId === e.target.dataset.deviceId);
                if (!device) return;
                if (e.target.type === 'checkbox') device.selected = e.target.checked;
                else if (e.target.type === 'text') {
                    if (e.target.value.trim() === '') {
                        device.participantName = '';
                    } else {
                        device.participantName = e.target.value;
                    }
                }
                render();
            });
        });
    };

    // --- Core Media Logic ---
    const handleDetect = async () => {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            stream.getTracks().forEach(track => track.stop());
            const devices = await navigator.mediaDevices.enumerateDevices();
            state.devices = devices.filter(d => d.kind === 'audioinput').map((d, index) => ({
                deviceId: d.deviceId,
                label: d.label || `Micrófono ${d.deviceId.substring(0, 8)}`,
                selected: true,
                participantName: `Hablante ${index + 1}`,
                color: COLOR_PALETTE[index % COLOR_PALETTE.length],
                status: 'ready'
            }));
            goToPhase(2);
        } catch (err) {
            alert('Error al detectar dispositivos: ' + err.message);
        }
    };

    const encodeWAV = (samples, sampleRate) => {
        const buffer = new ArrayBuffer(44 + samples.length * 2);
        const view = new DataView(buffer);
        const writeString = (view, offset, string) => { for (let i = 0; i < string.length; i++) { view.setUint8(offset + i, string.charCodeAt(i)); } };
        writeString(view, 0, 'RIFF'); view.setUint32(4, 36 + samples.length * 2, true); writeString(view, 8, 'WAVE'); writeString(view, 12, 'fmt '); view.setUint32(16, 16, true); view.setUint16(20, 1, true); view.setUint16(22, 1, true); view.setUint32(24, sampleRate, true); view.setUint32(28, sampleRate * 2, true); view.setUint16(32, 2, true); view.setUint16(34, 16, true); writeString(view, 36, 'data'); view.setUint32(40, samples.length * 2, true);
        let offset = 44;
        for (let i = 0; i < samples.length; i++, offset += 2) {
            const s = Math.max(-1, Math.min(1, samples[i]));
            view.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
        }
        return new Blob([view], { type: 'audio/wav' });
    };

    const handleStartStop = async () => {
        const useWavFallback = !('MediaRecorder' in window) || !MediaRecorder.isTypeSupported('audio/ogg; codecs=opus');

        if (state.isRecording) {
            state.isRecording = false;
            startStopButton.disabled = true;
            startStopButton.innerHTML = '<span>Finalizando...</span>';

            for (const device of state.devices) {
                if (!device.selected || device.status !== 'recording') continue;

                device.status = 'processing';
                if (useWavFallback) {
                    if (device.workletNode) {
                        device.workletNode.port.onmessage = null;
                        device.workletNode.disconnect();
                        device.analyser.disconnect();
                        await device.audioContext.close();

                        const totalLength = device.pcmData.reduce((acc, chunk) => acc + chunk.length, 0);
                        const concatenatedData = new Float32Array(totalLength);
                        let offset = 0;
                        for (const chunk of device.pcmData) {
                            concatenatedData.set(chunk, offset);
                            offset += chunk.length;
                        }

                        device.audioBlob = encodeWAV(concatenatedData, device.sampleRate);
                        device.fileExtension = 'wav';
                        device.status = 'ready_to_analyze';
                    }
                } else {
                    if (device.recorder && device.recorder.state === 'recording') {
                        device.recorder.onstop = () => {
                            device.audioBlob = new Blob(device.audioChunks, { type: 'audio/ogg' });
                            device.fileExtension = 'ogg';
                            device.status = 'ready_to_analyze';
                            render();
                        };
                        device.recorder.stop();
                    }
                }
                if (device.stream) device.stream.getTracks().forEach(track => track.stop());
            }
            startStopButton.classList.add('hidden');
            finishButton.classList.remove('hidden');
            render();

        } else {
            let anyDeviceSelected = false;
            for(const device of state.devices) {
                if(device.selected && device.participantName.trim() !== '') {
                    anyDeviceSelected = true;
                    break;
                }
            }

            if(!anyDeviceSelected) {
                alert("Por favor, asigna un nombre a al menos un participante seleccionado.");
                return;
            }

            state.isRecording = true;
            startStopButton.innerHTML = '<i class="ph-bold ph-stop"></i><span>Detener Grabación</span>';

            for (const device of state.devices) {
                if (device.selected && device.participantName.trim() !== '') {
                    try {
                        const stream = await navigator.mediaDevices.getUserMedia({ audio: { deviceId: { exact: device.deviceId } } });
                        device.stream = stream;
                        device.status = 'recording';

                        device.audioContext = new (window.AudioContext || window.webkitAudioContext)();
                        device.sampleRate = device.audioContext.sampleRate;
                        const source = device.audioContext.createMediaStreamSource(stream);
                        device.analyser = device.audioContext.createAnalyser();
                        device.analyser.fftSize = 256;
                        source.connect(device.analyser);

                        if (useWavFallback) {
                            await device.audioContext.audioWorklet.addModule('/static/wav-recorder-processor.js');
                            device.workletNode = new AudioWorkletNode(device.audioContext, 'wav-recorder-processor');
                            device.pcmData = [];
                            device.workletNode.port.onmessage = (event) => {
                                device.pcmData.push(event.data);
                            };
                            device.analyser.connect(device.workletNode);
                            device.workletNode.connect(device.audioContext.destination);
                        } else {
                            device.analyser.connect(device.audioContext.destination);
                            device.recorder = new MediaRecorder(stream, { mimeType: 'audio/ogg; codecs=opus' });
                            device.audioChunks = [];
                            device.recorder.ondataavailable = e => device.audioChunks.push(e.data);
                            device.recorder.start();
                        }
                    } catch (err) {
                        alert(`No se pudo iniciar la grabación para ${device.label}: ${err.message}`);
                        device.status = 'ready';
                    }
                }
            }
            updateMeters();
        }
        render();
    };

    const handleFinish = () => {
        alert("La sesión ha finalizado. Para comenzar una nueva, simplemente detecta los dispositivos otra vez.");
        state = { ...state, currentPhase: 1, isRecording: false, devices: [] };
        finishButton.classList.add('hidden');
        startStopButton.classList.remove('hidden');
        startStopButton.disabled = false;
        startStopButton.innerHTML = '<i class="ph-bold ph-play"></i><span>Iniciar Grabación</span>';
        goToPhase(1);
    };

    // --- Event Listeners ---
    detectButton.addEventListener('click', handleDetect);
    toPhase3Button.addEventListener('click', () => {
        if (!state.devices.some(d => d.selected)) {
            alert("Por favor, selecciona al menos un micrófono para continuar.");
            return;
        }
        goToPhase(3);
    });
    startStopButton.addEventListener('click', handleStartStop);
    finishButton.addEventListener('click', handleFinish);

    // Initial Render
    render();
    loadModels();
});