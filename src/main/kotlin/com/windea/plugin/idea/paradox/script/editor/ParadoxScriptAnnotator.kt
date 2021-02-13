@file:Suppress("UNCHECKED_CAST")

package com.windea.plugin.idea.paradox.script.editor

import com.intellij.lang.annotation.*
import com.intellij.lang.annotation.HighlightSeverity.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.windea.plugin.idea.paradox.*
import com.windea.plugin.idea.paradox.localisation.highlighter.*
import com.windea.plugin.idea.paradox.script.highlighter.*
import com.windea.plugin.idea.paradox.script.psi.*
import com.windea.plugin.idea.paradox.util.*

class ParadoxScriptAnnotator : Annotator, DumbAware {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when(element) {
			is ParadoxScriptProperty -> annotateProperty(element, holder)
			is ParadoxScriptVariableReference -> annotateVariableReference(element, holder)
			is ParadoxScriptString -> annotateString(element, holder)
		}
	}
	
	private fun annotateProperty(element: ParadoxScriptProperty, holder: AnnotationHolder) {
		//如果是定义，则
		val definitionInfo = element.paradoxDefinitionInfo
		if(definitionInfo != null) annotateDefinition(element, holder,definitionInfo)
	}
	
	private fun annotateDefinition(element: ParadoxScriptProperty, holder: AnnotationHolder, definitionInfo: ParadoxDefinitionInfo) {
		holder.newSilentAnnotation(INFORMATION)
			.range(element.propertyKey)
			.textAttributes(ParadoxScriptAttributesKeys.DEFINITION_KEY)
			.create()
		
		//检查definitionProperty的名字、数量是否合法，第一次不合法不会中断检查
		val existProperties = (element.propertyValue?.value as? ParadoxScriptBlock)?.propertyList
		if(!existProperties.isNullOrEmpty()){
			val properties = definitionInfo.properties
			val keyPatternExpressions = mutableListOf<ConditionalExpression>()
			properties.keys.mapTo(keyPatternExpressions){ it.toConditionalExpression() }
			
			val gameType = element.paradoxFileInfo?.gameType?:return
			val ruleGroup = paradoxRuleGroups[gameType.key]?:return
			val project = element.project
			
			//遍历existProperties
			annotateDefinitionProperties(existProperties, keyPatternExpressions, project, definitionInfo, ruleGroup, holder)
		}
	}
	
	private fun annotateDefinitionProperties(existProperties:List<ParadoxScriptProperty>,keyPatternExpressions: List<ConditionalExpression>,project: Project,
		definitionInfo: ParadoxDefinitionInfo, ruleGroup: ParadoxRuleGroup, holder: AnnotationHolder,subpaths:List<String> = listOf()){
		loop@ for(existProperty in existProperties){
			//如果definitionProperty本身就是definition，则跳过检查
			if(existProperty.paradoxDefinitionInfo != null) continue
			
			val existPropertyName = existProperty.name
			val typeText = definitionInfo.typeText
			val childSubpaths = subpaths + existPropertyName
			
			val requiredData = keyPatternExpressions.filter { it.required }.mapTo(mutableSetOf()) { it.value }
			val state = resolveKeyPatternExpressions(keyPatternExpressions, project, existPropertyName, ruleGroup,requiredData)
			
			//如果有问题
			when(state) {
				ValidateState.Unresolved -> {
					holder.newAnnotation(ERROR, message("paradox.script.annotator.unresolvedDefinitionProperty", existPropertyName, typeText))
						.range(existProperty.propertyKey)
						.create()
					continue@loop
				}
				ValidateState.Dupliate -> {
					holder.newAnnotation(ERROR, message("paradox.script.annotator.duplicateDefinitionProperty", existPropertyName, typeText))
						.range(existProperty.propertyKey)
						.create()
					continue@loop
				}
				else -> {}
			}
			
			//如果有缺失的属性
			if(requiredData.isNotEmpty()){
				for(name in requiredData) {
					holder.newAnnotation(ERROR, message("paradox.script.annotator.missingDefinitionProperty", name, typeText))
						.range(existProperty.propertyKey)
						.create()
				}
			}
			
			//没有问题再去检查可能的子属性
			val childExistProperties = (existProperty.propertyValue?.value as? ParadoxScriptBlock)?.propertyList
			if(!childExistProperties.isNullOrEmpty()) {
				val childKeyPatternExpressions = mutableListOf<ConditionalExpression>()
				for(childProperties in definitionInfo.resolvePropertiesList(mutableListOf(existProperty.name))) {
					childProperties.keys.mapTo(childKeyPatternExpressions){ it.toConditionalExpression() }
				}
				//递归检查
				//TODO 重构
				annotateDefinitionProperties(childExistProperties, childKeyPatternExpressions, project, definitionInfo, ruleGroup, holder,childSubpaths )
			}
		}
	}
	
	private fun resolveKeyPatternExpressions(keyPatternExpressions: List<ConditionalExpression>, project: Project,
		existPropertyName: String, ruleGroup: ParadoxRuleGroup, requiredData: MutableSet<String>): ValidateState {
		val multipleData = mutableSetOf<String>() //用于检查不可重复的keyPattern是否重复 
		for(keyPatternExpression in keyPatternExpressions) {
			val keyPattern = keyPatternExpression.value
			
			when {
				//$$类型
				keyPattern.startsWith("$$") -> {
					//TODO
					return afterResolved(keyPatternExpression, multipleData,requiredData)
				}
				//$类型（可能有子类型）
				keyPattern.startsWith("$") -> {
					val (type, subtypes) = keyPattern.drop(1).toTypeExpression()
					var matchedDefinitions = findDefinitions(existPropertyName, type, project)
					if(subtypes.isNotEmpty()) {
						matchedDefinitions = matchedDefinitions.filter { matchedDefinition ->
							val matchedSubtypes = matchedDefinition.paradoxDefinitionInfo?.subtypes
							matchedSubtypes != null && matchedSubtypes.any { it.name in subtypes }
						}
					}
					if(matchedDefinitions.isNotEmpty()) {
						return afterResolved(keyPatternExpression, multipleData,requiredData)
					}
				}
				//枚举
				keyPattern.startsWith("enum:") -> {
					val enum = ruleGroup.enums[keyPattern.drop(5)] ?: continue
					val enumValues = enum.enumValues
					if(existPropertyName in enumValues) {
						return afterResolved(keyPatternExpression, multipleData,requiredData)
					}
				}
				//基本类型int
				keyPattern == "int" -> {
					if(existPropertyName.isInt()) {
						return afterResolved(keyPatternExpression, multipleData,requiredData)
					}
				}
				//基本类型float
				keyPattern == "float" -> {
					if(existPropertyName.isFloat()) {
						return afterResolved(keyPatternExpression, multipleData,requiredData)
					}
				}
				//字符串
				else -> {
					if(existPropertyName == keyPattern) {
						return afterResolved(keyPatternExpression, multipleData,requiredData)
					}
				}
			}
		}
		
		return ValidateState.Unresolved
	}
	
	private fun afterResolved(keyPatternExpression:ConditionalExpression, multipleData: MutableSet<String>,requiredData: MutableSet<String>): ValidateState {
		val (keyPattern,_,required,multiple) = keyPatternExpression
		if(!multiple) {
			if(keyPattern in multipleData) {
				return ValidateState.Dupliate
			} else {
				multipleData.add(keyPattern)
			}
		}
		if(required){
			requiredData.remove(keyPattern)
		}
		return ValidateState.Ok
	}
	
	private fun annotateVariableReference(element: ParadoxScriptVariableReference, holder: AnnotationHolder) {
		//注明无法解析的情况
		val reference = element.reference
		if(reference.resolve() == null) {
			holder.newAnnotation(ERROR, message("paradox.script.annotator.unresolvedVariable", element.name))
				.create()
		}
	}
	
	private fun annotateString(element: ParadoxScriptString, holder: AnnotationHolder) {
		val name = element.value
		val project = element.project
		
		//注明所有对应名称的脚本属性，或者本地化属性（如果存在）
		val definition = findDefinition(name, null, project)
		if(definition != null) {
			holder.newSilentAnnotation(INFORMATION)
				.textAttributes(ParadoxScriptAttributesKeys.PROPERTY_KEY_KEY)
				.create()
			return
		}
		val localisation = findLocalisation(name, null, project, hasDefault = true)
		if(localisation != null) {
			holder.newSilentAnnotation(INFORMATION)
				.textAttributes(ParadoxLocalisationAttributesKeys.PROPERTY_KEY_KEY)
				.create()
		}
	}
}
