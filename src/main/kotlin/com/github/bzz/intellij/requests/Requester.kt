package com.github.bzz.intellij.requests

import org.apache.http.HttpStatus
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


object Requester {
    private val client = HttpClient.newBuilder().build()


    fun getModelSuggestions() : String {
        val request = HttpRequest.newBuilder().uri(
            URI.create("https://google.com")
        ).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}