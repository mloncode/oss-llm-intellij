package com.github.bzz.intellij

import com.github.bzz.intellij.requests.Requester
import com.intellij.codeInsight.inline.completion.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.InlineCompletionProvider
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.DocumentEvent
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
class OSSLLMCompletionProvider : InlineCompletionProvider {

    private val logger = Logger.getInstance("(when autocompleting)")

    override suspend fun getProposals(request: InlineCompletionRequest): List<InlineCompletionElement> =
        try {
            val caretPosition = request.startOffset
            val proposals = Requester.getModelSuggestions(
                request.document.text.substring(0, caretPosition)
            )
            proposals.results.map { InlineCompletionElement(it.text) }
        } catch (knownException: Requester.RequesterException) {
            logger.warn(knownException.message)
            emptyList()
        } catch (exception: Exception) {
            logger.warn("Unknown exception. Message: ${exception.message}")
            emptyList()
        }

    override fun isEnabled(event: DocumentEvent): Boolean {
        return true
    }
}
