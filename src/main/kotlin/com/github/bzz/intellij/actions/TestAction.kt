package com.github.bzz.intellij.actions

import com.github.bzz.intellij.requests.Requester
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger

class TestAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val logger = Logger.getInstance("Server Answer")
        logger.warn("Requesting...")
        logger.warn(Requester.getModelSuggestions("null_context").text)
    }
}