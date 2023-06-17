package icu.windea.pls.core.search

import com.intellij.openapi.application.*
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.*
import com.intellij.openapi.vfs.*
import com.intellij.psi.search.*
import com.intellij.util.*
import com.intellij.util.indexing.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.index.*
import icu.windea.pls.lang.*
import icu.windea.pls.lang.expression.*

/**
 * 文件路径的查询器。
 */
class ParadoxFilePathSearcher : QueryExecutorBase<VirtualFile, ParadoxFilePathSearch.SearchParameters>() {
    override fun processQuery(queryParameters: ParadoxFilePathSearch.SearchParameters, consumer: Processor<in VirtualFile>) {
        ProgressManager.checkCanceled()
        val scope = queryParameters.selector.scope
        if(SearchScope.isEmptyScope(scope)) return
        val filePath = queryParameters.filePath?.trimEnd('/')
        val configExpression = queryParameters.configExpression
        val project = queryParameters.project
        val gameType = queryParameters.selector.gameType
        val contextElement = queryParameters.selector.file?.toPsiFile(project)
        
        
        fun processFile(file: VirtualFile, consumer: Processor<in VirtualFile>): Boolean {
            ProgressManager.checkCanceled()
            ParadoxCoreHandler.getFileInfo(file) ?: return true //ensure file info is resolved here
            if(gameType != null && selectGameType(file) != gameType) return true //check game type at file level
            return consumer.process(file)
        }
        
        //下面这段代码需要确保在smartMode中执行（否则初次打开项目时，文件引用可能会显示为无法解析）
        DumbService.getInstance(project).runReadActionInSmartMode action@{
            if(configExpression == null) {
                if(filePath == null) {
                    val keys = FileBasedIndex.getInstance().getAllKeys(ParadoxFilePathIndex.NAME, project)
                    FileBasedIndex.getInstance().processFilesContainingAnyKey(ParadoxFilePathIndex.NAME, keys, scope, null, null) {
                        processFile(it, consumer)
                    }
                } else {
                    val keys = getFilePaths(filePath, queryParameters)
                    FileBasedIndex.getInstance().processFilesContainingAnyKey(ParadoxFilePathIndex.NAME, keys, scope, null, null) {
                        processFile(it, consumer)
                    }
                }
            } else {
                val support = ParadoxPathReferenceExpressionSupport.get(configExpression) ?: return@action
                if(filePath == null) {
                    val keys = mutableSetOf<String>()
                    FileBasedIndex.getInstance().processAllKeys(ParadoxFilePathIndex.NAME, p@{ p ->
                        if(!support.matches(configExpression, contextElement, p)) return@p true
                        keys.add(p)
                    }, scope, null)
                    FileBasedIndex.getInstance().processFilesContainingAnyKey(ParadoxFilePathIndex.NAME, keys, scope, null, null) {
                        processFile(it, consumer)
                    }
                } else {
                    val resolvedPath = support.resolvePath(configExpression, filePath)
                    if(resolvedPath != null) {
                        val keys = setOf(resolvedPath)
                        FileBasedIndex.getInstance().processFilesContainingAnyKey(ParadoxFilePathIndex.NAME, keys, scope, null, null) {
                            processFile(it, consumer)
                        }
                        return@action
                    }
                    val resolvedFileName = support.resolveFileName(configExpression, filePath)
                    FilenameIndex.processFilesByName(resolvedFileName, true, scope) p@{
                        val p = it.fileInfo?.path?.path ?: return@p true
                        if(!support.matches(configExpression, contextElement, p)) return@p true
                        processFile(it, consumer)
                    }
                }
            }
        }
    }
    
    private fun getFilePaths(filePath: String, queryParameters: ParadoxFilePathSearch.SearchParameters): Set<String> {
        if(queryParameters.ignoreLocale) {
            return getFilePathsIgnoreLocale(filePath) ?: setOf(filePath)
        } else {
            return setOf(filePath)
        }
    }
    
    private fun getFilePathsIgnoreLocale(filePath: String): Set<String>? {
        if(!filePath.endsWith(".yml", true)) return null //仅限本地化文件
        val localeStrings = getCwtConfig().core.localisationLocalesNoDefaultNoPrefix.keys
        var index = 0
        var usedLocaleString: String? = null
        for(localeString in localeStrings) {
            val nextIndex = filePath.indexOf(localeString, index)
            if(nextIndex == -1) continue
            index = nextIndex + localeString.length
            if(usedLocaleString != localeString) {
                if(usedLocaleString != null) {
                    //类似将l_english.yml放到l_simp_chinese目录下的情况，此时直接不作处理
                    return null
                } else {
                    usedLocaleString = localeString
                }
            }
        }
        if(usedLocaleString == null) return null
        val result = mutableSetOf<String>()
        result.add(filePath)
        localeStrings.forEach { result.add(filePath.replace(usedLocaleString, it)) }
        return result
    }
}