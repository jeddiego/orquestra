import { writable } from 'svelte/store';

export const missionStatus = writable("⏳ En Espera"); // "📊 En Ejecución", "✅ Completada"
export const missionPhase = writable("Ninguna");
export const missionProgress = writable(0); // 0-100
export const isMissionActive = writable(false);