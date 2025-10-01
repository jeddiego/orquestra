package com.montecristoai.orquestra.routes

import com.montecristoai.orquestra.data.dto.ChatRequest
import com.montecristoai.orquestra.domain.usecase.ChatUseCase
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.chatRoute() {
    routing {
        route("/api") {
            post("/chat") {
                val request = call.receive<ChatRequest>()
                val chatUseCase = call.get<ChatUseCase>()

                // For now, we'll use a hardcoded model name. This can be made dynamic later.
                val modelName = "gemini-1.5-pro-latest"

                val response = chatUseCase.execute(request.message, modelName)
                call.respond(response)
            }
        }
    }
}