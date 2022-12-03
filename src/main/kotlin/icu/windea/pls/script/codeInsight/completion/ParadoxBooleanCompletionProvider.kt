package icu.windea.pls.script.codeInsight.completion

import com.intellij.codeInsight.completion.*
import com.intellij.util.*
import icu.windea.pls.core.*
import icu.windea.pls.core.codeInsight.completion.*
import icu.windea.pls.script.psi.*

/**
 * 提供布尔值的代码补全（在定义声明中不为属性或者块中的值提供）。
 */
class ParadoxBooleanCompletionProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		val position = parameters.position
		val stringElement = position.parent?.castOrNull<ParadoxScriptString>()
		if(stringElement != null && stringElement.isExpressionElement()) {
			if(stringElement.findParentDefinition() != null) return
		}
		
		result.addElement(PlsLookupElements.yesLookupElement)
		result.addElement(PlsLookupElements.noLookupElement)
	}
}
