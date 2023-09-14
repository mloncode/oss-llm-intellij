package com.github.bzz.intellij.com.github.bzz.intellij.models

data class CodeCompletionModel(val model: String)

val availableModels: List<CodeCompletionModel> = listOf(
    "starcoder",
    "codet5"
).map { CodeCompletionModel(it) }
