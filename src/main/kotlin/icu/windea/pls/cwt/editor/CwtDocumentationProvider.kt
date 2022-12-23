@file:Suppress("UNUSED_PARAMETER")

package icu.windea.pls.cwt.editor

import com.intellij.lang.documentation.*
import com.intellij.openapi.progress.*
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.*
import com.intellij.psi.util.*
import com.intellij.util.*
import icu.windea.pls.*
import icu.windea.pls.config.cwt.*
import icu.windea.pls.config.cwt.config.*
import icu.windea.pls.config.script.*
import icu.windea.pls.core.*
import icu.windea.pls.core.handler.*
import icu.windea.pls.core.handler.ParadoxCwtConfigHandler.resolveConfigs
import icu.windea.pls.core.index.*
import icu.windea.pls.core.model.*
import icu.windea.pls.core.psi.*
import icu.windea.pls.core.selector.*
import icu.windea.pls.core.selector.chained.*
import icu.windea.pls.core.tool.*
import icu.windea.pls.cwt.*
import icu.windea.pls.cwt.psi.*
import icu.windea.pls.script.*
import icu.windea.pls.script.psi.*
import java.util.*

class CwtDocumentationProvider : AbstractDocumentationProvider() {
	override fun getDocumentationElementForLookupItem(psiManager: PsiManager?, `object`: Any?, element: PsiElement?): PsiElement? {
		if(`object` is PsiElement) return `object`
		return super.getDocumentationElementForLookupItem(psiManager, `object`, element)
	}
	
	override fun getDocumentationElementForLink(psiManager: PsiManager?, link: String?, context: PsiElement?): PsiElement? {
		if(link == null || context == null) return null
		return resolveLink(link, context)
	}
	
	override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
		return when(element) {
			is CwtProperty -> getPropertyInfo(element, originalElement)
			is CwtString -> getStringInfo(element, originalElement)
			else -> null
		}
	}
	
	private fun getPropertyInfo(element: CwtProperty, originalElement: PsiElement?): String {
		return buildString {
			val name = element.name
			val configType = CwtConfigType.resolve(element)
			val project = element.project
			val gameType = selectGameType(originalElement?.takeIf { it.language == ParadoxScriptLanguage })
			val configGroup = gameType?.let { getCwtConfig(project).getValue(it) }
			buildPropertyDefinition(element, originalElement, name, configType, configGroup, false, null)
		}
	}
	
	private fun getStringInfo(element: CwtString, originalElement: PsiElement?): String? {
		//only for property value or block value
		if(!element.isPropertyValue() && !element.isBlockValue()) return null
		
		return buildString {
			val name = element.name
			val configType = CwtConfigType.resolve(element)
			val project = element.project
			val gameType = selectGameType(originalElement?.takeIf { it.language == ParadoxScriptLanguage })
			val configGroup = gameType?.let { getCwtConfig(project).getValue(it) }
			buildStringDefinition(element, originalElement, name, configType, configGroup, false, null)
		}
	}
	
	override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
		return when(element) {
			is CwtProperty -> getPropertyDoc(element, originalElement)
			is CwtString -> getStringDoc(element, originalElement)
			else -> null
		}
	}
	
	private fun getPropertyDoc(element: CwtProperty, originalElement: PsiElement?): String {
		return buildString {
			val name = element.name
			val configType = CwtConfigType.resolve(element)
			val project = element.project
			val gameType = selectGameType(originalElement?.takeIf { it.language == ParadoxScriptLanguage })
			val configGroup = gameType?.let { getCwtConfig(project).getValue(it) }
			val sections = mutableMapOf<String, String>()
			buildPropertyDefinition(element, originalElement, name, configType, configGroup, true, sections)
			buildDocumentationContent(element)
			buildScopeContent(element, originalElement, name, configType, configGroup)
			buildSupportedScopesContent(element, originalElement, name, configType, configGroup)
			buildSections(sections)
		}
	}
	
	private fun getStringDoc(element: CwtString, originalElement: PsiElement?): String? {
		//only for property value or block value
		if(!element.isPropertyValue() && !element.isBlockValue()) return null
		
		return buildString {
			val name = element.name
			val configType = CwtConfigType.resolve(element)
			val project = element.project
			val gameType = selectGameType(originalElement?.takeIf { it.language == ParadoxScriptLanguage })
			val configGroup = gameType?.let { getCwtConfig(project).getValue(it) }
			val sections = mutableMapOf<String, String>()
			buildStringDefinition(element, originalElement, name, configType, configGroup, true, sections)
			buildDocumentationContent(element)
			buildSections(sections)
		}
	}
	
	//返回实际使用的相关本地化
	private fun StringBuilder.buildPropertyDefinition(
		element: CwtProperty,
		originalElement: PsiElement?,
		name: String,
		configType: CwtConfigType?,
		configGroup: CwtConfigGroup?,
		showDetail: Boolean,
		sections: MutableMap<String, String>?
	) {
		definition {
			val referenceElement = getReferenceElement(originalElement)
			if(referenceElement?.language != ParadoxScriptLanguage || configType?.isReference == true) {
				if(configType != null) append(configType.nameText).append(" ")
				append("<b>").append(name.escapeXml().orAnonymous()).append("</b>")
				//加上类型信息
				val typeCategory = configType?.category
				if(typeCategory != null) {
					val typeElement = element.parentOfType<CwtProperty>()
					val typeName = typeElement?.name?.substringIn('[', ']')?.takeIfNotEmpty()
					if(typeName != null && typeName.isNotEmpty()) {
						//在脚本文件中显示为链接
						if(configGroup != null) {
							val gameType = configGroup.gameType
							val typeLink = "${gameType.id}/${typeCategory}/${typeName}"
							append(": ").appendCwtLink(typeName, typeLink, typeElement)
						} else {
							append(": ").append(typeName)
						}
					}
				}
			} else {
				val prefix = when {
					referenceElement is ParadoxScriptPropertyKey -> PlsDocBundle.message("prefix.definitionProperty")
					referenceElement is ParadoxScriptValue -> PlsDocBundle.message("prefix.definitionValue")
					else -> null
				}
				val referenceName = referenceElement.text.unquote()
				if(prefix != null) append(prefix).append(" ")
				append("<b>").append(referenceName.escapeXml().orAnonymous()).append("</b>")
				if(showDetail && name != referenceName) { //这里不忽略大小写
					grayed {
						append(" by ").append(name.escapeXml().orAnonymous())
					}
				}
			}
			if(referenceElement != null && configGroup != null) {
				addScopeContext(element, referenceElement, configGroup, sections)
				if(configType == CwtConfigType.Modifier) {
					addModifierRelatedLocalisations(element, referenceElement, name, configGroup, sections)
					addModifierIcon(element, referenceElement, name, configGroup, sections)
				}
			}
		}
	}
	
	private fun StringBuilder.buildStringDefinition(
		element: CwtString,
		originalElement: PsiElement?,
		name: String,
		configType: CwtConfigType?,
		configGroup: CwtConfigGroup?,
		showDetail: Boolean,
		sections: MutableMap<String, String>?
	) {
		definition {
			val referenceElement = getReferenceElement(originalElement)
			if(referenceElement?.language != ParadoxScriptLanguage || configType?.isReference == true) {
				if(configType != null) append(configType.nameText).append(" ")
				append("<b>").append(name.escapeXml().orAnonymous()).append("</b>")
				//加上类型信息
				val typeCategory = configType?.category
				if(typeCategory != null) {
					val typeElement = element.parentOfType<CwtProperty>()
					val typeName = typeElement?.name?.substringIn('[', ']')?.takeIfNotEmpty()
					if(typeName != null && typeName.isNotEmpty()) {
						//在脚本文件中显示为链接
						if(configGroup != null) {
							val gameType = configGroup.gameType
							val typeLink = "${gameType.id}/${typeCategory}/${typeName}"
							append(": ").appendCwtLink(typeName, typeLink, typeElement)
						} else {
							append(": ").append(typeName)
						}
					}
				}
			} else {
				val prefix = when {
					referenceElement is ParadoxScriptPropertyKey -> PlsDocBundle.message("prefix.definitionProperty")
					referenceElement is ParadoxScriptValue -> PlsDocBundle.message("prefix.definitionValue")
					else -> null
				}
				val referenceName = referenceElement.text.unquote()
				if(prefix != null) append(prefix).append(" ")
				append("<b>").append(referenceName.escapeXml().orAnonymous()).append("</b>")
				if(showDetail && name != referenceName) { //这里不忽略大小写
					grayed {
						append(" by ").append(name.escapeXml().orAnonymous())
					}
				}
			}
			if(referenceElement != null && configGroup != null) {
				addScopeContext(element, referenceElement, configGroup, sections)
				if(configType == CwtConfigType.Modifier) {
					addModifierRelatedLocalisations(element, referenceElement, name, configGroup, sections)
					addModifierIcon(element, referenceElement, name, configGroup, sections)
				}
			}
		}
	}
	
	private fun StringBuilder.addScopeContext(element: PsiElement, referenceElement: PsiElement, configGroup: CwtConfigGroup, sections: MutableMap<String, String>?) {
		ProgressManager.checkCanceled()
		val memberElement = referenceElement.parentOfType<ParadoxScriptMemberElement>(true) ?: return
		if(!ScopeConfigHandler.isScopeContextSupported(memberElement)) return
		val scopeContext = ScopeConfigHandler.getScopeContext(memberElement)
		if(scopeContext == null) return
		val gameType = configGroup.gameType
		appendBr()
		append(PlsDocBundle.message("prefix.scopeContext"))
		scopeContext.map.forEach { (key, value) ->
			append(" ")
			val scopeId = ScopeConfigHandler.getScopeId(value)
			append(key).append(" = ")
			if(ScopeConfigHandler.isFakeScopeId(scopeId)) {
				append(scopeId)
			} else {
				appendCwtLink(scopeId, "${gameType.id}/scopes/$scopeId", memberElement)
			}
		}
	}
	
	private fun StringBuilder.addModifierRelatedLocalisations(element: PsiElement, referenceElement: PsiElement, name: String, configGroup: CwtConfigGroup, sections: MutableMap<String, String>?) {
		val render = getSettings().documentation.renderRelatedLocalisationsForModifiers
		ProgressManager.checkCanceled()
		val contextElement = referenceElement
		val gameType = configGroup.gameType ?: return
		val nameKeys = ModifierConfigHandler.getModifierNameKeys(name, configGroup)
		val localisation = nameKeys.firstNotNullOfOrNull {
			val selector = localisationSelector().gameType(gameType).preferRootFrom(contextElement).preferLocale(preferredParadoxLocale())
			findLocalisation(it, configGroup.project, selector = selector)
		}
		val descKeys = ModifierConfigHandler.getModifierDescKeys(name, configGroup)
		val descLocalisation = descKeys.firstNotNullOfOrNull {
			val descSelector = localisationSelector().gameType(gameType).preferRootFrom(contextElement).preferLocale(preferredParadoxLocale())
			findLocalisation(it, configGroup.project, selector = descSelector)
		}
		//如果没找到的话，不要在文档中显示相关信息
		if(localisation != null) {
			appendBr()
			append(PlsDocBundle.message("prefix.relatedLocalisation")).append(" ")
			append("Name = ").appendLocalisationLink(gameType, localisation.name, contextElement, resolved = true)
		}
		if(descLocalisation != null) {
			appendBr()
			append(PlsDocBundle.message("prefix.relatedLocalisation")).append(" ")
			append("Desc = ").appendLocalisationLink(gameType, descLocalisation.name, contextElement, resolved = true)
		}
		if(sections != null && render) {
			if(localisation != null) {
				val richText = ParadoxLocalisationTextRenderer.render(localisation)
				sections.put("Name", richText)
			}
			if(descLocalisation != null) {
				val richText = ParadoxLocalisationTextRenderer.render(descLocalisation)
				sections.put("Desc", richText)
			}
		}
	}
	
	private fun StringBuilder.addModifierIcon(element: PsiElement, referenceElement: PsiElement, name: String, configGroup: CwtConfigGroup, sections: MutableMap<String, String>?) {
		val render = getSettings().documentation.renderIconForModifiers
		ProgressManager.checkCanceled()
		val contextElement = referenceElement
		val gameType = configGroup.gameType ?: return
		val iconPaths = ModifierConfigHandler.getModifierIconPaths(name, configGroup)
		val (iconPath , iconFile) = iconPaths.firstNotNullOfOrNull {
			val iconSelector = fileSelector().gameType(gameType).preferRootFrom(contextElement)
			it to findFileByFilePath(it, configGroup.project, selector = iconSelector)
		} ?: (null to null)
		//如果没找到的话，不要在文档中显示相关信息
		if(iconPath != null && iconFile != null) {
			appendBr()
			append(PlsDocBundle.message("prefix.relatedImage")).append(" ")
			append("Icon = ").appendFilePathLink(gameType, iconPath, contextElement, resolved = true)
		}
		if(sections != null && render) {
			if(iconFile != null) {
				val url = ParadoxDdsUrlResolver.resolveByFile(iconFile)
				sections.put("Icon", buildString { appendImgTag(url) })
			}
		}
	}
	
	private fun StringBuilder.buildDocumentationContent(element: PsiElement) {
		//渲染文档注释（可能需要作为HTML）
		var current: PsiElement = element
		var lines: LinkedList<String>? = null
		var html = false
		while(true) {
			current = current.prevSibling ?: break
			when {
				current is CwtDocumentationComment -> {
					val documentationText = current.documentationText
					if(documentationText != null) {
						if(lines == null) lines = LinkedList()
						val docText = documentationText.text.trimStart('#').trim() //这里接受HTML
						lines.addFirst(docText)
					}
				}
				current is CwtOptionComment -> {
					val option = current.option ?: continue
					when {
						option.name == "format" && option.value == "html" -> html = true
					}
				}
				current is PsiWhiteSpace || current is PsiComment -> continue
				else -> break
			}
		}
		val documentation = lines?.joinToString("<br>") { if(html) it else it.escapeXml() }
		if(documentation != null) {
			content {
				append(documentation)
			}
		}
	}
	
	private fun StringBuilder.buildScopeContent(element: CwtProperty, originalElement: PsiElement?, name: String, configType: CwtConfigType?, configGroup: CwtConfigGroup?) {
		if(configGroup == null) return
		if(!getSettings().documentation.showScopes) return
		//为link提示名字、描述、输入作用域、输出作用域的文档注释
		//仅为脚本文件和本地化文件中的引用提供
		when(configType) {
			CwtConfigType.Link -> {
				val linkConfig = configGroup.links[name] ?: return
				val nameToUse = ScopeConfigHandler.getScopeName(name, configGroup)
				val descToUse = linkConfig.desc
				val inputScopeNames = linkConfig.inputScopeNames.joinToString { "<code>$it</code>" }
				val outputScopeName = linkConfig.outputScopeName.let { "<code>$it</code>" }
				content {
					append(nameToUse)
					if(descToUse != null && descToUse.isNotEmpty()) {
						appendBr()
						append(descToUse)
					}
				}
				content {
					append(PlsDocBundle.message("content.inputScopes", inputScopeNames))
					appendBr()
					append(PlsDocBundle.message("content.outputScope", outputScopeName))
				}
			}
			else -> pass()
		}
	}
	
	private fun StringBuilder.buildSupportedScopesContent(element: CwtProperty, originalElement: PsiElement?, name: String, configType: CwtConfigType?, configGroup: CwtConfigGroup?) {
		if(configGroup == null) return
		if(!getSettings().documentation.showScopes) return
		//为alias modifier localisation_command等提供分类、支持的作用域的文档注释
		//仅为脚本文件和本地化文件中的引用提供
		var categoryNames: Set<String>? = null
		var supportedScopeNames: Set<String>? = null
		when(configType) {
			CwtConfigType.Modifier -> {
				val modifierConfig = configGroup.modifiers[name] ?: return
				categoryNames = modifierConfig.categoryConfigMap.keys
				supportedScopeNames = modifierConfig.supportedScopeNames
			}
			CwtConfigType.LocalisationCommand -> {
				val localisationCommandConfig = configGroup.localisationCommands[name] ?: return
				supportedScopeNames = localisationCommandConfig.supportedScopeNames
			}
			CwtConfigType.Alias -> {
				val expressionElement = originalElement?.parent?.castOrNull<ParadoxScriptStringExpressionElement>() ?: return
				val config = resolveConfigs(expressionElement).firstOrNull()?.castOrNull<CwtPropertyConfig>()
				val aliasConfig = config?.inlineableConfig?.castOrNull<CwtAliasConfig>() ?: return
				if(aliasConfig.name !in configGroup.aliasNameSupportScope) return
				supportedScopeNames = aliasConfig.supportedScopeNames
			}
			else -> pass()
		}
		content {
			var appendBr = false
			if(!categoryNames.isNullOrEmpty()) {
				appendBr = true
				val categoryNamesToUse = categoryNames.joinToString(", ") { "<code>$it</code>" }
				append(PlsDocBundle.message("content.categories", categoryNamesToUse))
			}
			if(!supportedScopeNames.isNullOrEmpty()) {
				if(appendBr) appendBr()
				val supportedScopeNamesToUse = supportedScopeNames.joinToString(", ") { "<code>$it</code>" }
				append(PlsDocBundle.message("content.supportedScopes", supportedScopeNamesToUse))
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
	
	private fun getReferenceElement(originalElement: PsiElement?): PsiElement?{
		val element = when{
			originalElement == null -> return null
			originalElement.elementType == TokenType.WHITE_SPACE -> originalElement.prevSibling ?: return null
			else -> originalElement
		}
		return when{
			element is LeafPsiElement -> element.parent
			else -> element
		}
	}
}
