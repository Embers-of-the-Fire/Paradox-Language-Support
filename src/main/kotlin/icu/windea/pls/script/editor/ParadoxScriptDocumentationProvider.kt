package icu.windea.pls.script.editor

import com.intellij.lang.documentation.*
import com.intellij.psi.*
import icu.windea.pls.*
import icu.windea.pls.config.cwt.*
import icu.windea.pls.config.cwt.config.*
import icu.windea.pls.config.definition.*
import icu.windea.pls.core.*
import icu.windea.pls.core.model.*
import icu.windea.pls.core.selector.chained.*
import icu.windea.pls.core.tool.*
import icu.windea.pls.localisation.psi.*
import icu.windea.pls.script.psi.*

class ParadoxScriptDocumentationProvider : AbstractDocumentationProvider() {
	//do not provide special documentations for definition name and complex enum value name declarations,
	//for the expression represents a definition name or complex enum value name, can also be a localisation reference, etc.
	
	override fun getQuickNavigateInfo(element: PsiElement, originalElement: PsiElement?): String? {
		return when(element) {
			is ParadoxScriptScriptedVariable -> getScriptedVariableInfo(element)
			is ParadoxScriptProperty -> getPropertyInfo(element)
			//进行代码提示时，这里是有效的代码
			is ParadoxScriptStringExpressionElement -> {
				//only for complex enum value reference
				if(originalElement != null && originalElement.parent?.castOrNull<ParadoxScriptString>() !== element) {
					val complexEnumValueInfo = element.complexEnumValueInfo
					if(complexEnumValueInfo != null) return generateComplexEnumValueInfo(element, complexEnumValueInfo)
				}
				if(element is ParadoxScriptPropertyKey) {
					return getQuickNavigateInfo(element.parent, null)
				}
				null
			}
			else -> null
		}
	}
	
	private fun getScriptedVariableInfo(element: ParadoxScriptScriptedVariable): String {
		val name = element.name
		return buildString {
			buildScriptedVariableDefinition(element, name)
		}
	}
	
	private fun getPropertyInfo(element: ParadoxScriptProperty): String? {
		val definitionInfo = element.definitionInfo
		if(definitionInfo != null) return getDefinitionInfo(element, definitionInfo)
		val definitionMemberInfo = element.definitionMemberInfo
		if(definitionMemberInfo != null) return null //不为无法解析的属性元素提供文档
		val name = element.name
		return buildString {
			buildPropertyDefinition(element, name)
		}
	}
	
	private fun getDefinitionInfo(element: ParadoxScriptProperty, definitionInfo: ParadoxDefinitionInfo): String {
		return buildString {
			buildDefinitionDefinition(element, definitionInfo, null, null)
		}
	}
	
	private fun generateComplexEnumValueInfo(element: ParadoxScriptStringExpressionElement, complexEnumValueInfo: ParadoxComplexEnumValueInfo): String {
		return buildString { 
			buildComplexEnumValueDefinition(element, complexEnumValueInfo)
		}
	}
	
	//private fun getComplexEnumValueInfo(element: ParadoxScriptStringExpressionElement, config: CwtDataConfig<*>): String {
	//	return buildString {
	//		val configGroup = config.info.configGroup
	//		buildComplexEnumValueDefinition(element.value, config.expression.value.orEmpty(), configGroup)
	//	}
	//}
	
	override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
		return when(element) {
			is ParadoxScriptScriptedVariable -> getScriptedVariableDoc(element)
			is ParadoxScriptProperty -> getPropertyDoc(element)
			is ParadoxScriptStringExpressionElement -> {
				//only for complex enum value reference
				if(originalElement != null && originalElement.parent?.castOrNull<ParadoxScriptString>() !== element) {
					val complexEnumValueInfo = element.complexEnumValueInfo
					if(complexEnumValueInfo != null) return generateComplexEnumValueDoc(element, complexEnumValueInfo)
				}
				if(element is ParadoxScriptPropertyKey) {
					return generateDoc(element.parent, null)
				}
				null
			}
			else -> null
		}
	}
	
	private fun getScriptedVariableDoc(element: ParadoxScriptScriptedVariable): String {
		val name = element.name
		return buildString {
			buildScriptedVariableDefinition(element, name)
			buildLineCommentContent(element)
		}
	}
	
	private fun getPropertyDoc(element: ParadoxScriptProperty): String? {
		val definitionInfo = element.definitionInfo
		if(definitionInfo != null) return getDefinitionDoc(element, definitionInfo)
		val definitionMemberInfo = element.definitionMemberInfo
		if(definitionMemberInfo != null) return null //不为无法解析的属性元素提供文档
		val name = element.name
		return buildString {
			buildPropertyDefinition(element, name)
			buildLineCommentContent(element)
		}
	}
	
	private fun getDefinitionDoc(element: ParadoxScriptProperty, definitionInfo: ParadoxDefinitionInfo): String {
		return buildString {
			//在definition部分，相关图片信息显示在相关本地化信息之后，在sections部分则显示在之前
			val localisationTargetMap = mutableMapOf<String, ParadoxLocalisationProperty>()
			val imageTargetMap = mutableMapOf<String, Tuple2<PsiFile, Int>>()
			buildDefinitionDefinition(element, definitionInfo, localisationTargetMap, imageTargetMap)
			buildExtDocContent(definitionInfo)
			buildLineCommentContent(element)
			val sections = mutableMapOf<String, String>()
			addRelatedImageSections(imageTargetMap, sections)
			addRelatedLocalisationSections(localisationTargetMap, sections)
			buildSections(sections)
		}
	}
	
	private fun generateComplexEnumValueDoc(element: ParadoxScriptStringExpressionElement, complexEnumValueInfo: ParadoxComplexEnumValueInfo): String {
		return buildString {
			buildComplexEnumValueDefinition(element, complexEnumValueInfo)
		}
	}
	
	private fun getComplexEnumValueDoc(element: ParadoxScriptStringExpressionElement, config: CwtDataConfig<*>): String {
		return buildString {
			val configGroup = config.info.configGroup
			buildComplexEnumValueDefinition(element.value, config.expression.value.orEmpty(), configGroup)
		}
	}
	
	private fun StringBuilder.buildScriptedVariableDefinition(element: ParadoxScriptScriptedVariable, name: String) {
		definition {
			//加上文件信息
			appendFileInfoHeader(element.fileInfo)
			//加上定义信息
			append(PlsDocBundle.message("prefix.scriptedVariable")).append(" <b>@").append(name.escapeXml().orAnonymous()).append("</b>")
			element.unquotedValue?.let { unquotedValue -> append(" = ").append(unquotedValue.escapeXml()) }
		}
	}
	
	private fun StringBuilder.buildPropertyDefinition(element: ParadoxScriptProperty, name: String) {
		definition {
			//加上文件信息
			appendFileInfoHeader(element.fileInfo)
			//加上定义信息
			append(PlsDocBundle.message("prefix.property")).append(" <b>").append(name.escapeXml().orAnonymous()).append("</b>")
			element.value?.let { value -> append(" = ").append(value.escapeXml()) }
		}
	}
	
	private fun StringBuilder.buildDefinitionDefinition(
		element: ParadoxScriptProperty,
		definitionInfo: ParadoxDefinitionInfo,
		localisationTargetMap: MutableMap<String, ParadoxLocalisationProperty>? = null,
		imageTargetMap: MutableMap<String, Tuple2<PsiFile, Int>>? = null
	) {
		definition {
			//加上文件信息
			appendFileInfoHeader(element.fileInfo)
			//加上定义信息
			val name = definitionInfo.name
			val typeLinkText = buildString {
				val gameType = definitionInfo.gameType
				val typeConfig = definitionInfo.typeConfig
				val typeLink = "${gameType.id}/types/${typeConfig.name}"
				appendCwtLink(typeConfig.name, typeLink)
				val subtypeConfigs = definitionInfo.subtypeConfigs
				if(subtypeConfigs.isNotEmpty()) {
					for(subtypeConfig in subtypeConfigs) {
						append(", ")
						val subtypeLink = "$typeLink/${subtypeConfig.name}"
						appendCwtLink(subtypeConfig.name, subtypeLink)
					}
				}
			}
			append(PlsDocBundle.message("prefix.definition")).append(" <b>").append(name.escapeXml().orAnonymous()).append("</b>: ").append(typeLinkText)
			
			//加上相关本地化信息：去重后的一组本地化的键名，不包括可选且没有对应的本地化的项，按解析顺序排序
			val localisationInfos = definitionInfo.localisation
			if(localisationInfos.isNotEmpty()) {
				val project = element.project
				val localisationKeys = mutableSetOf<String>()
				val usedLocalisationTargetMap = localisationTargetMap ?: mutableMapOf()
				for((key, locationExpression, required) in localisationInfos) {
					if(!usedLocalisationTargetMap.containsKey(key)) {
						val selector = localisationSelector().gameTypeFrom(element).preferRootFrom(element).preferLocale(preferredParadoxLocale())
						val (targetKey, target) = locationExpression.resolve(element, definitionInfo, project, selector = selector) ?: continue //发生意外，直接跳过
						if(target != null) usedLocalisationTargetMap.put(key, target)
						if(required || target != null) {
							if(localisationKeys.add(key)) {
								appendBr()
								append(PlsDocBundle.message("prefix.relatedLocalisation")).append(" ")
								append(key).append(" = ").appendLocalisationLink(definitionInfo.gameType, targetKey, element, resolved = true)
							}
						}
					}
				}
			}
			//加上相关图片信息：去重后的一组DDS文件的filePath，或者sprite的definitionKey，不包括可选且没有对应的图片的项，按解析顺序排序
			val imagesInfos = definitionInfo.images
			if(imagesInfos.isNotEmpty()) {
				val project = element.project
				val imageKeys = mutableSetOf<String>()
				val usedImageTargetMap = imageTargetMap ?: mutableMapOf()
				for((key, locationExpression, required) in imagesInfos) {
					if(!usedImageTargetMap.containsKey(key)) {
						val (filePath, target, frame) = locationExpression.resolve(element, definitionInfo, project) ?: continue //发生意外，直接跳过
						if(target != null) usedImageTargetMap.put(key, tupleOf(target, frame))
						if(required || target != null) {
							if(imageKeys.add(key)) {
								appendBr()
								append(PlsDocBundle.message("prefix.relatedImage")).append(" ")
								append(key).append(" = ").appendFilePathLink(definitionInfo.gameType, filePath, element, resolved = true)
							}
						}
					}
				}
			}
			
			//加上作用域上下文信息（如果支持）
			addScopeContextInDefinition(element, definitionInfo)
			
			//加上参数信息（如果支持且存在）
			addParametersInDefinition(element, definitionInfo)
		}
	}
	
	private fun StringBuilder.addScopeContextInDefinition(element: ParadoxScriptProperty, definitionInfo: ParadoxDefinitionInfo) {
		val scopeContext = ScopeConfigHandler.getScopeContext(element)
		if(scopeContext == null) return
		if(!ScopeConfigHandler.hasScopeContext(element, scopeContext)) return
		val gameType = definitionInfo.gameType
		appendBr()
		append(PlsDocBundle.message("prefix.scopeContext"))
		var appendSeparator = false
		scopeContext.map.forEach { (key, value) ->
			if(appendSeparator) append(" ") else appendSeparator = true
			val link = "${gameType.id}/scopes/${value}"
			append(key).append(" = ").appendCwtLink(value, link, element)
		}
	}
	
	private fun StringBuilder.addParametersInDefinition(element: ParadoxScriptProperty, definitionInfo: ParadoxDefinitionInfo) {
		if(!getSettings().documentation.showParameters) return
		if(!definitionInfo.configGroup.definitionTypesSupportParameters.contains(definitionInfo.type)) return
		val parameterMap = element.parameterMap
		if(parameterMap.isEmpty()) return //ignore
		appendBr()
		append(PlsDocBundle.message("prefix.parameters")).append(" ")
		val wrapSize = 6
		for((index, key) in parameterMap.keys.withIndex()) {
			if(index != 0) {
				if(index % wrapSize == 0) appendBr().append("    ") else append(" ")
			}
			append(key)
		}
	}
	
	private fun StringBuilder.buildExtDocContent(definitionInfo: ParadoxDefinitionInfo) {
		//加上从PlsExtDocBundle中得到的文档文本
		val docText = PlsExtDocBundle.message(definitionInfo.name, definitionInfo.type, definitionInfo.gameType)
		if(docText != null && docText.isNotEmpty()) {
			content {
				append(docText)
			}
		}
	}
	
	private fun addRelatedLocalisationSections(map: Map<String, ParadoxLocalisationProperty>, sections: MutableMap<String, String>) {
		//加上渲染后的相关本地化文本
		if(!getSettings().documentation.renderRelatedLocalisationsForDefinitions) return
		if(map.isNotEmpty()) {
			for((key, target) in map) {
				val richText = ParadoxLocalisationTextRenderer.render(target)
				sections.put(key.toCapitalizedWords(), richText)
			}
		}
	}
	
	private fun addRelatedImageSections(map: MutableMap<String, Tuple2<PsiFile, Int>>, sections: MutableMap<String, String>) {
		//加上DDS图片预览图
		if(!getSettings().documentation.renderRelatedImagesForDefinitions) return
		if(map.isNotEmpty()) {
			for((key, tuple) in map) {
				val (target, frame) = tuple
				val url = ParadoxDdsUrlResolver.resolveByFile(target.virtualFile, frame)
				val tag = buildString { appendImgTag(url) }
				sections.put(key.toCapitalizedWords(), tag)
			}
		}
	}
	
	private fun StringBuilder.buildComplexEnumValueDefinition(element: PsiElement, complexEnumValueInfo: ParadoxComplexEnumValueInfo) {
		definition {
			//加上文件信息
			appendFileInfoHeader(element.fileInfo)
			//加上复杂枚举值信息
			val name = complexEnumValueInfo.name
			val enumName = complexEnumValueInfo.enumName
			append(PlsDocBundle.message("prefix.complexEnumValue")).append(" <b>").append(name.escapeXml().orAnonymous()).append("</b>")
			if(enumName.isNotEmpty()) {
				val gameType = complexEnumValueInfo.gameType.orDefault()
				val configGroup = getCwtConfig(element.project).getValue(gameType)
				val complexEnumConfig = configGroup.complexEnums[enumName]
				if(complexEnumConfig != null) {
					val typeLink = "${gameType.id}/complex_enums/${enumName}"
					append(": ").appendCwtLink(enumName, typeLink)
				} else {
					append(": ").append(enumName)
				}
			}
		}
	}
	
	private fun StringBuilder.buildComplexEnumValueDefinition(name: String, enumName: String, configGroup: CwtConfigGroup) {
		definition {
			//不加上文件信息
			//加上复杂枚举值信息
			append(PlsDocBundle.message("prefix.complexEnumValue")).append(" <b>").append(name.escapeXml().orAnonymous()).append("</b>")
			if(enumName.isNotEmpty()) {
				val gameType = configGroup.gameType
				val complexEnumConfig = configGroup.complexEnums[enumName]
				if(complexEnumConfig != null) {
					val typeLink = "${gameType.id}/complex_enums/${enumName}"
					append(": ").appendCwtLink(enumName, typeLink)
				} else {
					append(": ").append(enumName)
				}
			}
		}
	}
	
	private fun StringBuilder.buildLineCommentContent(element: PsiElement) {
		//加上单行注释文本
		if(!getSettings().documentation.renderLineComment) return
		val docText = getLineCommentDocText(element)
		if(docText != null && docText.isNotEmpty()) {
			content {
				append(docText)
			}
		}
	}
	
	private fun StringBuilder.buildSections(sections: Map<String, String>) {
		if(sections.isEmpty()) return
		sections {
			for((key, value) in sections) {
				section(key, value)
			}
		}
	}
}
