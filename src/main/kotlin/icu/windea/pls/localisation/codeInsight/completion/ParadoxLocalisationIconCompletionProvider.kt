package icu.windea.pls.localisation.codeInsight.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.*
import com.intellij.openapi.progress.*
import com.intellij.psi.*
import com.intellij.util.*
import icons.*
import icu.windea.pls.core.*
import icu.windea.pls.core.search.*
import icu.windea.pls.core.search.selector.*
import icu.windea.pls.lang.cwt.expression.*
import icu.windea.pls.script.psi.*

/**
 * 提供图标名字的代码补全。
 */
class ParadoxLocalisationIconCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        val originalFile = parameters.originalFile
        val project = originalFile.project
        val namesToDistinct = mutableSetOf<String>()
        
        //根据spriteName进行提示
        val spriteSelector = definitionSelector(project, originalFile).contextSensitive().distinctByName()
        ParadoxDefinitionSearch.search("sprite", spriteSelector).processQueryAsync p@{ sprite ->
            ProgressManager.checkCanceled()
            val definitionInfo = sprite.definitionInfo ?: return@p true
            if(definitionInfo.name.isEmpty()) return@p true //ignore anonymous definitions
            val name = definitionInfo.name.removePrefixOrNull("GFX_")?.removePrefix("text_")
            if(name != null && namesToDistinct.add(name)) {
                addLookupElement(name, sprite, result)
            }
            true
        }
        
        //根据ddsFileName进行提示
        val fileSelector = fileSelector(project, originalFile).contextSensitive().distinctByFilePath()
        val ddsFileExpression = CwtValueExpression.resolve("icon[gfx/interface/icons/]")
        ParadoxFilePathSearch.search(ddsFileExpression, fileSelector).processQueryAsync p@{ ddsFile ->
            ProgressManager.checkCanceled()
            val name = ddsFile.nameWithoutExtension
            val file = ddsFile.toPsiFile(project)
            if(file != null && namesToDistinct.add(name)) {
                addLookupElement(name, file, result)
            }
            true
        }
        
        //作为生成的图标处理（解析为其他类型的定义）
        val definitionSelector = definitionSelector(project, originalFile).contextSensitive().distinctByName()
        //如果iconName为job_head_researcher，定义head_researcher包含定义属性`icon = researcher`，则解析为该定义属性
        ParadoxDefinitionSearch.search("job", definitionSelector).processQueryAsync p@{ definition ->
            ProgressManager.checkCanceled()
            val definitionInfo = definition.definitionInfo ?: return@p true
            val jobName = definitionInfo.name
            val name = "job_$jobName"
            if(namesToDistinct.add(name)) {
                addLookupElement(name, definition, result)
            }
            true
        }
    }
    
    private fun addLookupElement(name: String, element: PsiElement, result: CompletionResultSet) {
        when(element) {
            //val tailText = " by $expression in ${config.pointer.containingFile?.name ?: anonymousString}"
            is ParadoxScriptDefinitionElement -> {
                val icon = PlsIcons.LocalisationIcon //使用特定图标
                val definitionInfo = element.definitionInfo //不应该为null
                val tailText = if(definitionInfo != null) " from ${definitionInfo.type} definition ${definitionInfo.name}" else ""
                val typeFile = element.containingFile
                val lookupElement = LookupElementBuilder.create(element, name).withIcon(icon)
                    .withTailText(tailText, true)
                    .withTypeText(typeFile.name, typeFile.icon, true)
                result.addElement(lookupElement)
            }
            is PsiFile -> {
                val icon = PlsIcons.LocalisationIcon //使用特定图标
                val tailText = " from dds file ${element.name}"
                val lookupElement = LookupElementBuilder.create(element, name).withIcon(icon)
                    .withTailText(tailText, true)
                    .withTypeText(element.name, element.icon, true)
                result.addElement(lookupElement)
            }
            else -> pass()
        }
    }
}
