package com.montecristoai.orquestra.presentation

import kotlinx.html.*

fun HTML.orquestraLayout(
    pageTitle: String,
    missionPanelContent: ASIDE.() -> Unit,
    chatPanelContent: SECTION.() -> Unit,
    canvasPanelContent: SECTION.() -> Unit
) {
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title("Orquestra | $pageTitle")
        link(href = "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap", rel = "stylesheet")
        link(href = "https://unpkg.com/@phosphor-icons/web@2.0.3/src/bold/style.css", rel = "stylesheet")
        // Cargamos los CSS necesarios
        link(rel = "stylesheet", href = "/static/style.css")
        link(rel = "stylesheet", href = "/static/session-setup.css")
        // --- NUEVO ---
        link(rel = "stylesheet", href = "/static/analysis.css")
    }
    body {
        header(classes = "top-header-dark") {
            div(classes = "header-left") { h1 { +"Orquestra" } }
            div(classes = "header-right") {
                span { +"Montecristo AInsights" }
                img(src = "https://placehold.co/32x32/3B82F6/FFFFFF?text=MA", alt = "Logo de la empresa")
            }
        }
        main(classes = "main-grid-container") {
            // Panel 1: Misiones / Configuración
            aside(classes = "panel missions-panel") {
                missionPanelContent()
            }

            // Panel 2: Chat
            section(classes = "panel chat-panel") {
                chatPanelContent()
            }

            // Panel 3: Canvas / Monitor
            section(classes = "panel canvas-panel") {
                canvasPanelContent()
            }
        }

        // --- NUEVO: Modal de Análisis (inicialmente oculto) ---
        div(classes = "analysis-modal-backdrop") { id = "analysis-modal"
            div(classes = "analysis-modal-panel") {
                div(classes = "analysis-modal-header") {
                    h2 { +"Análisis de Grabación" }
                    button { id = "close-analysis-modal"; +"×" }
                }
                div(classes = "analysis-modal-content") {
                    // Contenido se llenará dinámicamente con JS
                }
            }
        }


        // --- SCRIPTS ---
        // El script principal de la simulación de chat
        script(src = "/static/script.js") {}
        // El script para la configuración de la sesión
        script(src = "/static/session-setup.js") {}
        // --- NUEVO ---
        // El script para la lógica del panel de análisis
        script(src = "/static/analysis.js") {}
    }
}
