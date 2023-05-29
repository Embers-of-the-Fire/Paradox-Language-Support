package icu.windea.pls.lang

import com.intellij.lang.*
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.*
import com.intellij.psi.util.*
import icu.windea.pls.*
import icu.windea.pls.config.*
import icu.windea.pls.config.config.*
import icu.windea.pls.config.expression.*
import icu.windea.pls.core.*
import icu.windea.pls.core.expression.*
import icu.windea.pls.core.psi.*
import icu.windea.pls.core.search.selector.chained.*
import icu.windea.pls.lang.model.*
import icu.windea.pls.localisation.psi.*
import icu.windea.pls.script.psi.*
import icu.windea.pls.script.psi.ParadoxScriptElementTypes.*
import icu.windea.pls.script.psi.impl.*
import icu.windea.pls.tool.localisation.*

/**
 * 用于处理定义。
 *
 * @see ParadoxScriptDefinitionElement
 * @see ParadoxDefinitionInfo
 */
@Suppress("unused", "UNUSED_PARAMETER")
object ParadoxDefinitionHandler {
    //get info & match methods
    
    fun getInfo(element: ParadoxScriptDefinitionElement): ParadoxDefinitionInfo? {
        //快速判断
        if(runCatching { element.getStub() }.getOrNull()?.isValid() == false) return null
        //如果不能使用缓存，需要重新获取
        val notUseCache = element.getUserData(PlsKeys.isIncompleteKey) == true
        if(notUseCache) {
            val file = element.containingFile
            return doGetInfo(element, file)
        }
        //从缓存中获取
        return doGetInfoFromCache(element)
    }
    
    private fun doGetInfoFromCache(element: ParadoxScriptDefinitionElement): ParadoxDefinitionInfo? {
        return CachedValuesManager.getCachedValue(element, PlsKeys.cachedDefinitionInfoKey) {
            ProgressManager.checkCanceled()
            val file = element.containingFile
            val value = doGetInfo(element, file)
            //invalidated on file modification
            CachedValueProvider.Result.create(value, file)
        }
    }
    
    private fun doGetInfo(element: ParadoxScriptDefinitionElement, file: PsiFile = element.containingFile): ParadoxDefinitionInfo? {
        val rootKey = element.name.let { if(element is ParadoxScriptFile) it.substringBeforeLast('.') else it } //如果是文件名，不要包含扩展名
        if(element is ParadoxScriptProperty && rootKey.isParameterized()) return null //排除可能带参数的情况
        val project = file.project
        
        //首先尝试直接基于stub进行解析
        getInfoFromStub(element, project)?.let { return it }
        
        val fileInfo = file.fileInfo ?: return null
        val path = fileInfo.pathToEntry //这里使用pathToEntry
        val elementPath = ParadoxElementPathHandler.getFromFile(element, PlsConstants.maxDefinitionDepth) ?: return null
        val gameType = fileInfo.rootInfo.gameType //这里还是基于fileInfo获取gameType
        val configGroup = getCwtConfig(project).get(gameType) //这里需要指定project
        for(typeConfig in configGroup.types.values) {
            if(matchesType(element, typeConfig, path, elementPath, rootKey, configGroup)) {
                ProgressManager.checkCanceled()
                return ParadoxDefinitionInfo(null, typeConfig, null, rootKey, elementPath, gameType, configGroup, element)
            }
        }
        ProgressManager.checkCanceled()
        return null
    }
    
    fun isDefinition(element: ParadoxScriptDefinitionElement): Boolean {
        //用于快速判断
        val stub = runCatching { element.getStub() }.getOrNull()
        if(stub != null) return stub.isValid()
        return doGetInfoFromCache(element) != null
    }
    
    fun matchesTypeWithUnknownDeclaration(
        typeConfig: CwtTypeConfig,
        path: ParadoxPath,
        elementPath: ParadoxElementPath?,
        rootKey: String?
    ): Boolean {
        //判断element是否需要是scriptFile还是scriptProperty
        val nameFromFileConfig = typeConfig.nameFromFile
        if(nameFromFileConfig) return false
        
        //判断path是否匹配
        val pathConfig = typeConfig.path ?: return false
        val pathStrictConfig = typeConfig.pathStrict
        if(pathStrictConfig) {
            if(pathConfig != path.parent) return false
        } else {
            if(!pathConfig.matchesPath(path.parent)) return false
        }
        //判断path_name是否匹配
        val pathFileConfig = typeConfig.pathFile //String?
        if(pathFileConfig != null) {
            if(pathFileConfig != path.fileName) return false
        }
        //判断path_extension是否匹配
        val pathExtensionConfig = typeConfig.pathExtension //String?
        if(pathExtensionConfig != null) {
            if(pathExtensionConfig != "." + path.fileExtension) return false
        }
        
        if(elementPath != null) {
            //如果skip_root_key = any，则要判断是否需要跳过rootKey，如果为any，则任何情况都要跳过（忽略大小写）
            //skip_root_key可以为列表（如果是列表，其中的每一个root_key都要依次匹配）
            //skip_root_key可以重复（其中之一匹配即可）
            val skipRootKeyConfig = typeConfig.skipRootKey
            if(skipRootKeyConfig.isNullOrEmpty()) {
                if(elementPath.length > 1) return false
            } else {
                val skipResult = skipRootKeyConfig.any { elementPath.matchEntire(it, useParentPath = true) }
                if(!skipResult) return false
            }
        }
        
        if(rootKey != null) {
            //如果starts_with存在，则要求type_key匹配这个前缀（不忽略大小写）
            val startsWithConfig = typeConfig.startsWith
            if(!startsWithConfig.isNullOrEmpty()) {
                val result = rootKey.startsWith(startsWithConfig, true)
                if(!result) return false
            }
            //如果type_key_regex存在，则要求type_key匹配
            val typeKeyRegexConfig = typeConfig.typeKeyRegex
            if(typeKeyRegexConfig != null) {
                val result = typeKeyRegexConfig.matches(rootKey)
                if(!result) return false
            }
            //如果type_key_filter存在，则通过type_key进行过滤（忽略大小写）
            val typeKeyFilterConfig = typeConfig.typeKeyFilter
            if(typeKeyFilterConfig != null && typeKeyFilterConfig.value.isNotEmpty()) {
                val filterResult = typeKeyFilterConfig.where { it.contains(rootKey) }
                if(!filterResult) return false
            }
            //如果name_field存在，则要求root_key必须是由type_key_filter指定的所有可能的root_key之一，或者没有指定任何root_key
            val nameFieldConfig = typeConfig.nameField
            if(nameFieldConfig != null) {
                val result = typeConfig.possibleRootKeys.isEmpty() || typeConfig.possibleRootKeys.contains(rootKey)
                if(!result) return false
            }
        }
        
        return true
    }
    
    fun matchesType(
        element: ParadoxScriptDefinitionElement,
        typeConfig: CwtTypeConfig,
        path: ParadoxPath,
        elementPath: ParadoxElementPath,
        rootKey: String,
        configGroup: CwtConfigGroup
    ): Boolean {
       return matchesType(element,
           { it?.castOrNull<ParadoxScriptDefinitionElement>()?.elementType },
           { it?.castOrNull<ParadoxScriptProperty>()?.propertyValue },
           typeConfig, path, elementPath, rootKey, configGroup
       )
    }
    
    fun matchesType(
        node: LighterASTNode,
        tree: LighterAST,
        typeConfig: CwtTypeConfig,
        path: ParadoxPath,
        elementPath: ParadoxElementPath,
        rootKey: String,
        configGroup: CwtConfigGroup
    ): Boolean {
        return matchesType(node,
            { it?.castOrNull<LighterASTNode>()?.tokenType },
            { it?.castOrNull<LighterASTNode>()?.firstChild(tree, ParadoxScriptTokenSets.VALUES) },
            typeConfig, path, elementPath, rootKey, configGroup
        )
    }
    
    private inline fun matchesType(
        definition: Any,
        elementTypeProvider: (Any?) -> IElementType?,
        propertyValueProvider: (Any?) -> Any?,
        typeConfig: CwtTypeConfig,
        path: ParadoxPath,
        elementPath: ParadoxElementPath,
        rootKey: String,
        configGroup: CwtConfigGroup
    ): Boolean {
        val elementType = elementTypeProvider(definition)
        
        //判断definition是否需要是scriptFile还是scriptProperty
        val nameFromFileConfig = typeConfig.nameFromFile
        if(nameFromFileConfig) {
            if(elementType != ParadoxScriptStubElementTypes.FILE) return false
        } else {
            if(elementType != PROPERTY) return false
        }
        
        //判断path是否匹配
        val pathConfig = typeConfig.path ?: return false
        val pathStrictConfig = typeConfig.pathStrict
        if(pathStrictConfig) {
            if(pathConfig != path.parent) return false
        } else {
            if(!pathConfig.matchesPath(path.parent)) return false
        }
        //判断path_name是否匹配
        val pathFileConfig = typeConfig.pathFile //String?
        if(pathFileConfig != null) {
            if(pathFileConfig != path.fileName) return false
        }
        //判断path_extension是否匹配（默认为".txt"，CWT文件中可能未填写，此时直接留空）
        val pathExtensionConfig = typeConfig.pathExtension //String?
        if(pathExtensionConfig != null) {
            if(pathExtensionConfig != "." + path.fileExtension) return false
        }
        
        //如果skip_root_key = any，则要判断是否需要跳过rootKey，如果为any，则任何情况都要跳过（忽略大小写）
        //skip_root_key可以为列表（如果是列表，其中的每一个root_key都要依次匹配）
        //skip_root_key可以重复（其中之一匹配即可）
        val skipRootKeyConfig = typeConfig.skipRootKey
        if(skipRootKeyConfig.isNullOrEmpty()) {
            if(elementPath.length > 1) return false
        } else {
            val result = skipRootKeyConfig.any { elementPath.matchEntire(it, useParentPath = true) }
            if(!result) return false
        }
        //如果starts_with存在，则要求type_key匹配这个前缀（不忽略大小写）
        val startsWithConfig = typeConfig.startsWith
        if(!startsWithConfig.isNullOrEmpty()) {
            val result = rootKey.startsWith(startsWithConfig, true)
            if(!result) return false
        }
        //如果type_key_regex存在，则要求type_key匹配
        val typeKeyRegexConfig = typeConfig.typeKeyRegex
        if(typeKeyRegexConfig != null) {
            val result = typeKeyRegexConfig.matches(rootKey)
            if(!result) return false
        }
        //如果type_key_filter存在，则通过type_key进行过滤（忽略大小写）
        val typeKeyFilterConfig = typeConfig.typeKeyFilter
        if(typeKeyFilterConfig != null && typeKeyFilterConfig.value.isNotEmpty()) {
            val filterResult = typeKeyFilterConfig.where { it.contains(rootKey) }
            if(!filterResult) return false
        }
        //如果name_field存在，则要求root_key必须是由type_key_filter指定的所有可能的root_key之一，或者没有指定任何root_key
        val nameFieldConfig = typeConfig.nameField
        if(nameFieldConfig != null) {
            val result = typeConfig.possibleRootKeys.isEmpty() || typeConfig.possibleRootKeys.contains(rootKey)
            if(!result) return false
        }
        
        //判断definition的propertyValue是否需要是block
        val declarationConfig = configGroup.declarations[typeConfig.name]?.propertyConfig
        //当进行代码补全时需要特殊处理
        val propertyValue = propertyValueProvider(definition)
        val isBlock = elementTypeProvider(propertyValue)?.let { it == BLOCK }
        if(declarationConfig != null && isBlock != null) {
            val isBlockConfig = declarationConfig.valueExpression.type == CwtDataType.Block
            if(isBlockConfig != isBlock) return false
        }
        
        return true
    }
    
    fun matchesSubtype(
        element: ParadoxScriptDefinitionElement,
        subtypeConfig: CwtSubtypeConfig,
        rootKey: String,
        configGroup: CwtConfigGroup,
        subtypes: MutableList<CwtSubtypeConfig>,
        matchOptions: Int = ParadoxConfigMatcher.Options.Default
    ): Boolean {
        //如果only_if_not存在，且已经匹配指定的任意子类型，则不匹配
        val onlyIfNotConfig = subtypeConfig.onlyIfNot
        if(!onlyIfNotConfig.isNullOrEmpty()) {
            val matchesAny = subtypes.any { it.name in onlyIfNotConfig }
            if(matchesAny) return false
        }
        //如果starts_with存在，则要求type_key匹配这个前缀（不忽略大小写）
        val startsWithConfig = subtypeConfig.startsWith
        if(!startsWithConfig.isNullOrEmpty()) {
            val result = rootKey.startsWith(startsWithConfig, false)
            if(!result) return false
        }
        //如果type_key_regex存在，则要求type_key匹配
        val typeKeyRegexConfig = subtypeConfig.typeKeyRegex
        if(typeKeyRegexConfig != null) {
            val result = typeKeyRegexConfig.matches(rootKey)
            if(!result) return false
        }
        //如果type_key_filter存在，则通过type_key进行过滤（忽略大小写）
        val typeKeyFilterConfig = subtypeConfig.typeKeyFilter
        if(typeKeyFilterConfig != null && typeKeyFilterConfig.value.isNotEmpty()) {
            val filterResult = typeKeyFilterConfig.where { it.contains(rootKey) }
            if(!filterResult) return false
        }
        //根据config对property进行内容匹配
        val elementConfig = subtypeConfig.config
        return doMatchDefinition(element, elementConfig, configGroup, matchOptions)
    }
    
    private fun doMatchDefinition(definitionElement: ParadoxScriptDefinitionElement, propertyConfig: CwtPropertyConfig, configGroup: CwtConfigGroup, matchOptions: Int = ParadoxConfigMatcher.Options.Default): Boolean {
        //这里不能基于内联后的声明结构，否则可能会导致SOE
        //也不要参数条件表达式中的声明结构判断，
        val childValueConfigs = propertyConfig.values.orEmpty()
        val blockElement = definitionElement.block //TODO 1.0.4+ 显著影响性能，考虑优化？
        if(childValueConfigs.isNotEmpty()) {
            //匹配值列表
            if(!doMatchValues(definitionElement, blockElement, childValueConfigs, configGroup, matchOptions)) return false //继续匹配
        }
        val childPropertyConfigs = propertyConfig.properties.orEmpty()
        if(childPropertyConfigs.isNotEmpty()) {
            //匹配属性列表
            if(!doMatchProperties(definitionElement, blockElement, childPropertyConfigs, configGroup, matchOptions)) return false //继续匹配
        }
        return true
    }
    
    private fun doMatchProperty(definitionElement: ParadoxScriptDefinitionElement, propertyElement: ParadoxScriptProperty, propertyConfig: CwtPropertyConfig, configGroup: CwtConfigGroup, matchOptions: Int): Boolean {
        val propValue = propertyElement.propertyValue
        if(propValue == null) {
            //对于propertyValue同样这样判断（可能脚本没有写完）
            return propertyConfig.cardinality?.min == 0
        } else {
            when {
                //匹配布尔值
                propertyConfig.booleanValue != null -> {
                    if(propValue !is ParadoxScriptBoolean || propValue.booleanValue != propertyConfig.booleanValue) return false
                }
                //匹配值
                propertyConfig.stringValue != null -> {
                    val expression = ParadoxDataExpression.resolve(propValue, matchOptions)
                    return ParadoxConfigMatcher.matches(propValue, expression, propertyConfig.valueExpression, propertyConfig, configGroup, matchOptions).get(matchOptions)
                }
                //匹配single_alias
                ParadoxConfigHandler.isSingleAlias(propertyConfig) -> {
                    return doMatchSingleAlias(definitionElement, propertyElement, propertyConfig, configGroup, matchOptions)
                }
                //匹配alias
                ParadoxConfigHandler.isAlias(propertyConfig) -> {
                    return doMatchAlias(definitionElement, propertyElement, propertyConfig, configGroup, matchOptions)
                }
                propertyConfig.configs.orEmpty().isNotEmpty() -> {
                    val blockElement = propertyElement.block
                    //匹配值列表
                    if(!doMatchValues(definitionElement, blockElement, propertyConfig.values.orEmpty(), configGroup, matchOptions)) return false
                    //匹配属性列表
                    if(!doMatchProperties(definitionElement, blockElement, propertyConfig.properties.orEmpty(), configGroup, matchOptions)) return false
                }
            }
        }
        return true
    }
    
    private fun doMatchProperties(definitionElement: ParadoxScriptDefinitionElement, blockElement: ParadoxScriptBlockElement?, propertyConfigs: List<CwtPropertyConfig>, configGroup: CwtConfigGroup, matchOptions: Int): Boolean {
        if(propertyConfigs.isEmpty()) return true
        if(blockElement == null) return false
        
        val occurrenceMap = propertyConfigs.associateByTo(mutableMapOf(), { it.key }, { it.toOccurrence(definitionElement, configGroup.project) })
        
        //注意：propConfig.key可能有重复，这种情况下只要有其中一个匹配即可
        val matched = blockElement.processProperty { propertyElement ->
            val keyElement = propertyElement.propertyKey
            val expression = ParadoxDataExpression.resolve(keyElement, matchOptions)
            val propConfigs = propertyConfigs.filter {
                ParadoxConfigMatcher.matches(keyElement, expression, it.keyExpression, it, configGroup, matchOptions).get(matchOptions)
            }
            //如果没有匹配的规则则忽略
            if(propConfigs.isNotEmpty()) {
                val matched = propConfigs.any { propConfig ->
                    val matched = doMatchProperty(definitionElement, propertyElement, propConfig, configGroup, matchOptions)
                    if(matched) occurrenceMap.get(propConfig.key)?.let { it.actual++ }
                    matched
                }
                matched
            } else {
                true
            }
        }
        if(!matched) return false
        
        return occurrenceMap.values.all { (it.actual >= (it.min ?: 1)) && (it.max == null || (it.actual <= (it.max ?: 1))) }
    }
    
    private fun doMatchValues(definitionElement: ParadoxScriptDefinitionElement, blockElement: ParadoxScriptBlockElement?, valueConfigs: List<CwtValueConfig>, configGroup: CwtConfigGroup, matchOptions: Int): Boolean {
        if(valueConfigs.isEmpty()) return true
        if(blockElement == null) return false
        //要求其中所有的value的值在最终都会小于等于指定值
        val minMap = valueConfigs.associateByTo(mutableMapOf(), { it.value }, { it.cardinality?.min ?: 1 }) //默认为1
        
        val matched = blockElement.processValue { valueElement ->
            //如果没有匹配的规则则忽略
            val expression = ParadoxDataExpression.resolve(valueElement, matchOptions)
            
            val matched = valueConfigs.any { valueConfig ->
                val matched = ParadoxConfigMatcher.matches(valueElement, expression, valueConfig.valueExpression, valueConfig, configGroup, matchOptions).get(matchOptions)
                if(matched) minMap.compute(valueConfig.value) { _, v -> if(v == null) 1 else v - 1 }
                matched
            }
            matched
        }
        if(!matched) return false
        
        return minMap.values.all { it <= 0 }
    }
    
    private fun doMatchSingleAlias(definitionElement: ParadoxScriptDefinitionElement, propertyElement: ParadoxScriptProperty, propertyConfig: CwtPropertyConfig, configGroup: CwtConfigGroup, matchOptions: Int): Boolean {
        val singleAliasName = propertyConfig.valueExpression.value ?: return false
        val singleAlias = configGroup.singleAliases[singleAliasName] ?: return false
        return doMatchProperty(definitionElement, propertyElement, singleAlias.config, configGroup, matchOptions)
    }
    
    private fun doMatchAlias(definitionElement: ParadoxScriptDefinitionElement, propertyElement: ParadoxScriptProperty, propertyConfig: CwtPropertyConfig, configGroup: CwtConfigGroup, matchOptions: Int): Boolean {
        //aliasName和aliasSubName需要匹配
        val aliasName = propertyConfig.keyExpression.value ?: return false
        val key = propertyElement.name
        val quoted = propertyElement.propertyKey.text.isLeftQuoted()
        val aliasSubName = ParadoxConfigHandler.getAliasSubName(propertyElement, key, quoted, aliasName, configGroup, matchOptions) ?: return false
        val aliasGroup = configGroup.aliasGroups[aliasName] ?: return false
        val aliases = aliasGroup[aliasSubName] ?: return false
        return aliases.any { alias ->
            doMatchProperty(definitionElement, propertyElement, alias.config, configGroup, matchOptions)
        }
    }
    
    fun getName(element: ParadoxScriptDefinitionElement): String? {
        return runCatching { element.getStub() }.getOrNull()?.name ?: element.definitionInfo?.name
    }
    
    fun getType(element: ParadoxScriptDefinitionElement): String? {
        return runCatching { element.getStub() }.getOrNull()?.type ?: element.definitionInfo?.type
    }
    
    fun getSubtypes(element: ParadoxScriptDefinitionElement): List<String>? {
        //定义的subtype可能需要通过访问索引获取，不能在索引时就获取
        return element.definitionInfo?.subtypes
    }
    
    //stub methods
    
    fun createStubForFile(file: PsiFile, tree: LighterAST): StubElement<*>? {
        //这里使用scriptProperty.definitionInfo.name而非scriptProperty.name
        val psiFile = file as? ParadoxScriptFile ?: return null
        val definitionInfo = psiFile.definitionInfo ?: return null
        val name = definitionInfo.name
        val type = definitionInfo.type
        val subtypes = definitionInfo.subtypes
        val gameType = definitionInfo.gameType
        return ParadoxScriptFileStubImpl(psiFile, name, type, subtypes, gameType)
    }
    
    fun createStub(psi: ParadoxScriptProperty, parentStub: StubElement<*>): ParadoxScriptPropertyStub? {
        //这里使用scriptProperty.definitionInfo.name而非scriptProperty.name
        val definitionInfo = psi.definitionInfo ?: return null
        val name = definitionInfo.name
        val type = definitionInfo.type
        val subtypes = runCatching { definitionInfo.subtypes }.getOrNull() //如果无法在索引时获取，之后再懒加载
        val rootKey = definitionInfo.rootKey
        val elementPath = definitionInfo.elementPath
        val gameType = definitionInfo.gameType
        return ParadoxScriptPropertyStubImpl(parentStub, name, type, subtypes, rootKey, elementPath, gameType)
    }
    
    fun createStub(tree: LighterAST, node: LighterASTNode, parentStub: StubElement<*>): ParadoxScriptPropertyStub? {
        val rootKey = getNameFromNode(node, tree) ?: return null
        if(rootKey.isParameterized()) return null //排除可能带参数的情况
        val psi = parentStub.psi
        val psiFile = psi.containingFile
        val project = psiFile.project
        val file = selectFile(psi) ?: return null
        val fileInfo = file.fileInfo ?: return null
        val gameType = selectGameType(file) ?: return null
        val path = fileInfo.pathToEntry //这里使用pathToEntry
        val elementPath = ParadoxElementPathHandler.getFromFile(node, tree, file) ?: return null
        val configGroup = getCwtConfig(project).get(gameType) //这里需要指定project
        for(typeConfig in configGroup.types.values) {
            if(matchesType(node, tree, typeConfig, path, elementPath, rootKey, configGroup)) {
                //NOTE 这里不处理内联的情况
                val name = when {
                    typeConfig.nameFromFile -> rootKey
                    typeConfig.nameField == "" -> {
                        getValueFromNode(node, tree).orAnonymous()
                    }
                    typeConfig.nameField != null -> {
                        node.firstChild(tree, BLOCK)
                            ?.firstChild(tree) { it.tokenType == PROPERTY && getNameFromNode(it, tree)?.equals(typeConfig.nameField, true) == true }
                            ?.let { getValueFromNode(it, tree) }
                            .orAnonymous()
                    }
                    else -> rootKey
                }
                val type = typeConfig.name
                //val subtypes = runCatching { definitionInfo.subtypes }.getOrNull() //如果无法在索引时获取，之后再懒加载
                return ParadoxScriptPropertyStubImpl(parentStub, name, type, null, rootKey, elementPath, gameType)
            }
        }
        return null
    }
    
    private fun getNameFromNode(node: LighterASTNode, tree: LighterAST): String? {
        return node.firstChild(tree, PROPERTY_KEY)?.firstChild(tree, ParadoxScriptTokenSets.PROPERTY_KEY_TOKENS)?.internNode(tree)?.toString()?.unquote()
    }
    
    private fun getValueFromNode(node: LighterASTNode, tree: LighterAST): String? {
        return node.firstChild(tree, STRING)?.firstChild(tree, ParadoxScriptTokenSets.STRING_TOKENS)?.internNode(tree)?.toString()?.unquote()
    }
    
    fun shouldCreateStub(node: ASTNode): Boolean {
        return true
    }
    
    fun shouldCreateStub(tree: LighterAST, node: LighterASTNode, parentStub: StubElement<*>): Boolean {
        return true
    }
    
    fun getInfoFromStub(element: ParadoxScriptDefinitionElement, project: Project): ParadoxDefinitionInfo? {
        val stub = runCatching { element.getStub() }.getOrNull() ?: return null
        //if(!stub.isValid()) return null //这里不用再次判断
        val name = stub.name
        val type = stub.type
        val gameType = stub.gameType ?: return null
        val configGroup = getCwtConfig(project).get(gameType) //这里需要指定project
        val typeConfig = configGroup.types[type] ?: return null
        val subtypes = stub.subtypes
        val subtypeConfigs = subtypes?.mapNotNull { typeConfig.subtypes[it] }
        val rootKey = stub.rootKey
        val elementPath = stub.elementPath
        return ParadoxDefinitionInfo(name, typeConfig, subtypeConfigs, rootKey, elementPath, gameType, configGroup, element)
    }
    
    //related localisations & images methods
    
    fun getPrimaryLocalisationKey(element: ParadoxScriptDefinitionElement): String? {
        return doGetPrimaryLocalisationKeyFromCache(element)
    }
    
    private fun doGetPrimaryLocalisationKeyFromCache(element: ParadoxScriptDefinitionElement): String? {
        return CachedValuesManager.getCachedValue(element, PlsKeys.cachedDefinitionPrimaryLocalisationKeyKey) {
            ProgressManager.checkCanceled()
            val value = doGetPrimaryLocalisationKey(element)
            val tracker = ParadoxPsiModificationTracker.getInstance(element.project).LocalisationFileTracker
            CachedValueProvider.Result.create(value, element, tracker)
        }
    }
    
    private fun doGetPrimaryLocalisationKey(element: ParadoxScriptDefinitionElement): String? {
        val definitionInfo = element.definitionInfo ?: return null
        val primaryLocalisations = definitionInfo.primaryLocalisations
        if(primaryLocalisations.isEmpty()) return null //没有或者CWT规则不完善
        val project = definitionInfo.project
        for(primaryLocalisation in primaryLocalisations) {
            val selector = localisationSelector(project, element).contextSensitive().preferLocale(preferredParadoxLocale())
            val resolved = primaryLocalisation.locationExpression.resolve(element, definitionInfo, selector)
            val key = resolved?.key ?: continue
            return key
        }
        return null
    }
    
    fun getPrimaryLocalisation(element: ParadoxScriptDefinitionElement): ParadoxLocalisationProperty? {
        return doGetPrimaryLocalisationFromCache(element)
    }
    
    private fun doGetPrimaryLocalisationFromCache(element: ParadoxScriptDefinitionElement): ParadoxLocalisationProperty? =
        CachedValuesManager.getCachedValue(element, PlsKeys.cachedDefinitionPrimaryLocalisationKey) {
            ProgressManager.checkCanceled()
            val value = doGetPrimaryLocalisation(element)
            val tracker = ParadoxPsiModificationTracker.getInstance(element.project).LocalisationFileTracker
            CachedValueProvider.Result.create(value, element, tracker)
        }
    
    private fun doGetPrimaryLocalisation(element: ParadoxScriptDefinitionElement): ParadoxLocalisationProperty? {
        val definitionInfo = element.definitionInfo ?: return null
        val primaryLocalisations = definitionInfo.primaryLocalisations
        if(primaryLocalisations.isEmpty()) return null //没有或者CWT规则不完善
        val project = definitionInfo.project
        for(primaryLocalisation in primaryLocalisations) {
            val selector = localisationSelector(project, element).contextSensitive().preferLocale(preferredParadoxLocale())
            val resolved = primaryLocalisation.locationExpression.resolve(element, definitionInfo, selector)
            val localisation = resolved?.localisation ?: continue
            return localisation
        }
        return null
    }
    
    fun getPrimaryLocalisations(element: ParadoxScriptDefinitionElement): Set<ParadoxLocalisationProperty> {
        return doGetPrimaryLocalisationsFromCache(element)
    }
    
    private fun doGetPrimaryLocalisationsFromCache(element: ParadoxScriptDefinitionElement): Set<ParadoxLocalisationProperty> {
        return CachedValuesManager.getCachedValue(element, PlsKeys.cachedDefinitionPrimaryLocalisationsKey) {
            ProgressManager.checkCanceled()
            val value = doGetPrimaryLocalisations(element)
            val tracker = ParadoxPsiModificationTracker.getInstance(element.project).LocalisationFileTracker
            CachedValueProvider.Result.create(value, element, tracker)
        }
    }
    
    private fun doGetPrimaryLocalisations(element: ParadoxScriptDefinitionElement): Set<ParadoxLocalisationProperty> {
        val definitionInfo = element.definitionInfo ?: return emptySet()
        val primaryLocalisations = definitionInfo.primaryLocalisations
        if(primaryLocalisations.isEmpty()) return emptySet() //没有或者CWT规则不完善
        val project = definitionInfo.project
        val result = mutableSetOf<ParadoxLocalisationProperty>()
        for(primaryLocalisation in primaryLocalisations) {
            val selector = localisationSelector(project, element).contextSensitive().preferLocale(preferredParadoxLocale())
            val resolved = primaryLocalisation.locationExpression.resolveAll(element, definitionInfo, selector)
            val localisations = resolved?.localisations ?: continue
            result.addAll(localisations)
        }
        return result
    }
    
    fun getPrimaryImage(element: ParadoxScriptDefinitionElement): PsiFile? {
        return doGetPrimaryImageFromCache(element)
    }
    
    private fun doGetPrimaryImageFromCache(element: ParadoxScriptDefinitionElement): PsiFile? {
        return CachedValuesManager.getCachedValue(element, PlsKeys.cachedDefinitionPrimaryImageKey) {
            ProgressManager.checkCanceled()
            val value = doGetPrimaryImage(element)
            val tracker = PsiModificationTracker.MODIFICATION_COUNT
            CachedValueProvider.Result.create(value, element, tracker)
        }
    }
    
    private fun doGetPrimaryImage(element: ParadoxScriptDefinitionElement): PsiFile? {
        val definitionInfo = element.definitionInfo ?: return null
        val primaryImages = definitionInfo.primaryImages
        if(primaryImages.isEmpty()) return null //没有或者CWT规则不完善
        val project = definitionInfo.project
        for(primaryImage in primaryImages) {
            val resolved = primaryImage.locationExpression.resolve(element, definitionInfo, project)
            val file = resolved?.file
            if(file == null) continue
            element.putUserData(PlsKeys.iconFrameKey, resolved.frame)
            return file
        }
        return null
    }
    
    fun getLocalizedNames(element: ParadoxScriptDefinitionElement): Set<String> {
        return doGetLocalizedNamesFromCache(element)
    }
    
    private fun doGetLocalizedNamesFromCache(element: ParadoxScriptDefinitionElement): Set<String> {
        return CachedValuesManager.getCachedValue(element, PlsKeys.cachedDefinitionLocalizedNamesKey) {
            ProgressManager.checkCanceled()
            val value = doGetLocalizedNames(element)
            val tracker = ParadoxPsiModificationTracker.getInstance(element.project).LocalisationFileTracker
            CachedValueProvider.Result.create(value, element, tracker)
        }
    }
    
    private fun doGetLocalizedNames(element: ParadoxScriptDefinitionElement): Set<String> {
        //这里返回的是基于偏好语言区域的所有本地化名字（即使最终会被覆盖掉）
        val localizedNames = mutableSetOf<String>()
        val primaryLocalisations = getPrimaryLocalisations(element)
        primaryLocalisations.forEach { localisation ->
            ProgressManager.checkCanceled()
            val r = ParadoxLocalisationTextExtractor.extract(localisation).takeIfNotEmpty()
            if(r != null) localizedNames.add(r)
        }
        return localizedNames
    }
}
