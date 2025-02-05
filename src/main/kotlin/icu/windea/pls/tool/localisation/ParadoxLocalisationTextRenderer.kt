package icu.windea.pls.tool.localisation

import icu.windea.pls.cwt.psi.*
import icu.windea.pls.lang.*
import icu.windea.pls.localisation.psi.*
import icu.windea.pls.script.psi.*
import java.util.*

@Suppress("unused", "UNUSED_PARAMETER")
object ParadoxLocalisationTextRenderer {
    class Context(
        var builder: StringBuilder
    ) {
        val guardStack = LinkedList<String>() //防止StackOverflow
    }
    
    fun render(element: ParadoxLocalisationProperty): String {
        return buildString { renderTo(this, element) }
    }
    
    fun renderTo(builder: StringBuilder, element: ParadoxLocalisationProperty) {
        val context = Context(builder)
        context.guardStack.addLast(element.name)
        renderTo(element, context)
    }
    
    private fun renderTo(element: ParadoxLocalisationProperty, context: Context) {
        val richTextList = element.propertyValue?.richTextList
        if(richTextList.isNullOrEmpty()) return
        for(richText in richTextList) {
            renderTo(richText, context)
        }
    }
    
    private fun renderTo(element: ParadoxLocalisationRichText, context: Context) {
        when(element) {
            is ParadoxLocalisationString -> renderStringTo(element, context)
            is ParadoxLocalisationEscape -> renderEscapeTo(element, context)
            is ParadoxLocalisationPropertyReference -> renderPropertyReferenceTo(element, context)
            is ParadoxLocalisationIcon -> renderIconTo(element, context)
            is ParadoxLocalisationCommand -> renderCommandTo(element, context)
            is ParadoxLocalisationColorfulText -> renderColorfulTextTo(element, context)
        }
    }
    
    private fun renderStringTo(element: ParadoxLocalisationString, context: Context) {
        context.builder.append(element.text)
    }
    
    private fun renderEscapeTo(element: ParadoxLocalisationEscape, context: Context) {
        val elementText = element.text
        when {
            elementText == "\\n" -> context.builder.append("\n")
            elementText == "\\t" -> context.builder.append("\t")
            elementText.length > 1 -> context.builder.append(elementText[1])
        }
    }
    
    private fun renderPropertyReferenceTo(element: ParadoxLocalisationPropertyReference, context: Context) {
        val resolved = element.reference?.resolve()
            ?: element.scriptedVariableReference?.reference?.resolve()
		when {
			resolved is ParadoxLocalisationProperty -> {
				val resolvedName = resolved.name
				if(context.guardStack.contains(resolvedName)) {
					//infinite recursion, do not render context
					context.builder.append(element.text)
				} else {
                    context.guardStack.addLast(resolvedName)
                    try {
                        renderTo(resolved, context)
                    } finally {
                        context.guardStack.removeLast()
                    }
				}
			}
			resolved is CwtProperty -> {
				context.builder.append(resolved.value)
			}
			resolved is ParadoxScriptScriptedVariable && resolved.value != null -> {
				context.builder.append(resolved.value)
			}
			else -> {
				context.builder.append(element.text)
			}
		}
    }
    
    private fun renderIconTo(element: ParadoxLocalisationIcon, context: Context) {
        //NOTE 不提取到结果中
        //builder.append(":${element.name}:")
    }
    
    private fun renderCommandTo(element: ParadoxLocalisationCommand, context: Context) {
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
                for(v in richTextList) {
                    renderTo(v, context)
                }
            } else {
                context.builder.append(conceptName.text)
            }
            return
        }
        //使用原始文本
        context.builder.append(element.text)
    }
    
    private fun renderColorfulTextTo(element: ParadoxLocalisationColorfulText, context: Context) {
        //直接渲染其中的文本
        for(v in element.richTextList) {
            renderTo(v, context)
        }
    }
}