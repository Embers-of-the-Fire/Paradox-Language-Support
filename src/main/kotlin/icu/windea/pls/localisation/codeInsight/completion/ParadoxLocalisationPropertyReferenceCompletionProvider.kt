package icu.windea.pls.localisation.codeInsight.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.*
import com.intellij.openapi.progress.*
import com.intellij.psi.util.*
import com.intellij.util.*
import icu.windea.pls.core.*
import icu.windea.pls.core.codeInsight.completion.*
import icu.windea.pls.core.search.*
import icu.windea.pls.core.search.selector.*
import icu.windea.pls.lang.*
import icu.windea.pls.localisation.psi.*
import icu.windea.pls.model.*

/**
 * 提供属性引用名字的代码补全。
 */
class ParadoxLocalisationPropertyReferenceCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        val offsetInParent = parameters.offset - parameters.position.startOffset
        val keyword = parameters.position.getKeyword(offsetInParent)
        val file = parameters.originalFile.castOrNull<ParadoxLocalisationFile>() ?: return
        val category = ParadoxLocalisationCategory.resolve(file) ?: return
        val project = parameters.originalFile.project
        
        //不提示predefined_parameter
        
        //提示parameter
        val localisation = parameters.position.parentOfType<ParadoxLocalisationProperty>()
        if(localisation != null) {
            ParadoxLocalisationParameterHandler.completeParameters(localisation, result)
        }
        
        //本地化的提示结果可能有上千条，因此这里改为先按照输入的关键字过滤结果，关键字变更时重新提示
        result.restartCompletionOnAnyPrefixChange()
        
        //提示localisation或者synced_localisation
        val selector = localisationSelector(project, file)
            .contextSensitive()
            .preferLocale(ParadoxLocaleHandler.getPreferredLocale())
            //.distinctByName() //这里selector不需要指定去重
        val processor = LimitedCompletionProcessor<ParadoxLocalisationProperty> {
            ProgressManager.checkCanceled()
            val name = it.name
            val icon = it.icon
            val typeFile = it.containingFile
            val lookupElement = LookupElementBuilder.create(it, name)
                .withIcon(icon)
                .withTypeText(typeFile.name, typeFile.icon, true)
            result.addElement(lookupElement)
            true
        }
        when(category) {
            ParadoxLocalisationCategory.Localisation -> ParadoxLocalisationSearch.processVariants(result.prefixMatcher, selector, processor)
            ParadoxLocalisationCategory.SyncedLocalisation -> ParadoxSyncedLocalisationSearch.processVariants(result.prefixMatcher, selector, processor)
        }
    }
}
