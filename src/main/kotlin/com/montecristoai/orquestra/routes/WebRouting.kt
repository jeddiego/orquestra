package com.montecristoai.orquestra.routes

import com.montecristoai.orquestra.presentation.index.indexContent
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.webRouting() {
    routing {
        // --- RUTA PARA LA PÁGINA DE INICIO (PRELANZAMIENTO) ---
        get("/") {
            indexContent(call)
        }

        // --- RUTA PARA SERVIR ARCHIVOS ESTÁTICOS (sin cambios) ---
        static("/static") {
            resources("static")
        }
    }
}