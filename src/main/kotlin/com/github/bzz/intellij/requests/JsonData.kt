package com.github.bzz.intellij.com.github.bzz.intellij.requests

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class ServerProposal (
    val text: String
)

@Serializable
data class ProposalsResponse (
    val results: List<ServerProposal>
)

@Serializable
data class LLMModel (
    @SerializedName("model_name")
    val modelName: String
)

@Serializable
data class ModelsResponse(
    val models: List<LLMModel>
)

@Serializable
data class ProposalsQuery(
    val model: String,
    val prompt: String,
    @SerializedName("max_new_tokens")
    val maxNewTokens: Int
)
