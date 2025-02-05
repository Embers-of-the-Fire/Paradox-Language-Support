package icu.windea.pls.lang.modifier.impl

import com.intellij.codeInsight.completion.*
import com.intellij.openapi.progress.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.util.*
import icons.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.annotations.*
import icu.windea.pls.core.codeInsight.completion.*
import icu.windea.pls.core.psi.*
import icu.windea.pls.core.search.*
import icu.windea.pls.core.search.selector.*
import icu.windea.pls.lang.*
import icu.windea.pls.lang.cwt.*
import icu.windea.pls.lang.cwt.config.*
import icu.windea.pls.lang.modifier.*
import icu.windea.pls.model.*
import icu.windea.pls.script.psi.*

/**
 * 通过经济类型（`economic_category`）生成修正。
 */
@WithGameType(ParadoxGameType.Stellaris)
class StellarisEconomicCategoryModifierSupport : ParadoxModifierSupport {
    companion object {
        const val economicCategoriesDirPath = "common/economic_categories"
        const val economicCategoriesPathExpression = "common/economic_categories/,.txt"
    }
    
    override fun matchModifier(name: String, element: PsiElement, configGroup: CwtConfigGroup): Boolean {
        val modifierName = name
        val project = configGroup.project
        val selector = definitionSelector(project, element).distinctByName()
        var r = false
        ParadoxDefinitionSearch.search("economic_category", selector).processQueryAsync p@{
            ProgressManager.checkCanceled()
            val info = StellarisEconomicCategoryHandler.getInfo(it) ?: return@p true
            for(modifierInfo in info.modifiers) {
                if(modifierInfo.name == modifierName) {
                    r = true
                    return@p false
                }
            }
            true
        }
        return r
    }
    
    override fun resolveModifier(name: String, element: ParadoxScriptStringExpressionElement, configGroup: CwtConfigGroup): ParadoxModifierElement? {
        val modifierName = name
        val gameType = configGroup.gameType ?: return null
        val project = configGroup.project
        var economicCategoryInfo: StellarisEconomicCategoryInfo? = null
        var economicCategoryModifierInfo: StellarisEconomicCategoryModifierInfo? = null
        val selector = definitionSelector(project, element).contextSensitive().distinctByName()
        ParadoxDefinitionSearch.search("economic_category", selector).processQueryAsync p@{
            ProgressManager.checkCanceled()
            val info = StellarisEconomicCategoryHandler.getInfo(it) ?: return@p true
            for(modifierInfo in info.modifiers) {
                if(modifierInfo.name == modifierName) {
                    economicCategoryInfo = info
                    economicCategoryModifierInfo = modifierInfo
                    return@p false
                }
            }
            true
        }
        if(economicCategoryInfo == null || economicCategoryModifierInfo == null) return null
        val resolved = ParadoxModifierElement(element, modifierName, gameType, project)
        resolved.putUserData(ParadoxModifierSupport.Keys.economicCategoryInfo, economicCategoryInfo)
        resolved.putUserData(ParadoxModifierSupport.Keys.economicCategoryModifierInfo, economicCategoryModifierInfo)
        resolved.putUserData(ParadoxModifierSupport.Keys.support, this)
        return resolved
    }
    
    override fun completeModifier(context: ProcessingContext, result: CompletionResultSet, modifierNames: MutableSet<String>): Unit = with(context) {
        val element = contextElement
        if(element !is ParadoxScriptStringExpressionElement) return
        val configGroup = configGroup
        val project = configGroup.project
        val selector = definitionSelector(project, element).contextSensitive()
        ParadoxDefinitionSearch.search("economic_category", selector).processQueryAsync p@{ definition ->
            ProgressManager.checkCanceled()
            val info = StellarisEconomicCategoryHandler.getInfo(definition) ?: return@p true
            //排除不匹配modifier的supported_scopes的情况
            val modifierCategories = StellarisEconomicCategoryHandler.resolveModifierCategory(info.modifierCategory, configGroup)
            val supportedScopes = modifierCategories.getSupportedScopes()
            val scopeMatched = ParadoxScopeHandler.matchesScope(scopeContext, supportedScopes, configGroup)
            if(!scopeMatched && getSettings().completion.completeOnlyScopeIsMatched) return@p true
            
            val tailText = " from economic category " + info.name
            val typeText = info.name
            val typeIcon = PlsIcons.Definition
            for(modifierInfo in info.modifiers) {
                val name = modifierInfo.name
                //排除重复的
                if(!modifierNames.add(name)) continue
                
                val modifierElement = ParadoxModifierHandler.resolveModifier(name, element, configGroup, this@StellarisEconomicCategoryModifierSupport)
                val builder = ParadoxScriptExpressionLookupElementBuilder.create(modifierElement, name)
                    .withIcon(PlsIcons.Modifier)
                    .withTailText(tailText)
                    .withTypeText(typeText)
                    .withTypeIcon(typeIcon)
                    .withScopeMatched(scopeMatched)
                    .letIf(getSettings().completion.completeByLocalizedName) {
                        //如果启用，也基于修正的本地化名字进行代码补全
                        val localizedNames = ParadoxModifierHandler.getModifierLocalizedNames(name, project, element)
                        it.withLocalizedNames(localizedNames)
                    }
                result.addScriptExpressionElement(context, builder)
            }
            true
        }
    }
    
    override fun getModificationTracker(resolved: ParadoxModifierElement): ModificationTracker {
        return ParadoxPsiModificationTracker.getInstance(resolved.project).ScriptFileTracker("common/economic_categories:txt")
    }
    
    override fun getModifierCategories(element: ParadoxModifierElement): Map<String, CwtModifierCategoryConfig>? {
        val economicCategoryInfo = element.getUserData(ParadoxModifierSupport.Keys.economicCategoryInfo) ?: return null
        val modifierCategory = economicCategoryInfo.modifierCategory //may be null
        val configGroup = getCwtConfig(element.project).get(element.gameType)
        return StellarisEconomicCategoryHandler.resolveModifierCategory(modifierCategory, configGroup)
    }
    
    override fun buildDocumentationDefinition(element: ParadoxModifierElement, builder: StringBuilder): Boolean = with(builder) {
        val gameType = element.gameType
        val economicCategoryInfo = element.getUserData(ParadoxModifierSupport.Keys.economicCategoryInfo) ?: return false
        val modifierInfo = element.getUserData(ParadoxModifierSupport.Keys.economicCategoryModifierInfo) ?: return false
        
        //加上名字
        val name = element.name
        append(PlsBundle.message("prefix.modifier")).append(" <b>").append(name.escapeXml().orAnonymous()).append("</b>")
        //加上经济类型信息
        appendBr().appendIndent()
        append(PlsBundle.message("generatedFromEconomicCategory"))
        append(" ")
        appendDefinitionLink(gameType, economicCategoryInfo.name, "economic_category", element)
        if(modifierInfo.resource != null) {
            appendBr().appendIndent()
            append(PlsBundle.message("generatedFromResource"))
            append(" ")
            appendDefinitionLink(gameType, modifierInfo.resource, "resource", element)
        } else {
            appendBr().appendIndent()
            append(PlsBundle.message("forAiBudget"))
        }
        
        return true
    }
    
    override fun buildDDocumentationDefinitionForDefinition(definition: ParadoxScriptDefinitionElement, definitionInfo: ParadoxDefinitionInfo, builder: StringBuilder): Boolean = with(builder) {
        val gameType = definitionInfo.gameType
        val configGroup = definitionInfo.configGroup
        val project = configGroup.project
        val selector = definitionSelector(project, definition).contextSensitive()
        val economicCategory = ParadoxDefinitionSearch.search(definitionInfo.name, "economic_category", selector)
            .findFirst()
            ?: return false
        val economicCategoryInfo = StellarisEconomicCategoryHandler.getInfo(economicCategory) ?: return false
        for(modifierInfo in economicCategoryInfo.modifiers) {
            appendBr()
            append(PlsBundle.message("prefix.generatedModifier")).append(" ")
            append(modifierInfo.name)
            if(modifierInfo.resource != null) {
                grayed {
                    append(" ")
                    append(PlsBundle.message("fromResource"))
                    append(" ")
                    appendDefinitionLink(gameType, modifierInfo.resource, "resource", definition)
                }
            } else {
                grayed {
                    append(" ")
                    append(PlsBundle.message("forAiBudget"))
                }
            }
        }
        return true
    }
}