package icu.windea.pls.script.codeInsight.completion

import com.intellij.codeInsight.completion.*
import com.intellij.patterns.PlatformPatterns.*
import icu.windea.pls.*
import icu.windea.pls.core.codeInsight.completion.*
import icu.windea.pls.script.psi.*
import icu.windea.pls.script.psi.ParadoxScriptElementTypes.*

class ParadoxScriptCompletionContributor : CompletionContributor() {
	init {
		//当用户可能正在输入一个关键字（布尔值或子句）时提示
		val keywordPattern = psiElement(STRING_TOKEN)
		extend(CompletionType.BASIC, keywordPattern, ParadoxKeywordCompletionProvider())
		
		//当用户可能正在输入一个scriptedVariableReference的名字时提示
		val scriptedVariableReferencePattern = psiElement()
			.withElementType(ParadoxScriptTokenSets.SCRIPTED_VARIABLE_REFERENCE_TOKENS)
		extend(scriptedVariableReferencePattern, ParadoxScriptedVariableCompletionProvider())
		
		//当用户可能正在输入一个propertyKey或string时提示
		val definitionPattern = psiElement()
			.withElementType(ParadoxScriptTokenSets.KEY_OR_STRING_OR_SNIPPET_TOKENS)
		extend(definitionPattern, ParadoxDefinitionCompletionProvider())
		
		//当用户可能正在输入一个eventId时提示
		val eventIdPattern = psiElement()
			.withElementType(ParadoxScriptTokenSets.STRING_TOKENS)
			.withParent(psiElement(ParadoxScriptString::class.java)
				.withParent(psiElement(ParadoxScriptProperty::class.java)
					.withParent(psiElement(ParadoxScriptBlock::class.java)
						.withParent(psiElement(ParadoxScriptProperty::class.java)))))
		extend(eventIdPattern, ParadoxEventIdCompletionProvider())
		
		//当用户可能正在输入一个parameter的名字时提示
		val parameterPattern = psiElement()
			.withElementType(ParadoxScriptTokenSets.PARAMETER_TOKENS)
		extend(parameterPattern, ParadoxParameterCompletionProvider())
		
		//当用户可能正在输入一个scriptedVariable的名字时提示（除非用户也可能正在输入一个引用的名字）
		val scriptedVariableNamePattern = psiElement()
			.withElementType(SCRIPTED_VARIABLE_NAME_TOKEN)
		extend(CompletionType.BASIC, scriptedVariableNamePattern, ParadoxScriptedVariableNameCompletionProvider())
		
		//当用户可能正在输入一个定义的名字时提示
		val definitionNamePattern = psiElement()
			.withElementType(ParadoxScriptTokenSets.KEY_OR_STRING_OR_SNIPPET_TOKENS)
		extend(CompletionType.BASIC, definitionNamePattern, ParadoxDefinitionNameCompletionProvider())
		
		//当用户可能正在输入一个变量名时提示
		val variableNamePattern = psiElement()
			.withElementType(ParadoxScriptTokenSets.STRING_TOKENS)
		extend(CompletionType.BASIC, variableNamePattern, ParadoxVariableNameCompletionProvider())
	}
	
	override fun beforeCompletion(context: CompletionInitializationContext) {
		context.dummyIdentifier = PlsConstants.dummyIdentifier
	}
	
	@Suppress("RedundantOverride")
	override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
		super.fillCompletionVariants(parameters, result)
	}
}
