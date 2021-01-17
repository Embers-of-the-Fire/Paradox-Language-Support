package com.windea.plugin.idea.paradox

import com.intellij.openapi.project.*
import com.intellij.openapi.util.*
import com.intellij.openapi.vfs.*
import com.intellij.psi.*
import com.intellij.psi.search.*
import com.intellij.psi.util.*
import com.windea.plugin.idea.paradox.core.settings.*
import com.windea.plugin.idea.paradox.localisation.psi.*
import com.windea.plugin.idea.paradox.script.psi.*
import com.windea.plugin.idea.paradox.script.psi.ParadoxScriptTypes.*
import com.windea.plugin.idea.paradox.util.*
import org.jetbrains.annotations.*

//Extensions

fun StringBuilder.appendPsiLink(prefix: String, target: String): StringBuilder {
	return append("<a href='psi_element://").append(prefix).append(target).append("'>").append(target).append("</a>")
}

fun StringBuilder.appendIconTag(url: String, size: Int = iconSize): StringBuilder {
	return append("<img src='").append(url).append("' width='").append(size).append("' height='").append(size).append("'/>")
}

/**得到指定元素之前的所有直接的注释的文本，作为文档注释，跳过空白。*/
fun getDocTextFromPreviousComment(element: PsiElement): String {
	//我们认为当前元素之前，之间没有空行的非行尾行注释，可以视为文档注释，但这并非文档注释的全部
	val lines = mutableListOf<String>()
	var prevElement = element.prevSibling ?: element.parent?.prevSibling
	while(prevElement != null) {
		val text = prevElement.text
		if(prevElement !is PsiWhiteSpace) {
			if(!isPreviousComment(prevElement)) break
			lines.add(0, text.trimStart('#').trim().escapeXml())
		} else {
			if(text.containsBlankLine()) break
		}
		// 兼容comment在rootBlock之外的特殊情况
		prevElement = prevElement.prevSibling
	}
	return lines.joinToString("<br>")
}

/**判断指定的注释是否可认为是之前的注释。*/
fun isPreviousComment(element: PsiElement): Boolean {
	val elementType = element.elementType
	return elementType == ParadoxLocalisationTypes.COMMENT || elementType == COMMENT
}

//Keys

val paradoxFileInfoKey = Key<ParadoxFileInfo>("paradoxFileInfo")
val paradoxPathKey = Key<ParadoxPath>("paradoxPath")
val paradoxDefinitionInfoKey = Key<ParadoxDefinitionInfo>("paradoxDefinitionInfo")
val cachedParadoxFileInfoKey = Key<CachedValue<ParadoxFileInfo>>("cachedParadoxFileInfo")
val cachedParadoxPathKey = Key<CachedValue<ParadoxPath>>("cachedParadoxPath")
val cachedParadoxScriptPathKey = Key<CachedValue<ParadoxPath>>("cachedParadoxScriptPath")
val cachedParadoxDefinitionInfoKey = Key<CachedValue<ParadoxDefinitionInfo>>("cachedParadoxDefinitionInfo")

//Extension Properties

val ParadoxLocalisationLocale.paradoxLocale: ParadoxLocale?
	get() {
		val name = this.name
		return paradoxLocaleMap[name]
	}

val ParadoxLocalisationPropertyReference.paradoxColor: ParadoxColor?
	get() {
		val colorId = this.propertyReferenceParameter?.text?.firstOrNull()
		if(colorId != null && colorId.isUpperCase()) {
			return paradoxColorMap[colorId.toString()]
		}
		return null
	}

val ParadoxLocalisationSequentialNumber.paradoxSequentialNumber: ParadoxSequentialNumber?
	get() {
		val name = this.name
		return paradoxSequentialNumberMap[name]
	}

val ParadoxLocalisationCommandScope.paradoxCommandScope: ParadoxCommandScope?
	get() {
		val name = this.name.toCapitalizedWord() //忽略大小写，首字母大写
		if(name.startsWith(eventTargetPrefix)) return null
		return paradoxCommandScopeMap[name]
	}

val ParadoxLocalisationCommandField.paradoxCommandField: ParadoxCommandField?
	get() {
		val name = this.name
		return paradoxCommandFieldMap[name]
	}

val ParadoxLocalisationColorfulText.paradoxColor: ParadoxColor?
	get() {
		val name = this.name
		return paradoxColorMap[name]
	}


val PsiElement.paradoxLocale: ParadoxLocale? get() = getLocale(this)

private fun getLocale(element: PsiElement): ParadoxLocale? {
	return when(val file = element.containingFile) {
		is ParadoxScriptFile -> inferredParadoxLocale
		is ParadoxLocalisationFile -> file.locale?.paradoxLocale
		else -> null
	}
}


val PsiElement.paradoxScriptPath: ParadoxPath? get() = getScriptPath(this)

private fun getScriptPath(element: PsiElement): ParadoxPath? {
	if(!canGetScriptPath(element)) return null
	return CachedValuesManager.getCachedValue(element, cachedParadoxScriptPathKey) {
		CachedValueProvider.Result.create(resolveScriptPath(element), element)
	}
}

internal fun canGetScriptPath(element: PsiElement): Boolean {
	return element is ParadoxScriptProperty || element is ParadoxScriptValue
}

private fun resolveScriptPath(element: PsiElement): ParadoxPath? {
	return when {
		element is ParadoxScriptProperty || element is ParadoxScriptValue -> {
			val subPaths = mutableListOf<String>()
			var current = element
			while(current !is PsiFile) {
				when {
					current is ParadoxScriptProperty -> {
						subPaths.add(0, current.name)
					}
					current is ParadoxScriptValue -> {
						val parent = current.parent ?: break
						if(parent is ParadoxScriptBlock) {
							subPaths.add(0, parent.indexOfChild(current).toString())
						}
						current = parent
					}
				}
				current = current.parent ?: break
			}
			ParadoxPath(subPaths)
		}
		else -> null
	}
}


val VirtualFile.paradoxFileInfo: ParadoxFileInfo? get() = this.getUserData(paradoxFileInfoKey)

val PsiFile.paradoxFileInfo: ParadoxFileInfo? get() = getFileInfo(this.originalFile) //使用原始文件

val PsiElement.paradoxFileInfo: ParadoxFileInfo? get() = getFileInfo(this.containingFile)

internal fun canGetFileInfo(file: PsiFile): Boolean {
	return file is ParadoxScriptFile || file is ParadoxLocalisationFile
}

private fun getFileInfo(file: PsiFile): ParadoxFileInfo? {
	if(!canGetFileInfo(file)) return null
	//尝试基于fileViewProvider得到fileInfo
	val quickFileInfo = file.getUserData(paradoxFileInfoKey)
	if(quickFileInfo != null) return quickFileInfo
	return CachedValuesManager.getCachedValue(file, cachedParadoxFileInfoKey) {
		val value = file.virtualFile?.getUserData(paradoxFileInfoKey) ?: resolveFileInfo(file)
		CachedValueProvider.Result.create(value, file)
	}
}

private fun resolveFileInfo(file: PsiFile): ParadoxFileInfo? {
	val fileType = getFileType(file) ?: return null
	val fileName = file.name
	val subPaths = mutableListOf(fileName)
	var currentFile = file.parent
	while(currentFile != null) {
		val rootType = getRootType(currentFile)
		if(rootType != null) {
			val path = getPath(subPaths)
			val gameType = getGameType()
			return ParadoxFileInfo(fileName, path, fileType, rootType, gameType)
		}
		subPaths.add(0, currentFile.name)
		currentFile = currentFile.parent
	}
	return null
}

private fun getPath(subPaths: List<String>): ParadoxPath {
	return ParadoxPath(subPaths)
}

private fun getFileType(file: PsiFile): ParadoxFileType? {
	val fileName = file.name.toLowerCase()
	val fileExtension = fileName.substringAfterLast('.')
	return when {
		fileExtension in scriptFileExtensions -> ParadoxFileType.Script
		fileExtension in localisationFileExtensions -> ParadoxFileType.Localisation
		else -> null
	}
}

private fun getRootType(file: PsiDirectory): ParadoxRootType? {
	if(!file.isDirectory) return null
	val fileName = file.name
	for(child in file.files) {
		val childName = child.name
		when {
			exeFileNames.any { exeFileName -> childName.equals(exeFileName, true) } -> return ParadoxRootType.Stdlib
			childName.equals(descriptorFileName, true) -> return ParadoxRootType.Mod
			fileName == ParadoxRootType.PdxLauncher.key -> return ParadoxRootType.PdxLauncher
			fileName == ParadoxRootType.PdxOnlineAssets.key -> return ParadoxRootType.PdxOnlineAssets
			fileName == ParadoxRootType.TweakerGuiAssets.key -> return ParadoxRootType.TweakerGuiAssets
		}
	}
	return null
}

private fun getGameType(): ParadoxGameType {
	return ParadoxGameType.Stellaris //TODO
}


val ParadoxScriptProperty.paradoxDefinitionInfo: ParadoxDefinitionInfo? get() = getDefinitionInfo(this)

val ParadoxScriptProperty.paradoxDefinitionInfoNoCheck: ParadoxDefinitionInfo? get() = getDefinitionInfo(this, false)

internal fun canGetDefinitionInfo(element: ParadoxScriptProperty): Boolean {
	//最低到2级scriptProperty
	val parent = element.parent
	return parent is ParadoxScriptRootBlock || parent?.parent?.parent?.parent is ParadoxScriptRootBlock
}

private fun getDefinitionInfo(element: ParadoxScriptProperty, check: Boolean = true): ParadoxDefinitionInfo? {
	if(check && !canGetDefinitionInfo(element)) return null
	return CachedValuesManager.getCachedValue(element, cachedParadoxDefinitionInfoKey) {
		CachedValueProvider.Result.create(resolveDefinitionInfo(element), element)
	}
}

private fun resolveDefinitionInfo(element: ParadoxScriptProperty): ParadoxDefinitionInfo? {
	val (_, path, _, _, gameType) = element.paradoxFileInfo ?: return null
	val ruleGroup = paradoxRuleGroups[gameType.key] ?: return null
	val elementName = element.name
	val scriptPath = element.paradoxScriptPath ?: return null
	val definition = ruleGroup.types.values.find { it.matches(element, elementName, path, scriptPath) } ?: return null
	return definition.toDefinitionInfo(element, elementName)
}


fun ParadoxScriptValue.getType(): String?{
	return when(this){
		is ParadoxScriptBlock -> when {
			this.isEmpty -> "array | object"
			this.isArray -> "array"
			this.isObject -> "object"
			else -> null
		}
		is ParadoxScriptString -> "string"
		is ParadoxScriptBoolean -> "boolean"
		is ParadoxScriptNumber -> "number"
		is ParadoxScriptColor -> "color"
		is ParadoxScriptCode -> "code"
		else -> null
	}
}

fun ParadoxScriptValue.checkType(type: String): Boolean {
	return when(type) {
		"block" -> this is ParadoxScriptBlock
		"object" -> this is ParadoxScriptBlock && isObject
		"array" -> this is ParadoxScriptBlock && isArray
		"string" -> this is ParadoxScriptString
		"boolean" -> this is ParadoxScriptBoolean
		"number" -> this is ParadoxScriptNumber
		"color" -> this is ParadoxScriptColor
		"code" -> this is ParadoxScriptCode
		else -> false
	}
}

fun ParadoxScriptValue.isNullLike():Boolean{
	return when{
		this is ParadoxScriptBlock -> this.isEmpty || this.isAlwaysYes() //兼容always=yes
		this is ParadoxScriptString -> this.textMatches("")
		this is ParadoxScriptNumber -> this.text.toIntOrNull() == 0 //兼容0.0和0.00这样的情况
		this is ParadoxScriptBoolean -> this.textMatches("no")
		else -> false
	}
}

fun ParadoxScriptBlock.isAlwaysYes():Boolean{
	return this.isObject && this.propertyList.singleOrNull()?.let { it.name == "always" && it.value == "yes" }?:false
}
//Find Extensions

//使用stubIndex以提高性能
private	val state = ParadoxSettingsState.getInstance()


fun findScriptVariableInFile(name: String, file: PsiFile): ParadoxScriptVariable? {
	//在所在文件中递归查找（不一定定义在顶层）
	if(file !is ParadoxScriptFile) return null
	return file.findDescendantOfType { it.name == name }
}

fun findScriptVariablesInFile(name: String, file: PsiFile): List<ParadoxScriptVariable> {
	//在所在文件中递归查找（不一定定义在顶层），仅查找第一个
	return findScriptVariableInFile(name, file).toSingletonListOrEmpty()
}

fun findScriptVariablesInFile(file: PsiFile): List<ParadoxScriptVariable> {
	//在所在文件中递归查找（不一定定义在顶层）
	if(file !is ParadoxScriptFile) return emptyList()
	return file.collectDescendantsOfType()
}

fun findScriptVariable(name: String, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project)): ParadoxScriptVariable? {
	return ParadoxScriptVariableNameIndex.getOne(name, project, scope, !state.preferOverridden)
}

fun findScriptVariables(name: String, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project)): List<ParadoxScriptVariable> {
	return ParadoxScriptVariableNameIndex.getAll(name, project, scope)
}

fun findScriptVariables(project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project)): List<ParadoxScriptVariable> {
	return ParadoxScriptVariableNameIndex.getAll(project, scope)
}


fun findDefinition(name: String, type: String? = null, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project)): ParadoxScriptProperty? {
	return ParadoxDefinitionNameIndex.getOne(name, type, project, scope, !state.preferOverridden)
}

fun findDefinitions(name: String, type: String? = null, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project)): List<ParadoxScriptProperty> {
	return ParadoxDefinitionNameIndex.getAll(name, type, project, scope)
}

fun findDefinitions(type: String? = null, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project)): List<ParadoxScriptProperty> {
	return ParadoxDefinitionNameIndex.getAll(type, project, scope)
}


fun findScriptLocalisation(name: String, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project)): ParadoxScriptProperty? {
	return ParadoxScriptLocalisationNameIndex.getOne(name, project, scope, !state.preferOverridden)
}

fun findScriptLocalisations(name: String, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project)): List<ParadoxScriptProperty> {
	return ParadoxScriptLocalisationNameIndex.getAll(name, project, scope)
}

fun findScriptLocalisations(project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project)): List<ParadoxScriptProperty> {
	return ParadoxScriptLocalisationNameIndex.getAll(project, scope)
}


fun findLocalisation(name: String, locale: ParadoxLocale?, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project), hasDefault: Boolean = false): ParadoxLocalisationProperty? {
	return ParadoxLocalisationNameIndex.getOne(name, locale, project, scope, hasDefault, !state.preferOverridden)
}

fun findLocalisations(name: String, locale: ParadoxLocale? = null, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project), hasDefault: Boolean = true): List<ParadoxLocalisationProperty> {
	return ParadoxLocalisationNameIndex.getAll(name, locale, project, scope, hasDefault)
}

fun findLocalisations(locale: ParadoxLocale? = null, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project),hasDefault: Boolean = false): List<ParadoxLocalisationProperty> {
	return ParadoxLocalisationNameIndex.getAll(locale, project, scope,hasDefault)
}

fun findLocalisations(names: Iterable<String>, locale: ParadoxLocale? = null, project: Project, scope: GlobalSearchScope = GlobalSearchScope.allScope(project), hasDefault:Boolean= false,keepOrder: Boolean = false): List<ParadoxLocalisationProperty> {
	return ParadoxLocalisationNameIndex.getAll(names, locale, project, scope, hasDefault,keepOrder)
}

//Util Extensions

fun message(@PropertyKey(resourceBundle = paradoxBundleName) key: String, vararg params: Any): String {
	return ParadoxBundle.getMessage(key, *params)
}

fun String.resolveIconUrl(defaultToUnknown: Boolean = true): String {
	return ParadoxIconUrlResolver.resolve(this, defaultToUnknown)
}

fun ParadoxLocalisationPropertyValue.renderRichText(): String {
	return ParadoxRichTextRenderer.render(this)
}

fun ParadoxLocalisationPropertyValue.renderRichTextTo(buffer: StringBuilder) {
	ParadoxRichTextRenderer.renderTo(this, buffer)
}
