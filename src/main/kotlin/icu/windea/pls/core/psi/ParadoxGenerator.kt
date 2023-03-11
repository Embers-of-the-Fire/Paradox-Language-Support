package icu.windea.pls.core.psi

import com.intellij.application.options.*
import com.intellij.ide.actions.*
import com.intellij.ide.util.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.codeInsight.generation.*
import icu.windea.pls.localisation.*
import icu.windea.pls.tool.*
import org.jetbrains.plugins.notebooks.visualization.r.inlays.*

@Suppress("UNUSED_PARAMETER")
object ParadoxGenerator {
    //TODO
    
    fun generateLocalisations(context: GenerateLocalisationsContext, project: Project, editor: Editor, file: PsiFile) {
        val name = "generated localisations of definition ${context.definitionName}"
        val localeConfig = preferredParadoxLocale() ?: return //unexpected
        val text = buildString {
            append(localeConfig.id).append(":\n")
            val indentSize = CodeStyle.getSettings(project).getIndentOptions(ParadoxLocalisationFileType).INDENT_SIZE
            val indent = " ".repeat(indentSize)
            for(localisationName in context.localisationNames) {
                appendLocalisationLine(indent, localisationName)
            }
        }
        createAndOpenLocalisationTempFile(name, text, project)
    }
    
    fun generateLocalisationsInFile(context: GenerateLocalisationsInFileContext, project: Project, editor: Editor, file: PsiFile) {
        val name = "generated localisations of file ${context.fileName}"
        val localeConfig = preferredParadoxLocale() ?: return //unexpected
        val text = buildString {
            append(localeConfig.id).append(":\n")
            val indentSize = CodeStyle.getSettings(project).getIndentOptions(ParadoxLocalisationFileType).INDENT_SIZE
            val indent = " ".repeat(indentSize)
            for(context0 in context.contextList) {
                for(localisationName in context0.localisationNames) {
                    appendLocalisationLine(indent, localisationName)
                }
            }
        }
        createAndOpenLocalisationTempFile(name, text, project)
    }
    
    private fun StringBuilder.appendLocalisationLine(indent: String, localisationName: String) {
        append(indent)
        append(localisationName)
        append(": \"")
        append("REPLACE_ME")
        append("\"\n")
    }
    
    private fun createAndOpenLocalisationTempFile(name: String, text: String, project: Project) {
        val lightFile = ParadoxFileManager.createLightFile(name, text, ParadoxLocalisationLanguage)
        lightFile.bom = PlsConstants.utf8Bom //这里需要直接这样添加bom
        OpenFileAction.openFile(lightFile, project) //在编辑器中打开临时文件
    }
}