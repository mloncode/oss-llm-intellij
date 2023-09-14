package com.github.bzz.intellij

import com.github.bzz.intellij.requests.Requester
import com.intellij.codeInsight.inline.completion.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.InlineCompletionProvider
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.event.DocumentEvent
import java.io.IOException

class OSSLLMCompletionProvider : InlineCompletionProvider {

    override suspend fun getProposals(request: InlineCompletionRequest): List<InlineCompletionElement> {
        try {
            val caretPosition = runReadAction {
                request.editor.caretModel.primaryCaret.selectionStart
            }
            val proposal = Requester.getModelSuggestions(
                request.document.text.substring(0, caretPosition)
            ).text ?: return emptyList()
            return listOf(InlineCompletionElement(proposal))
        } catch(exception: RuntimeException) {
            thisLogger().warn("Message: ${exception.message}")
            return emptyList()
        } catch(ioexception: IOException) {
            thisLogger().warn("Message: ${ioexception.message}")
            return emptyList()
        } catch(interruptedException: InterruptedException) {
            thisLogger().warn("Message: ${interruptedException.message}")
            return emptyList()
        }
    }

    override fun isEnabled(event: DocumentEvent): Boolean {
        return true
    }
}
