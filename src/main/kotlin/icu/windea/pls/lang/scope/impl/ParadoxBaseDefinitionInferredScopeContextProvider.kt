package icu.windea.pls.lang.scope.impl

import com.intellij.openapi.application.*
import com.intellij.openapi.progress.*
import com.intellij.openapi.util.*
import com.intellij.psi.search.*
import com.intellij.psi.util.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.collections.*
import icu.windea.pls.core.index.*
import icu.windea.pls.core.index.hierarchy.*
import icu.windea.pls.core.psi.*
import icu.windea.pls.core.search.scope.*
import icu.windea.pls.lang.*
import icu.windea.pls.lang.cwt.*
import icu.windea.pls.lang.scope.*
import icu.windea.pls.model.*
import icu.windea.pls.script.psi.*

private val cachedScopeContextInferenceInfoKey = Key.create<CachedValue<ParadoxScopeContextInferenceInfo>>("paradox.cached.scopeContextInferenceInfo")

private val DEFINITION_TYPES = arrayOf("scripted_trigger", "scripted_effect")

/**
 * 推断scripted_trigger、scripted_effect等的作用域上下文（仅限this和root）。
 */
class ParadoxBaseDefinitionInferredScopeContextProvider : ParadoxDefinitionInferredScopeContextProvider {
    override fun getScopeContext(definition: ParadoxScriptDefinitionElement, definitionInfo: ParadoxDefinitionInfo): ParadoxScopeContextInferenceInfo? {
        if(!getSettings().inference.scopeContext) return null
        if(definitionInfo.type !in DEFINITION_TYPES) return null
        return doGetScopeContextFromCache(definition)
    }
    
    private fun doGetScopeContextFromCache(definition: ParadoxScriptDefinitionElement): ParadoxScopeContextInferenceInfo? {
        return CachedValuesManager.getCachedValue(definition, cachedScopeContextInferenceInfoKey) {
            ProgressManager.checkCanceled()
            val value = doGetScopeContext(definition)
            val tracker0 = ParadoxPsiModificationTracker.DefinitionScopeContextInferenceTracker
            val tracker = ParadoxPsiModificationTracker.getInstance(definition.project).ScriptFileTracker
            CachedValueProvider.Result.create(value, tracker0, tracker)
        }
    }
    
    private fun doGetScopeContext(definition: ParadoxScriptDefinitionElement): ParadoxScopeContextInferenceInfo? {
        val definitionInfo = definition.definitionInfo ?: return null
        
        //optimize search scope
        val searchScope = runReadAction { ParadoxSearchScope.fromElement(definition) }
            ?: return null
        val configGroup = definitionInfo.configGroup
        val scopeContextMap = mutableMapOf<String, String?>()
        var hasConflict = false
        val r = doProcessQuery(definitionInfo, searchScope, scopeContextMap, configGroup)
        if(!r) hasConflict = true
        val resultScopeContextMap = scopeContextMap.takeIfNotEmpty() ?: return null
        return ParadoxScopeContextInferenceInfo(resultScopeContextMap, hasConflict)
    }
    
    private fun doProcessQuery(
        definitionInfo: ParadoxDefinitionInfo,
        searchScope: GlobalSearchScope,
        scopeContextMap: MutableMap<String, String?>,
        configGroup: CwtConfigGroup
    ): Boolean {
        ProgressManager.checkCanceled()
        val project = configGroup.project
        val gameType = configGroup.gameType ?: return true
        return withRecursionGuard("icu.windea.pls.lang.scope.impl.ParadoxBaseDefinitionInferredScopeContextProvider.doProcessQuery") {
            withCheckRecursion(definitionInfo.name + "@" + definitionInfo.type) {
                val index = findIndex<ParadoxInferredScopeContextAwareDefinitionHierarchyIndex>()
                ParadoxDefinitionHierarchyHandler.processQuery(index , project, gameType, searchScope) p@{ file, fileData ->
                    val infos = fileData.values.firstOrNull() ?: return@p true
                    val psiFile = file.toPsiFile(project) ?: return@p true
                    infos.forEachFast f@{ info ->
                        //TODO 1.0.6+ 这里对应的引用可能属于某个复杂表达式的一部分（目前不需要考虑兼容这种情况）
                        val definitionName = info.definitionName
                        if(definitionName != definitionInfo.name) return@f //matches definition name
                        val eventType = info.typeExpression.substringBefore('.')
                        if(eventType != definitionInfo.type) return@f //matches definition type
                        val e = psiFile.findElementAt(info.elementOffset) ?: return@f
                        val m = e.parentOfType<ParadoxScriptMemberElement>(withSelf = false) ?: return@f
                        val scopeContext = ParadoxScopeHandler.getScopeContext(m) ?: return@f
                        val map = with(scopeContext) {
                            buildMap {
                                put("this", scope.id)
                                root?.let { put("root", it.scope.id) }
                            }
                        }
                        //val map = scopeContext.detailMap.mapValues { (_, v) -> v.id }
                        if(scopeContextMap.isNotEmpty()) {
                            val mergedMap = ParadoxScopeHandler.mergeScopeContextMap(scopeContextMap, map, true)
                            if(mergedMap != null) {
                                scopeContextMap.clear()
                                scopeContextMap.putAll(mergedMap)
                            } else {
                                return@p false
                            }
                        } else {
                            scopeContextMap.putAll(map)
                        }
                    }
                    true
                }
            }
        } ?: false
    }
    
    override fun getMessage(definition: ParadoxScriptDefinitionElement, definitionInfo: ParadoxDefinitionInfo, info: ParadoxScopeContextInferenceInfo): String {
        return PlsBundle.message("script.annotator.scopeContext.0", definitionInfo.name)
    }
    
    override fun getErrorMessage(definition: ParadoxScriptDefinitionElement, definitionInfo: ParadoxDefinitionInfo, info: ParadoxScopeContextInferenceInfo): String {
        return PlsBundle.message("script.annotator.scopeContext.0.conflict", definitionInfo.name)
    }
}
