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

class ParadoxLocalisationParameterSearcher : QueryExecutorBase<ParadoxLocalisationParameterInfo, ParadoxLocalisationParameterSearch.SearchParameters>() {
    override fun processQuery(queryParameters: ParadoxLocalisationParameterSearch.SearchParameters, consumer: Processor<in ParadoxLocalisationParameterInfo>) {
        ProgressManager.checkCanceled()
        val scope = queryParameters.selector.scope
        if(SearchScope.isEmptyScope(scope)) return
        val name = queryParameters.name
        val localisationName = queryParameters.localisationName
        val project = queryParameters.project
        val selector = queryParameters.selector
        val gameType = selector.gameType ?: return
        
        doProcessFiles(scope) p@{ file ->
            ProgressManager.checkCanceled()
            ParadoxCoreHandler.getFileInfo(file) ?: return@p true //ensure file info is resolved here
            if(selectGameType(file) != gameType) return@p true //check game type at file level
            
            val fileData = findIndex<ParadoxLocalisationParameterIndex>().getFileData(file, project)
            if(fileData.isEmpty()) return@p true
            val localisationParameterInfoList = fileData[localisationName]
            if(localisationParameterInfoList.isNullOrEmpty()) return@p true
            localisationParameterInfoList.forEachFast { info ->
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
