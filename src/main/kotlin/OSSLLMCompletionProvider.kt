package com.github.bzz.intellij

import com.github.bzz.intellij.requests.Requester
import com.intellij.codeInsight.inline.completion.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.InlineCompletionProvider
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.event.DocumentEvent

class OSSLLMCompletionProvider : InlineCompletionProvider {

    override suspend fun getProposals(request: InlineCompletionRequest): List<InlineCompletionElement> {
        try {
            val caretPosition = request.startOffset
            val proposal = Requester.getModelSuggestions(
                request.document.text.substring(0, caretPosition)
            ).text ?: return emptyList()
            return listOf(InlineCompletionElement(proposal))
        } catch (knownException: Requester.RequesterException) {
            thisLogger().warn(knownException.message)
            return emptyList()
        } catch (exception: Exception) {
            thisLogger().warn("Unknown exception. Message: ${exception.message}")
            return emptyList()
        }
    }

    override fun isEnabled(event: DocumentEvent): Boolean {
        return true
    }
}
