package com.github.bzz.intellij.com.github.bzz.intellij.models

import com.github.bzz.intellij.requests.Requester
import com.intellij.openapi.diagnostic.Logger
import java.util.concurrent.atomic.AtomicInteger


object AvailableModels {

    val currentModelIndex = AtomicInteger(0)

    private val logger = Logger.getInstance("(getting available models)")

    private fun getAvailableModels(): List<String> =
        try {
            Requester.getAvailableModels().models.map { it.modelName }
        } catch (knownException: Requester.RequesterException) {
            logger.warn(knownException.message)
            emptyList()
        } catch (otherException: Requester.RequesterException) {
            logger.warn("Unknown exception. Message: ${otherException.message}")
            emptyList()
        }

    val modelsList: List<String> = getAvailableModels()

    fun currentModel() : String? {
        return modelsList.getOrNull(currentModelIndex.get())
    }
}
