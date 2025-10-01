package com.montecristoai.orquestra.routes

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.webRouting() {
    routing {
        staticResources("/", "static") {
            default("index.html")
        }
    }
}