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
                var audioBytes: ByteArray? = null

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem && part.name == "audio") {
                        audioBytes = part.streamProvider().readBytes()
                    }
                    part.dispose()
                }

                val bytes = audioBytes
                if (bytes == null) {
                    call.respond(mapOf("error" to "No se recibió archivo de audio"))
                    return@post
                }

                val useCase = call.get<TranscribeAudioUseCase>()
                val result = useCase.execute(bytes)
                call.respond(result)
            }
        }
    }
}

