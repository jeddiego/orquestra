package com.montecristoai.orquestra.di

import com.montecristoai.orquestra.data.repository.GeminiManualTranscriptionRepository
import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository
import com.montecristoai.orquestra.domain.usecase.ChatUseCase
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun appModule(geminiKey: String) = module {

    // Proporciona una única instancia del HttpClient para toda la app.
    single {
        HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 600000 // 10 minutos
                connectTimeoutMillis = 600000
                socketTimeoutMillis = 600000
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    // Define cómo crear el ChatUseCase.
    single { ChatUseCase(get()) }

    // Proporciona el repositorio, inyectando la clave de Gemini.
    single<ITranscriptionRepository> {
        GeminiManualTranscriptionRepository(geminiKey)
    }
}