package com.montecristoai.orquestra.routes

import com.montecristoai.orquestra.domain.usecase.ListModelsUseCase
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.modelsRoute() {
    routing {
        route("/api") {
            get("/models") {
                val useCase = call.get<ListModelsUseCase>()
                val models = useCase.execute()
                call.respond(models)
            }
        }
    }
}