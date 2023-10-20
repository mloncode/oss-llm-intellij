package org.intellij.ml.llm.server

import com.github.bzz.intellij.com.github.bzz.intellij.models.OSSLLMModels
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


object OSSLLMServer {
    private const val MAX_TOKEN_LENGTH = 10

//    @Serializable data class LLMModel (@SerializedName("model_name") val modelName: String)
    @Serializable data class LLMModelsResponse(val models: List<String>)
    @Serializable data class LLMResponse (val results: List<String>)
    @Serializable data class LLMRequest(
        val model: String,
        val prompt: String,
        @SerializedName("max_new_tokens")
        val maxNewTokens: Int
    )

    open class ServerException(message: String, cause: Throwable?=null):
        RuntimeException("OSS LLM Server: $message", cause)
    private class HTTPRequestException(url: String, httpStatus: Int, cause: Throwable?=null):
        ServerException("HTTP Request to $url failed. Status code: $httpStatus", cause)
    private class JsonFormatException(body: String?, cause: Throwable?):
        ServerException("Unknown JSON format: $body", cause)
    private class NoLLMLoadedException(): ServerException("No LLM is selected")

    private val client = HttpClient.newBuilder().build()

    private fun <T> serverQuery(url: String, request: HttpRequest, serializableClass: Class<T>): T {
        val response = try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch(ioe: IOException) {
            throw HTTPRequestException(url, -1, ioe)
        }

        return when (response.statusCode()) {
            200 -> try {
                Gson().fromJson(response.body(), serializableClass)
            } catch(jsonException: JsonSyntaxException) {
                throw JsonFormatException(response.body(), jsonException)
            }
            else -> throw HTTPRequestException(url, response.statusCode())
        }
    }

    fun getAvailableModels(host: String = "localhost", port: Int = 8000): LLMModelsResponse {
        val url = URI.create("http://$host:$port")
        val getRequest = HttpRequest.newBuilder()
            .uri(url)
            .GET()
            .timeout(java.time.Duration.ofSeconds(10))
            .build()

        val models = serverQuery(url.toString(), getRequest, LLMModelsResponse::class.java)
        return models
    }

    fun getSuggestions(context: String, host: String = "localhost", port: Int = 8000): LLMResponse {
        val queryParams = LLMRequest(
            model = OSSLLMModels.currentModel() ?: throw NoLLMLoadedException(),
            prompt = context,
            maxNewTokens = MAX_TOKEN_LENGTH
        )
        val json = Gson().toJson(queryParams)
        val url = URI.create("http://$host:$port")
        val postRequest = HttpRequest.newBuilder()
            .uri(url)
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .header("Content-type", "application/json")
            .timeout(java.time.Duration.ofSeconds(10))
            .build()

        return serverQuery(url.toString(), postRequest, LLMResponse::class.java)
    }
}