package com.github.bzz.intellij.requests

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


object Requester {
    private val client = HttpClient.newBuilder().build()

    fun getModelSuggestionsUpdated(): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://plugins.jetbrains.com/docs/intellij/psi-files.html"))
            .header("User-Agent", "Mozilla/5.0")
            .timeout(java.time.Duration.ofSeconds(10))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> response.body()
            301, 302, 303, 307 -> {
                val newLocation = response.headers().firstValue("Location")
                    .orElseThrow {
                        RuntimeException("Redirect response is missing Location header")
                    }
                getModelSuggestionsUpdated(newLocation)
            }

            else -> throw RuntimeException("HTTP Request failed. Failure status code: ${response.statusCode()}")
        }
    }
    private fun getModelSuggestionsUpdated(newLocation: String): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(newLocation))
            .header("User-Agent", "Mozilla/5.0")
            .timeout(java.time.Duration.ofSeconds(10))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() == 200) {
            return response.body()
        }
        else
        {
            throw RuntimeException("HTTP Request failed. Failure status code: ${response.statusCode()}")
        }
    }
}