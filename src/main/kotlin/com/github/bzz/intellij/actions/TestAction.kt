package com.github.bzz.intellij.actions

import com.github.bzz.intellij.requests.Requester
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
            Requester.getModelSuggestions(editor.document.text.substring(0, caretPosition)).
                results.firstOrNull()?.text ?: "---"
        )
    }
}
