package com.github.bzz.intellij.requests

import com.github.bzz.intellij.com.github.bzz.intellij.requests.ServerResponse
import com.google.gson.Gson
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


object Requester {
    class RequesterException(message: String): RuntimeException("RequesterException: $message")

    private val client = HttpClient.newBuilder().build()

    fun getModelSuggestions(context: String, ip: String = "localhost", port: Int = 8000): ServerResponse {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://$ip:$port"))
            .POST(HttpRequest.BodyPublishers.ofString(context))
            .timeout(java.time.Duration.ofSeconds(10))
            .build()

        val response = try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch(ioe: IOException) {
            throw RequesterException("Probable cause: server $ip is not running or listening at port $port.")
        }

        return when (response.statusCode()) {
            200 -> Gson().fromJson(response.body(), ServerResponse::class.java)
            301, 302, 303, 307 -> {
                val newLocation = response.headers().firstValue("Location")
                    .orElseThrow {
                        RequesterException("Redirect response from $ip is missing Location header")
                    }
                getModelSuggestions(context, newLocation)
            }
            else -> throw RequesterException("HTTP Request to $ip:$port failed. Failure status code: ${response.statusCode()}")
        }
    }
}