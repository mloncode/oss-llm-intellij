package com.github.bzz.intellij.com.github.bzz.intellij.models

import org.intellij.ml.llm.server.OSSLLMServer
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import java.util.concurrent.atomic.AtomicInteger


object OSSLLMModels {
    private val logger = thisLogger()

    val currentModelIndex = AtomicInteger(0)
    val modelsList: List<String> = OSSLLMServer.getAvailableModels().models

    fun currentModel() : String? {
        return modelsList.getOrNull(currentModelIndex.get())
    }
}
