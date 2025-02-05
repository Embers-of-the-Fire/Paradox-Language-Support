package icu.windea.pls.core.search

import com.intellij.openapi.application.*
import com.intellij.openapi.progress.*
import com.intellij.openapi.vfs.*
import com.intellij.psi.search.*
import com.intellij.util.*
import icu.windea.pls.core.*
import icu.windea.pls.core.collections.*
import icu.windea.pls.core.index.*
import icu.windea.pls.lang.*
import icu.windea.pls.model.*
import icu.windea.pls.script.*

class ParadoxParameterSearcher : QueryExecutorBase<ParadoxParameterInfo, ParadoxParameterSearch.SearchParameters>() {
    override fun processQuery(queryParameters: ParadoxParameterSearch.SearchParameters, consumer: Processor<in ParadoxParameterInfo>) {
        ProgressManager.checkCanceled()
        val scope = queryParameters.selector.scope
        if(SearchScope.isEmptyScope(scope)) return
        val name = queryParameters.name
        val contextKey = queryParameters.contextKey
        val project = queryParameters.project
        val selector = queryParameters.selector
        val gameType = selector.gameType ?: return
        
        doProcessFiles(scope) p@{ file ->
            ProgressManager.checkCanceled()
            ParadoxCoreHandler.getFileInfo(file) ?: return@p true //ensure file info is resolved here
            if(selectGameType(file) != gameType) return@p true //check game type at file level
            
            val fileData = findIndex<ParadoxParameterIndex>().getFileData(file, project)
            if(fileData.isEmpty()) return@p true
            val parameterInfoList = fileData[contextKey]
            if(parameterInfoList.isNullOrEmpty()) return@p true
            parameterInfoList.forEachFast { info ->
                if(name == null || name == info.name) {
                    val r = info.withVirtualFile(file) { consumer.process(info) }
                    if(!r) return@p false
                }
            }
            
            true
        }
    }
    
    private fun doProcessFiles(scope: GlobalSearchScope, processor: Processor<VirtualFile>): Boolean {
        return FileTypeIndex.processFiles(ParadoxScriptFileType, processor, scope)
    }
}