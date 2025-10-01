package com.montecristoai.orquestra.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(val response: String, val error: String? = null)