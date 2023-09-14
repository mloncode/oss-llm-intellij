package com.github.bzz.intellij.toolWindow

import ai.grazie.utils.capitalize
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.bzz.intellij.MyBundle
import com.github.bzz.intellij.com.github.bzz.intellij.models.availableModels
import com.github.bzz.intellij.services.MyProjectService
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel


class MyToolWindowFactory : ToolWindowFactory {


    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
            val headerPanel = JPanel()
            headerPanel.add(JBLabel("Available models"))
            add(headerPanel)
            availableModels.forEach {
                val label = JBLabel(it.model.capitalize())
                val jPanel = JPanel()
                jPanel.apply {
                    add(label)
                    add(JButton("Apply").apply {
                        addActionListener {
                        }
                    })
                }
                add(jPanel)
            }
        }
    }
}
