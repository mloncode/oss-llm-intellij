package com.github.bzz.intellij.com.github.bzz.intellij.models

import java.util.concurrent.atomic.AtomicInteger


object AvailableModels {

    val currentModelIndex = AtomicInteger(0)

    val modelsList: List<String> = listOf("proxy-model", "other proxy-model")
}
