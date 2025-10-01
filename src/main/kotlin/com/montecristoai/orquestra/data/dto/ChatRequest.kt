package com.montecristoai.orquestra.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(val message: String)