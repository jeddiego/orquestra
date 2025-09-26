package com.montecristoai.orquestra.routes

import com.montecristoai.orquestra.domain.usecase.TranscribeAudioUseCase
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.transcriptionRoute() {
    routing {
        route("/api") {
            post("/transcribe") {
                val multipart = call.receiveMultipart()
                var responseSent = false

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem && part.name == "audio" && !responseSent) {
                        responseSent = true
                        part.streamProvider().use { audioStream ->
                            val useCase = call.get<TranscribeAudioUseCase>()
                            val result = useCase.execute(audioStream)
                            call.respond(result)
                        }
                    }
                    part.dispose()
                }

                if (!responseSent) {
                    call.respond(mapOf("error" to "No se recibió archivo de audio o parte de audio inválida."))
                }
            }
        }
    }
}

