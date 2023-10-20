package org.intellij.ml.llm

import org.intellij.ml.llm.server.OSSLLMServer
import com.intellij.codeInsight.inline.completion.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.InlineCompletionProvider
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.event.DocumentEvent
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
class OSSLLMCompletionProvider : InlineCompletionProvider {

    private val logger = thisLogger()

    override suspend fun getProposals(request: InlineCompletionRequest): List<InlineCompletionElement> {
        try {
            val caretPosition = request.startOffset
            val proposals = OSSLLMServer.getSuggestions(
                request.document.text.substring(0, caretPosition)
            )
            return proposals.results.map { InlineCompletionElement(it) }
        } catch (knownException: OSSLLMServer.ServerException) {
            logger.warn(knownException.message)
            return emptyList()
        }
//        catch (exception: Exception) {
//            logger.warn("Unknown exception. Message: ${exception.message}")
//            return emptyList()
//        }
    }

    override fun isEnabled(event: DocumentEvent): Boolean {
        return true
    }
}
