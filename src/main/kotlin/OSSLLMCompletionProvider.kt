package com.github.bzz.intellij

import com.github.bzz.intellij.requests.Requester
import com.google.gson.JsonSyntaxException
import com.intellij.codeInsight.inline.completion.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.InlineCompletionProvider;
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.event.DocumentEvent

class OSSLLMCompletionProvider : InlineCompletionProvider {

    override suspend fun getProposals(request: InlineCompletionRequest): List<InlineCompletionElement> {
        try {
            val proposal = Requester.getModelSuggestions(request.document.text).text ?: return emptyList()
            return listOf(InlineCompletionElement(proposal))
        } catch(exception: RuntimeException) {
            thisLogger().warn("Message: ${exception.message}")
            return emptyList()
        }
    }

    override fun isEnabled(event: DocumentEvent): Boolean {
        return true
    }
}
