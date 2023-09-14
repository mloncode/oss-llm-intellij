package com.github.bzz.intellij

import com.intellij.codeInsight.inline.completion.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.InlineCompletionProvider;
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.editor.event.DocumentEvent
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
class OSSLLMCompletionProvider : InlineCompletionProvider {

    override suspend fun getProposals(request: InlineCompletionRequest): List<InlineCompletionElement> {
        return listOf(InlineCompletionElement("public static void"),InlineCompletionElement("this is a test \uD83E\uDD16"),)
    }

    override fun isEnabled(event: DocumentEvent): Boolean {
        return true
    }
}
