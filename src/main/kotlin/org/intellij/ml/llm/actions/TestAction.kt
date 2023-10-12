package org.intellij.ml.llm.actions

import org.intellij.ml.llm.server.OSSLLMServer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger

class TestAction: AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val logger = Logger.getInstance("Server Answer")
        logger.warn("Requesting...")

        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val caretPosition = editor.caretModel.primaryCaret.selectionStart
        logger.warn(
            OSSLLMServer.getSuggestions(editor.document.text.substring(0, caretPosition)).
                results.firstOrNull() ?: "---"
        )
    }
}
