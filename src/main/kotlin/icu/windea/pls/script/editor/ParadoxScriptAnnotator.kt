package icu.windea.pls.script.editor

import com.intellij.lang.annotation.*
import com.intellij.lang.annotation.HighlightSeverity.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import icu.windea.pls.*
import icu.windea.pls.config.core.*
import icu.windea.pls.config.core.config.*
import icu.windea.pls.config.cwt.*
import icu.windea.pls.config.cwt.config.*
import icu.windea.pls.config.cwt.expression.*
import icu.windea.pls.core.*
import icu.windea.pls.core.expression.*
import icu.windea.pls.core.expression.nodes.*
import icu.windea.pls.script.psi.*
import icu.windea.pls.script.highlighter.ParadoxScriptAttributesKeys as Keys

/**
 * 脚本文件的注解器。
 *
 * * 提供定义的特殊颜色高亮。（基于CWT规则）
 * * 提供定义成员的特殊颜色高亮。（基于CWT规则）
 * * 提供特殊标签的特殊颜色高亮。（基于扩展的CWT规则）
 */
class ParadoxScriptAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when(element) {
			is ParadoxScriptProperty -> annotateProperty(element, holder)
			is ParadoxScriptStringExpressionElement -> annotateExpressionElement(element, holder)
			is ParadoxScriptInt -> annotateExpressionElement(element, holder)
		}
	}
	
	private fun annotateProperty(element: ParadoxScriptProperty, holder: AnnotationHolder) {
		val definitionInfo = element.definitionInfo
		if(definitionInfo != null) return annotateDefinition(element, holder, definitionInfo)
	}
	
	private fun annotateDefinition(element: ParadoxScriptProperty, holder: AnnotationHolder, definitionInfo: ParadoxDefinitionInfo) {
		//颜色高亮
		holder.newSilentAnnotation(INFORMATION).range(element.propertyKey).textAttributes(Keys.DEFINITION_KEY).create()
		val nameField = definitionInfo.typeConfig.nameField
		if(nameField != null) {
			//如果存在，高亮定义名对应的字符串（可能还有其他高亮）（这里不能使用PSI链接）
			val propertyElement = element.findProperty(nameField, true) //不处理内联的情况
			val nameElement = propertyElement?.findValue<ParadoxScriptString>()
			if(nameElement != null) {
				val nameString = definitionInfo.name.escapeXml().orAnonymous()
				val typesString = definitionInfo.typesText
				val tooltip = PlsBundle.message("script.annotator.definitionName", nameString, typesString)
				holder.newSilentAnnotation(INFORMATION).range(nameElement)
					.tooltip(tooltip)
					.textAttributes(Keys.DEFINITION_NAME_KEY)
					.create()
			}
		}
	}
	
	private fun annotateComplexEnumValue(element: ParadoxScriptExpressionElement, holder: AnnotationHolder, complexEnumValueInfo: ParadoxComplexEnumValueInfo) {
		//高亮复杂枚举名对应的字符串（可能还有其他高亮）（这里不能使用PSI链接）
		val nameString = complexEnumValueInfo.name.escapeXml().orAnonymous()
		val enumNameString = complexEnumValueInfo.enumName
		val tooltip = PlsBundle.message("script.annotator.complexEnumValueName", nameString, enumNameString)
		holder.newSilentAnnotation(INFORMATION).range(element)
			.tooltip(tooltip)
			.textAttributes(Keys.COMPLEX_ENUM_VALUE_NAME_KEY)
			.create()
	}
	
	private fun annotateExpressionElement(element: ParadoxScriptExpressionElement, holder: AnnotationHolder) {
		checkLiteralElement(element, holder)
		
		val isKey = element is ParadoxScriptPropertyKey
		val config = ParadoxCwtConfigHandler.resolveConfigs(element, !isKey, isKey).firstOrNull()
		if(config != null) {
			annotateExpression(element, element.textRange, null, config, holder)
		}
		
		if(element is ParadoxScriptStringExpressionElement) {
			val complexEnumValueInfo = element.complexEnumValueInfo
			if(complexEnumValueInfo != null) {
				annotateComplexEnumValue(element, holder, complexEnumValueInfo)
			}
		}
	}
	
	private fun checkLiteralElement(element: PsiElement, holder: AnnotationHolder) {
		val text = element.text
		if(text.isLeftQuoted() && !text.isRightQuoted()) {
			//missing closing quote
			holder.newAnnotation(ERROR, PlsBundle.message("syntax.error.missing.closing.quote")).create()
		}
	}
	
	private fun annotateExpression(
		element: ParadoxScriptExpressionElement,
		range: TextRange,
		rangeInElement: TextRange?,
		config: CwtConfig<*>,
		holder: AnnotationHolder
	) {
		val configExpression = config.expression ?: return
		
		//高亮特殊标签
		if(config is CwtValueConfig && config.isTagConfig) {
			holder.newSilentAnnotation(INFORMATION).range(element).textAttributes(Keys.TAG_KEY).create()
		}
		
		//如果不是字符串，除非是定义引用，否则不作高亮
		if(element !is ParadoxScriptStringExpressionElement && configExpression.type != CwtDataType.Definition) {
			return
		}
		
		//颜色高亮
		val configGroup = config.info.configGroup
		val text = rangeInElement?.substring(element.text) ?: element.text
		val isKey = element is ParadoxScriptPropertyKey
		when(configExpression.type) {
			CwtDataType.Localisation -> {
				if(text.isParameterAwareExpression()) return
				val attributesKey = Keys.LOCALISATION_REFERENCE_KEY
				holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
			}
			CwtDataType.SyncedLocalisation -> {
				if(text.isParameterAwareExpression()) return
				val attributesKey = Keys.SYNCED_LOCALISATION_REFERENCE_KEY
				holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
			}
			CwtDataType.InlineLocalisation -> {
				if(!element.text.isLeftQuoted()) {
					if(text.isParameterAwareExpression()) return
					val attributesKey = Keys.LOCALISATION_REFERENCE_KEY
					holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
				}
			}
			CwtDataType.StellarisNameFormat -> {
				//TODO
			}
			CwtDataType.Definition -> {
				if(text.isParameterAwareExpression()) return
				val attributesKey = Keys.DEFINITION_REFERENCE_KEY
				holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
			}
			CwtDataType.AbsoluteFilePath -> {
				if(text.isParameterAwareExpression()) return
				val attributesKey = Keys.PATH_REFERENCE_KEY
				holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
			}
			CwtDataType.FilePath -> {
				if(text.isParameterAwareExpression()) return
				val attributesKey = Keys.PATH_REFERENCE_KEY
				holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
			}
			CwtDataType.Icon -> {
				if(text.isParameterAwareExpression()) return
				val attributesKey = Keys.PATH_REFERENCE_KEY
				holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
			}
			CwtDataType.Enum -> {
				if(text.isParameterAwareExpression()) return
				val enumName = configExpression.value ?: return
				val attributesKey = when {
					enumName == CwtConfigHandler.paramsEnumName -> Keys.ARGUMENT_KEY
					configGroup.enums[enumName] != null -> Keys.ENUM_VALUE_KEY
					configGroup.complexEnums[enumName] != null -> Keys.COMPLEX_ENUM_VALUE_KEY
					else -> Keys.ENUM_VALUE_KEY
				}
				holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
			}
			CwtDataType.Value, CwtDataType.ValueSet -> {
				//not key/value or quoted -> only value set value name, no scope info
				if(config !is CwtDataConfig<*> || text.isLeftQuoted()) {
					val valueSetName = config.expression?.value ?: return
					val textAttributesKey = when(valueSetName) {
						"variable" -> Keys.VARIABLE_KEY
						else -> Keys.VALUE_SET_VALUE_KEY
					}
					holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(textAttributesKey).create()
					return
				}
				val textRange = TextRange.create(0, text.length)
				val valueSetValueExpression = ParadoxValueSetValueExpression.resolve(text, textRange, config, configGroup, isKey) ?: return
				annotateComplexExpression(element, valueSetValueExpression, config, range, holder)
			}
			CwtDataType.ScopeField, CwtDataType.Scope, CwtDataType.ScopeGroup -> {
				if(text.isLeftQuoted()) return
				val textRange = TextRange.create(0, text.length)
				val scopeFieldExpression = ParadoxScopeFieldExpression.resolve(text, textRange, configGroup, isKey) ?: return
				annotateComplexExpression(element, scopeFieldExpression, config, range, holder)
			}
			CwtDataType.ValueField, CwtDataType.IntValueField -> {
				if(text.isLeftQuoted()) return
				val textRange = TextRange.create(0, text.length)
				val valueFieldExpression = ParadoxValueFieldExpression.resolve(text, textRange, configGroup, isKey) ?: return
				annotateComplexExpression(element, valueFieldExpression, config, range, holder)
			}
			CwtDataType.VariableField, CwtDataType.IntVariableField -> {
				if(text.isLeftQuoted()) return
				val textRange = TextRange.create(0, text.length)
				val variableFieldExpression = ParadoxVariableFieldExpression.resolve(text, textRange, configGroup, isKey) ?: return
				annotateComplexExpression(element, variableFieldExpression, config, range, holder)
			}
			CwtDataType.Modifier -> {
				if(text.isParameterAwareExpression()) return
				val attributesKey = Keys.MODIFIER_KEY
				holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
			}
			CwtDataType.AliasName, CwtDataType.AliasKeysField -> {
				if(text.isParameterAwareExpression()) return
				val aliasName = configExpression.value ?: return
				val aliasMap = configGroup.aliasGroups.get(aliasName) ?: return
				val aliasSubName = CwtConfigHandler.getAliasSubName(text, false, aliasName, configGroup) ?: return
				val aliasConfig = aliasMap[aliasSubName]?.first() ?: return
				annotateExpression(element, range, rangeInElement, aliasConfig, holder)
			}
			CwtDataType.TemplateExpression, CwtDataType.Constant -> {
				if(text.isParameterAwareExpression()) return
				val isAnnotated = annotateAliasName(config, holder, range)
				if(isAnnotated) return
				if(rangeInElement == null) {
					if(element is ParadoxScriptPropertyKey && configExpression is CwtKeyExpression) return //unnecessary
					if(element is ParadoxScriptString && configExpression is CwtValueExpression) return //unnecessary
				}
				val attributesKey = when(configExpression) {
					is CwtKeyExpression -> Keys.PROPERTY_KEY_KEY
					is CwtValueExpression -> Keys.STRING_KEY
				}
				holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
			}
			else -> return
		}
	}
	
	private fun annotateAliasName(config: CwtConfig<*>, holder: AnnotationHolder, range: TextRange): Boolean {
		val aliasConfig = config.findAliasConfig() ?: return false
		val type = aliasConfig.expression.type
		if(!type.isGeneratorType()) return false
		val aliasName = aliasConfig.name
		val attributesKey = when {
			aliasName == "modifier" -> Keys.MODIFIER_KEY
			aliasName == "trigger" -> Keys.TRIGGER_KEY
			aliasName == "effect" -> Keys.EFFECT_KEY
			else -> return false
		}
		holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(attributesKey).create()
		return true
	}
	
	private fun annotateComplexExpression(element: ParadoxScriptExpressionElement, expression: ParadoxComplexExpression, config: CwtConfig<*>, range: TextRange, holder: AnnotationHolder) {
		if(element !is ParadoxScriptStringExpressionElement) return
		doAnnotateComplexExpression(element, expression, config, range, holder)
	}
	
	private fun doAnnotateComplexExpression(element: ParadoxScriptStringExpressionElement, expressionNode: ParadoxExpressionNode, config: CwtConfig<*>, range: TextRange, holder: AnnotationHolder) {
		val rangeToAnnotate = expressionNode.rangeInExpression.shiftRight(range.startOffset)
		val attributesKey = expressionNode.getAttributesKey()
		
		if(attributesKey != null) {
			if(expressionNode is ParadoxTokenExpressionNode) {
				//override default highlight by highlighter (property key or string)
				holder.newSilentAnnotation(INFORMATION).textAttributes(HighlighterColors.TEXT).create()
			}
			holder.newSilentAnnotation(INFORMATION).range(rangeToAnnotate).textAttributes(attributesKey).create()
		}
		val attributesKeyConfig = expressionNode.getAttributesKeyConfig(element)
		if(attributesKeyConfig != null) {
			annotateExpression(element, rangeToAnnotate, expressionNode.rangeInExpression, attributesKeyConfig, holder)
		}
		if(expressionNode.nodes.isNotEmpty()) {
			for(node in expressionNode.nodes) {
				doAnnotateComplexExpression(element, node, config, range, holder)
			}
		}
	}
}
