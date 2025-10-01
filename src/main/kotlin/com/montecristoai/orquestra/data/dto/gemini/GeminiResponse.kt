package com.montecristoai.orquestra.data.dto.gemini

import kotlinx.serialization.Serializable

// Note: These classes are simplified to match the expected response structure
// for the chat functionality.

@Serializable
data class PartResponse(val text: String)

@Serializable
data class ContentResponse(val parts: List<PartResponse>, val role: String)

@Serializable
data class Candidate(val content: ContentResponse)

@Serializable
data class GenerateContentResponse(val candidates: List<Candidate>)