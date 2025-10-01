package com.montecristoai.orquestra

import com.montecristoai.orquestra.routes.webRouting
import com.montecristoai.orquestra.di.appModule
import com.montecristoai.orquestra.routes.chatRoute
import io.ktor.serialization.kotlinx.json.json
import io.ktor.http.HttpHeaders
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val geminiKey = environment.config.property("gemini.apiKey").getString()

    install(Koin) {
        // Le pasamos la clave a nuestro módulo de Koin.
        modules(appModule(geminiKey))
    }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }

    webRouting()
    chatRoute()
}
