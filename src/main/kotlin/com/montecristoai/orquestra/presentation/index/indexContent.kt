package com.montecristoai.orquestra.presentation.index

import com.montecristoai.orquestra.presentation.orquestraLayout
import io.ktor.server.application.*
import io.ktor.server.html.*
import kotlinx.html.*

suspend fun indexContent(call: ApplicationCall) {
    call.respondHtml {
        orquestraLayout(
            pageTitle = "Grabación de Sesión",
            missionPanelContent = {
                // --- Panel Izquierdo: Configurador de Sesión (sin cambios) ---
                div(classes = "session-setup-panel") {
                    h2 { +"Configurar Sesión" }
                    ul(classes = "setup-phases") {
                        li(classes = "phase active") { id = "phase-1"; +"1. Detección" }
                        li(classes = "phase") { id = "phase-2"; +"2. Asignación" }
                        li(classes = "phase") { id = "phase-3"; +"3. Grabación" }
                    }
                    div(classes = "phase-content-wrapper") {
                        div(classes = "phase-content active") {
                            id = "phase-1-content"
                            p { +"Detecta los micrófonos conectados para iniciar." }
                            button(classes = "control-button detect") {
                                id = "detect-devices-button"
                                i(classes = "ph-bold ph-magnifying-glass")
                                span { +"Detectar Dispositivos" }
                            }
                        }
                    }
                    div(classes = "phase-content-wrapper") {
                        div(classes = "phase-content") {
                            id = "phase-2-content"
                            p { +"Selecciona los micrófonos y asigna cada participante." }
                            div { id = "assignment-list-container" }
                            button(classes = "control-button proceed") {
                                id = "to-phase-3-button"
                                i(classes = "ph-bold ph-arrow-right")
                                span { +"Continuar a Grabación" }
                            }
                        }
                    }
                    div(classes = "phase-content-wrapper") {
                        div(classes = "phase-content") {
                            id = "phase-3-content"
                            p { +"Inicia la grabación. Podrás analizar cada audio al finalizar." }
                            button(classes = "control-button record") {
                                id = "start-stop-button"
                                i(classes = "ph-bold ph-play")
                                span { +"Iniciar Grabación" }
                            }
                            button(classes = "control-button download hidden") {
                                id = "finish-button"
                                i(classes = "ph-bold ph-power")
                                span { +"Finalizar Sesión" }
                            }
                        }
                    }
                }
            },
            chatPanelContent = {
                // --- Panel de Chat (sin cambios) ---
                div(classes = "chat-content-area") {
                    div(classes = "chat-bubble agent-bubble") {
                        i(classes = "ph-bold ph-brain agent-icon")
                        div {
                            p(classes = "agent-name") { +"Orquestra" }
                            p { +"Panel de grabación listo. Configure la sesión usando el panel izquierdo." }
                        }
                    }
                }
                div(classes = "chat-input-area") {
                    textArea { rows = "1"; placeholder = "Interactúa con Orquestra..." }
                    button { i(classes = "ph-bold ph-arrow-up") }
                }
            },
            canvasPanelContent = {
                // --- Panel Derecho: Monitor de Estado (sin cambios) ---
                div(classes = "status-monitor-panel") {
                    h3 { +"Monitor de Estado" }
                    p(classes = "panel-subtitle") { id="monitor-subtitle"; +"Esperando detección de dispositivos..." }
                    ul(classes = "device-status-list") { id = "device-status-list" }
                }
            }
        )
    }
}
