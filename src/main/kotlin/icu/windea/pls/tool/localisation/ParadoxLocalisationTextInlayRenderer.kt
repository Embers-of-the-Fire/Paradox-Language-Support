package icu.windea.pls.tool.localisation

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.progress.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.psi.*
import icu.windea.pls.cwt.psi.*
import icu.windea.pls.lang.*
import icu.windea.pls.localisation.highlighter.*
import icu.windea.pls.localisation.psi.*
import icu.windea.pls.script.psi.*
import icu.windea.pls.tool.*
import java.awt.*
import java.awt.event.*
import java.util.*
import java.util.concurrent.atomic.*

@Suppress("UnstableApiUsage")
object ParadoxLocalisationTextInlayRenderer {
    /**
     * 如果[truncateLimit]小于等于0，则仅渲染首行文本。
     */
    class Context(
        val editor: Editor,
        val factory: PresentationFactory,
        var builder: MutableList<InlayPresentation>
    ) {
        var truncateLimit: Int = -1
        var iconHeightLimit: Int = -1
        val truncateRemain by lazy { AtomicInteger(truncateLimit) } //记录到需要截断为止所剩余的长度
        val guardStack = LinkedList<String>() //防止StackOverflow
    }
    
    fun render(element: ParadoxLocalisationProperty, factory: PresentationFactory, editor: Editor, truncateLimit: Int, iconHeightLimit: Int): InlayPresentation? {
        //虽然看起来截断后的长度不正确，但是实际上是正确的，因为图标前后往往存在或不存在神秘的空白
        val context = Context(editor, factory, mutableListOf())
        context.truncateLimit = truncateLimit
        context.iconHeightLimit = iconHeightLimit
        context.guardStack.addLast(element.name)
        val r = renderTo(element, context)
        if(!r) {
            context.builder.add(factory.smallText("...")) //添加省略号
        }
        return context.builder.mergePresentation()
    }
    
    private fun renderTo(element: ParadoxLocalisationProperty, context: Context): Boolean {
        val richTextList = element.propertyValue?.richTextList
        if(richTextList == null || richTextList.isEmpty()) return true
        var continueProcess = true
        for(richText in richTextList) {
            ProgressManager.checkCanceled()
            val r = renderTo(richText, context)
            if(!r) {
                continueProcess = false
                break
            }
        }
        return continueProcess
    }
    
    private fun renderTo(element: ParadoxLocalisationRichText, context: Context): Boolean {
        return when(element) {
            is ParadoxLocalisationString -> renderStringTo(element, context)
            is ParadoxLocalisationEscape -> renderEscapeTo(element, context)
            is ParadoxLocalisationPropertyReference -> renderPropertyReferenceTo(element, context)
            is ParadoxLocalisationIcon -> renderIconTo(element, context)
            is ParadoxLocalisationCommand -> renderCommandTo(element, context)
            is ParadoxLocalisationColorfulText -> renderColorfulTextTo(element, context)
            else -> true
        }
    }
    
    private fun renderStringTo(element: ParadoxLocalisationString, context: Context): Boolean = with(context.factory) {
        val elementText = element.text
        context.builder.add(truncatedSmallText(elementText, context))
        return continueProcess(context)
    }
    
    private fun renderEscapeTo(element: ParadoxLocalisationEscape, context: Context): Boolean = with(context.factory) {
        //使用原始文本（内嵌注释不能换行，这时直接截断）
        val elementText = element.text
        val text = when {
            elementText == "\\n" -> return false
            elementText == "\\r" -> return false
            elementText == "\\t" -> "\t"
            else -> elementText
        }
        context.builder.add(truncatedSmallText(text, context))
        return continueProcess(context)
    }
    
    private fun renderPropertyReferenceTo(element: ParadoxLocalisationPropertyReference, context: Context): Boolean = with(context.factory) {
        //如果处理文本失败，则使用原始文本，如果有颜色码，则使用该颜色渲染，否则保留颜色码
        val colorConfig = element.colorConfig
        val resolved = element.reference?.resolve()
            ?: element.scriptedVariableReference?.reference?.resolve()
        val presentation = when {
            resolved is ParadoxLocalisationProperty -> {
                val resolvedName = resolved.name
                if(context.guardStack.contains(resolvedName)) {
                    //infinite recursion, do not render context
                    truncatedSmallText(element.text, context)
                } else {
                    context.guardStack.addLast(resolvedName)
                    try {
                        val oldBuilder = context.builder
                        context.builder = mutableListOf()
                        renderTo(resolved, context)
                        val newBuilder = context.builder
                        context.builder = oldBuilder
                        newBuilder.mergePresentation()
                    } finally {
                        context.guardStack.removeLast()
                    }
                }
            }
            resolved is CwtProperty -> {
                smallText(resolved.value ?: PlsConstants.unresolvedString)
            }
            resolved is ParadoxScriptScriptedVariable && resolved.value != null -> {
                smallText(resolved.value ?: PlsConstants.unresolvedString)
            }
            else -> {
                truncatedSmallText(element.text, context)
            }
        } ?: return true
        val textAttributesKey = if(colorConfig != null) ParadoxLocalisationAttributesKeys.getColorOnlyKey(colorConfig.color) else null
        val finalPresentation = when {
            textAttributesKey != null -> WithAttributesPresentation(presentation, textAttributesKey, context.editor)
            else -> presentation
        }
        context.builder.add(finalPresentation)
        return continueProcess(context)
    }
    
    private fun renderIconTo(element: ParadoxLocalisationIcon, context: Context): Boolean = with(context.factory) {
        val resolved = element.reference?.resolve() ?: return true
        val iconUrl = when {
            resolved is ParadoxScriptDefinitionElement -> ParadoxImageResolver.resolveUrlByDefinition(resolved, defaultToUnknown = true)
            resolved is PsiFile -> ParadoxImageResolver.resolveUrlByFile(resolved.virtualFile, defaultToUnknown = true)
            else -> return true
        }
        if(iconUrl.isNotEmpty()) {
            //忽略异常
            runCatching {
                //找不到图标的话就直接跳过
                val icon = IconLoader.findIcon(iconUrl.toFileUrl()) ?: return true
                if(icon.iconHeight <= context.iconHeightLimit) {
                    //基于内嵌提示的字体大小缩放图标，直到图标宽度等于字体宽度
                    val presentation = psiSingleReference(smallScaledIcon(icon)) { resolved }
                    context.builder.add(presentation)
                } else {
                    val unknownIcon = IconLoader.findIcon(PlsConstants.Paths.unknownPngUrl) ?: return true
                    val presentation = psiSingleReference(smallScaledIcon(unknownIcon)) { resolved }
                    context.builder.add(presentation)
                }
            }
        }
        return true
    }
    
    private fun renderCommandTo(element: ParadoxLocalisationCommand, context: Context): Boolean = with(context.factory) {
        val conceptName = element.conceptName
        if(conceptName != null) {
            //使用要显示的文本
            val conceptTextElement = ParadoxGameConceptHandler.getTextElement(conceptName)
            val richTextList = when {
                conceptTextElement is ParadoxLocalisationConceptText -> conceptTextElement.richTextList
                conceptTextElement is ParadoxLocalisationProperty -> conceptTextElement.propertyValue?.richTextList
                else -> null
            }
            if(richTextList != null) {
                val newBuilder = mutableListOf<InlayPresentation>()
                val oldBuilder = context.builder
                context.builder = newBuilder
                var continueProcess = true
                for(richText in richTextList) {
                    ProgressManager.checkCanceled()
                    val r = renderTo(richText, context)
                    if(!r) {
                        continueProcess = false
                        break
                    }
                }
                context.builder = oldBuilder
                val conceptAttributesKey = ParadoxLocalisationAttributesKeys.CONCEPT_KEY
                var presentation: InlayPresentation = SequencePresentation(newBuilder)
                
                val attributesFlags = WithAttributesPresentation.AttributesFlags().withSkipBackground(true).withSkipEffects(true)
                presentation = WithAttributesPresentation(presentation, conceptAttributesKey, context.editor, attributesFlags)
                presentation = onHover(psiSingleReference(presentation) { conceptName.reference?.resolve() }, object : InlayPresentationFactory.HoverListener {
                    override fun onHover(event: MouseEvent, translated: Point) {
                        attributesFlags.isDefault = true //change foreground
                    }
                    
                    override fun onHoverFinished() {
                        attributesFlags.isDefault = false //reset foreground
                    }
                    
                })
                context.builder.add(presentation)
                if(!continueProcess) return false
            } else {
                context.builder.add(smallText(conceptName.name))
            }
            return continueProcess(context)
        }
        
        //直接显示命令文本，适用对应的颜色高亮
        //点击其中的相关文本也能跳转到相关声明（如scope和scripted_loc）
        element.forEachChild { e ->
            ProgressManager.checkCanceled()
            getElementPresentation(e, context)
        }
        return continueProcess(context)
    }
    
    private fun renderColorfulTextTo(element: ParadoxLocalisationColorfulText, context: Context): Boolean = with(context.factory) {
        //如果处理文本失败，则清除非法的颜色标记，直接渲染其中的文本
        val richTextList = element.richTextList
        if(richTextList.isEmpty()) return true
        val colorConfig = element.colorConfig
        val textAttributesKey = if(colorConfig != null) ParadoxLocalisationAttributesKeys.getColorOnlyKey(colorConfig.color) else null
        val oldBuilder = context.builder
        context.builder = mutableListOf()
        var continueProcess = true
        for(richText in richTextList) {
            ProgressManager.checkCanceled()
            val r = renderTo(richText, context)
            if(!r) {
                continueProcess = false
                break
            }
        }
        val newBuilder = context.builder
        context.builder = oldBuilder
        val presentation = newBuilder.mergePresentation() ?: return true
        val finalPresentation = if(textAttributesKey != null) WithAttributesPresentation(presentation, textAttributesKey, context.editor) else presentation
        context.builder.add(finalPresentation)
        return continueProcess
    }
    
    private fun MutableList<InlayPresentation>.mergePresentation(): InlayPresentation? {
        return when {
            isEmpty() -> null
            size == 1 -> first()
            else -> SequencePresentation(this)
        }
    }
    
    private fun PresentationFactory.truncatedSmallText(text: String, context: Context): InlayPresentation {
        if(context.truncateLimit <= 0) {
            val finalText = text
            val result = smallText(finalText)
            return result
        } else {
            val finalText = text.take(context.truncateRemain.get())
            val result = smallText(finalText)
            context.truncateRemain.addAndGet(-text.length)
            return result
        }
    }
    
    private fun continueProcess(context: Context): Boolean {
        return context.truncateLimit <= 0 || context.truncateRemain.get() >= 0
    }
    
    fun getElementPresentation(element: PsiElement, context: Context) = with(context.factory) {
        val text = element.text
        val references = element.references
        if(references.isEmpty()) {
            context.builder.add(smallText(element.text))
            return
        }
        var i = 0
        for(reference in references) {
            ProgressManager.checkCanceled()
            val startOffset = reference.rangeInElement.startOffset
            if(startOffset != i) {
                val s = text.substring(i, startOffset)
                context.builder.add(smallText(s))
            }
            i = reference.rangeInElement.endOffset
            val s = reference.rangeInElement.substring(text)
            val resolved = reference.resolve()
            //不要尝试跳转到valueSetValue的声明处
            if(resolved == null || resolved is ParadoxFakePsiElement) {
                context.builder.add(smallText(s))
            } else {
                context.builder.add(psiSingleReference(smallText(s)) { reference.resolve() })
            }
        }
        val endOffset = references.last().rangeInElement.endOffset
        if(endOffset != text.length) {
            val s = text.substring(endOffset)
            context.builder.add(smallText(s))
        }
    }
}
