package icu.windea.pls.core.search.implementations

import com.intellij.openapi.application.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.intellij.psi.search.*
import com.intellij.psi.search.searches.*
import com.intellij.util.*
import icu.windea.pls.*
import icu.windea.pls.core.search.*
import icu.windea.pls.core.selector.chained.*
import icu.windea.pls.lang.model.*
import icu.windea.pls.localisation.psi.*

/**
 * 本地化的实现的查询。加入所有作用域内的包括不同语言区域在内的同名本地化。
 */
class ParadoxLocalisationImplementationsSearch : QueryExecutor<PsiElement, DefinitionsScopedSearch.SearchParameters> {
    override fun execute(queryParameters: DefinitionsScopedSearch.SearchParameters, consumer: Processor<in PsiElement>): Boolean {
        //得到解析后的PSI元素
        val sourceElement = queryParameters.element
        if(sourceElement !is ParadoxLocalisationProperty) return true
        val localisationInfo = runReadAction { sourceElement.localisationInfo }
        if(localisationInfo == null) return true
        val name = localisationInfo.name
        if(name.isEmpty()) return true
        val project = queryParameters.project
        val gameType = localisationInfo.gameType ?: return true
        DumbService.getInstance(project).runReadActionInSmartMode {
            val category = localisationInfo.category
            //这里不需要也无法进行排序
            //TODO 暂时限定语言区域
            val selector = localisationSelector(project).gameType(gameType).preferRootFrom(sourceElement).preferLocale(preferredParadoxLocale())
                .withSearchScope(GlobalSearchScope.allScope(project)) ////使用全部作用域
            val localisations = when(category) {
                ParadoxLocalisationCategory.Localisation -> ParadoxLocalisationSearch.search(name, selector = selector).findAll()
                ParadoxLocalisationCategory.SyncedLocalisation -> ParadoxSyncedLocalisationSearch.search(name, selector = selector).findAll()
            }
            localisations.forEach {
                consumer.process(it)
            }
        }
        return true
    }
}