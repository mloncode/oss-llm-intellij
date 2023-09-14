package com.github.bzz.intellij.requests

import com.github.bzz.intellij.com.github.bzz.intellij.requests.ServerResponse
import com.google.gson.Gson
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


object Requester {
    private val client = HttpClient.newBuilder().build()

    fun getModelSuggestions(context: String, ip: String? = null, port: Int? = null): ServerResponse {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://${ip ?: "localhost"}:${port ?: 8000}"))
            .POST(HttpRequest.BodyPublishers.ofString(context))
            .timeout(java.time.Duration.ofSeconds(10))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> Gson().fromJson(response.body(), ServerResponse::class.java)
            301, 302, 303, 307 -> {
                val newLocation = response.headers().firstValue("Location")
                    .orElseThrow {
                        RuntimeException("Redirect response is missing Location header")
                    }
                getModelSuggestions(context, newLocation)
            }
            else -> throw RuntimeException("HTTP Request failed. Failure status code: ${response.statusCode()}")
        }
    }
}