package com.github.bzz.intellij.requests

import com.github.bzz.intellij.com.github.bzz.intellij.models.AvailableModels
import com.github.bzz.intellij.com.github.bzz.intellij.requests.ModelsResponse
import com.github.bzz.intellij.com.github.bzz.intellij.requests.ProposalsQuery
import com.github.bzz.intellij.com.github.bzz.intellij.requests.ProposalsResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpTimeoutException


object Requester {

    const val MAX_TOKEN_LENGTH = 10
    open class RequesterException(message: String): RuntimeException("RequesterException: $message")

    private class NoListenerRequesterException(ip: String, port: Int):
        RequesterException("Probable cause: server $ip is not running or listening at port $port.")

    private class TimeoutRequesterException:
        RequesterException("Probable cause: poor connection.")

    private class GeneralRequesterException(ip: String, port: Int, httpStatus: Int):
        RequesterException("HTTP Request to $ip:$port failed. Failure status code: $httpStatus")

    private class JsonRequesterException(ip: String, port: Int, answer: String?):
        RequesterException("Response from $ip:$port has incompatible json: $answer")

    private class NoLoadedLLMException:
        RequesterException("No LLM is chosen due to the absence of any.")


    private val client = HttpClient.newBuilder().build()

    fun getAvailableModels(ip: String = "localhost", port: Int = 8000): ModelsResponse {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://$ip:$port"))
            .GET()
            .timeout(java.time.Duration.ofSeconds(10))
            .build()

        return serverQuery(ip, port, request, ModelsResponse::class.java)
    }

    fun getModelSuggestions(context: String, ip: String = "localhost", port: Int = 8000): ProposalsResponse {
        val queryParams = ProposalsQuery(
            model = AvailableModels.currentModel() ?: throw NoLoadedLLMException(),
            prompt = context,
            maxNewTokens = MAX_TOKEN_LENGTH
        )
        val json = Gson().toJson(queryParams)
        val request = HttpRequest.newBuilder()
                .uri(URI.create("http://$ip:$port"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-type", "application/json")
                .timeout(java.time.Duration.ofSeconds(10))
                .build()

        return serverQuery(ip, port, request, ProposalsResponse::class.java)
    }

    private fun <T> serverQuery(ip: String, port: Int, request: HttpRequest, serializableClass: Class<T>): T {
        val response = try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch(toe: HttpTimeoutException) {
            throw TimeoutRequesterException()
        } catch(ioe: IOException) {
            throw NoListenerRequesterException(ip, port)
        }

        return when (response.statusCode()) {
            200 -> try {
                Gson().fromJson(response.body(), serializableClass)
            } catch(jsonException: JsonSyntaxException) {
                throw JsonRequesterException(ip, port, response.body())
            }
            else -> throw GeneralRequesterException(ip, port, response.statusCode())
        }
    }
}