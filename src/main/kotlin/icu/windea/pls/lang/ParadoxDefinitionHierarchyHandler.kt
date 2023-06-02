package icu.windea.pls.lang

import com.intellij.openapi.progress.*
import com.intellij.openapi.project.*
import com.intellij.openapi.vfs.*
import com.intellij.psi.search.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.index.lazy.*
import icu.windea.pls.lang.hierarchy.impl.*
import icu.windea.pls.lang.model.*
import icu.windea.pls.script.*

object ParadoxDefinitionHierarchyHandler {
    fun processQuery(
        project: Project,
        supportId: String,
        gameType: ParadoxGameType,
        scope: GlobalSearchScope,
        processor: (file: VirtualFile, infos: List<ParadoxDefinitionHierarchyInfo>) -> Boolean
    ): Boolean {
        ProgressManager.checkCanceled()
        if(SearchScope.isEmptyScope(scope)) return true
        
        return FileTypeIndex.processFiles(ParadoxScriptFileType, p@{ file ->
            ProgressManager.checkCanceled()
            if(selectGameType(file) != gameType) return@p true //check game type at file level
            val fileData = ParadoxDefinitionHierarchyIndex.getFileData(file, project)
            val infos = fileData.definitionHierarchyInfoGroup.get(supportId)
            if(infos.isNullOrEmpty()) return@p true
            processor(file, infos)
        }, scope)
    }
}

inline fun ParadoxDefinitionHierarchyHandler.processEventsInOnAction(
    project: Project,
    gameType: ParadoxGameType,
    scope: GlobalSearchScope,
    crossinline processor: ParadoxEventInOnActionHierarchyContext.(file: VirtualFile, infos: List<ParadoxDefinitionHierarchyInfo>) -> Boolean
): Boolean {
    val supportId = ParadoxEventInOnActionDefinitionHierarchySupport.ID
    return processQuery(project, supportId, gameType, scope) p@{ file, value ->
        ParadoxEventInOnActionHierarchyContext.processor(file, value)
    }
}

object ParadoxEventInOnActionHierarchyContext

inline fun ParadoxDefinitionHierarchyHandler.processEventsInEffect(
    project: Project,
    gameType: ParadoxGameType,
    scope: GlobalSearchScope,
    crossinline processor: ParadoxEventInEffectHierarchyContext.(file: VirtualFile, infos: List<ParadoxDefinitionHierarchyInfo>) -> Boolean
): Boolean {
    val supportId = ParadoxEventInEffectDefinitionHierarchySupport.ID
    return processQuery(project, supportId, gameType, scope) p@{ file, value ->
        ParadoxEventInEffectHierarchyContext.processor(file, value)
    }
}

object ParadoxEventInEffectHierarchyContext {
    val ParadoxDefinitionHierarchyInfo.containingEventScope: String? by ParadoxEventInEffectDefinitionHierarchySupport.containingEventScopeKey
    val ParadoxDefinitionHierarchyInfo.scopesElementOffset: Int? by ParadoxEventInEffectDefinitionHierarchySupport.scopesElementOffsetKey
}

inline fun ParadoxDefinitionHierarchyHandler.processOnActionsInEffect(
    project: Project,
    gameType: ParadoxGameType,
    scope: GlobalSearchScope,
    crossinline processor: ParadoxEventInEffectHierarchyContext.(file: VirtualFile, infos: List<ParadoxDefinitionHierarchyInfo>) -> Boolean
): Boolean {
    val supportId = ParadoxOnActionInEffectDefinitionHierarchySupport.ID
    return processQuery(project, supportId, gameType, scope) p@{ file, value ->
        ParadoxEventInEffectHierarchyContext.processor(file, value)
    }
}

object ParadoxOnActionsInEffectHierarchyContext {
    val ParadoxDefinitionHierarchyInfo.containingEventScope: String? by ParadoxOnActionInEffectDefinitionHierarchySupport.containingEventScopeKey
    val ParadoxDefinitionHierarchyInfo.scopesElementOffset: Int? by ParadoxOnActionInEffectDefinitionHierarchySupport.scopesElementOffsetKey
}

inline fun ParadoxDefinitionHierarchyHandler.processLocalisationParameters(
    project: Project,
    gameType: ParadoxGameType,
    scope: GlobalSearchScope,
    crossinline processor: ParadoxLocalisationParameterHierarchyContext.(file: VirtualFile, infos: List<ParadoxDefinitionHierarchyInfo>) -> Boolean
): Boolean {
    val supportId = ParadoxLocalisationParameterDefinitionHierarchySupport.ID
    return processQuery(project, supportId, gameType, scope) p@{ file, value ->
        ParadoxLocalisationParameterHierarchyContext.processor(file, value)
    }
}

object ParadoxLocalisationParameterHierarchyContext {
    val ParadoxDefinitionHierarchyInfo.localisationName: String? by ParadoxLocalisationParameterDefinitionHierarchySupport.localisationNameKey
}

inline fun ParadoxDefinitionHierarchyHandler.processInferredScopeContextAwareDefinitions(
    project: Project,
    gameType: ParadoxGameType,
    scope: GlobalSearchScope,
    crossinline processor: ParadoxLocalisationParameterHierarchyContext.(file: VirtualFile, infos: List<ParadoxDefinitionHierarchyInfo>) -> Boolean
): Boolean {
    val supportId = ParadoxInferredScopeContextAwareDefinitionHierarchySupport.ID
    return processQuery(project, supportId, gameType, scope) p@{ file, value ->
        ParadoxLocalisationParameterHierarchyContext.processor(file, value)
    }
}
