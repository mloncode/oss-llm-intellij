package com.github.bzz.intellij.com.github.bzz.intellij.requests

import kotlinx.serialization.Serializable

@Serializable
data class ServerResponse (
    val text: String?
)