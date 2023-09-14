package com.github.bzz.intellij.toolWindow

import ai.grazie.utils.capitalize
import com.github.bzz.intellij.com.github.bzz.intellij.models.AvailableModels.currentModelIndex
import com.github.bzz.intellij.com.github.bzz.intellij.models.AvailableModels.modelsList
import com.github.bzz.intellij.services.MyProjectService
import com.github.weisj.jsvg.f
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import java.awt.Font
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel


class MyToolWindowFactory : ToolWindowFactory {

    companion object {
        val NO_LLM =
            """Unfortunately, no applicable large language models are currently found. 
                |You may try to stabilize your Internet connection and restart Intellij IDEA.""".trimMargin()
    }


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
            headerPanel.add(JBLabel(if (modelsList.isNotEmpty()) "Available models" else NO_LLM).apply {
                font = font.deriveFont(Font.BOLD)
            })
            add(headerPanel)

            if (modelsList.isNotEmpty()) {
                val footerLabel = JBLabel("Current LLM: ${modelsList[currentModelIndex.get()]}")

                modelsList.forEachIndexed { index, model ->
                    add(JPanel().apply {
                        add(JBLabel(model.capitalize()))
                        add(JButton("Apply").apply {
                            addActionListener {
                                currentModelIndex.set(index)
                                footerLabel.text = "Current LLM: ${modelsList[currentModelIndex.get()]}"
                            }
                        })
                    })
                }
                add(JPanel().apply { add(footerLabel) })
            }
        }
    }
}
