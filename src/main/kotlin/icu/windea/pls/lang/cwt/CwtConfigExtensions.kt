@file:Suppress("unused")

package icu.windea.pls.lang.cwt

import com.intellij.psi.*
import com.intellij.util.*
import icu.windea.pls.core.*
import icu.windea.pls.core.collections.*
import icu.windea.pls.core.psi.*
import icu.windea.pls.core.references.*
import icu.windea.pls.cwt.psi.*
import icu.windea.pls.lang.*
import icu.windea.pls.lang.cwt.config.*
import icu.windea.pls.lang.cwt.expression.*
import icu.windea.pls.script.psi.*

inline fun CwtMemberConfig<*>.processParent(inline: Boolean = false, processor: (CwtMemberConfig<*>) -> Boolean): Boolean {
    var parent = this.parent
    while(parent != null) {
        val result = processor(parent)
        if(!result) return false
        if(inline) {
            parent = parent.inlineableConfig?.config ?: parent.parent
        } else {
            parent = parent.parent
        }
    }
    return true
}

fun CwtMemberConfig<*>.processDescendants(processor: (CwtMemberConfig<*>) -> Boolean): Boolean {
    return doProcessDescendants(processor)
}

private fun CwtMemberConfig<*>.doProcessDescendants(processor: (CwtMemberConfig<*>) -> Boolean): Boolean {
    processor(this).also { if(!it) return false }
    this.configs?.process { it.doProcessDescendants(processor) }?.also { if(!it) return false }
    return true
}

fun CwtConfig<*>.findAliasConfig(): CwtAliasConfig? {
    return when {
        this is CwtPropertyConfig -> this.inlineableConfig?.castOrNull()
        this is CwtValueConfig -> this.propertyConfig?.inlineableConfig?.castOrNull()
        this is CwtAliasConfig -> this
        else -> null
    }
}

inline fun <T: CwtConfig<*>> Collection<T>.sortedByPriority(crossinline expressionExtractor: (T) -> CwtDataExpression): List<T> {
    if(isEmpty()) return emptyList()
    val configGroup = first().info.configGroup
    return sortedByDescending { ParadoxConfigHandler.getPriority(expressionExtractor(it), configGroup) }
}

inline fun <T> Collection<T>.sortedByPriority(configGroup: CwtConfigGroup, crossinline expressionExtractor: (T) -> CwtDataExpression): List<T> {
    if(isEmpty()) return emptyList()
    return sortedByDescending { ParadoxConfigHandler.getPriority(expressionExtractor(it), configGroup) }
}


val CwtProperty.configPath: CwtConfigPath?
    get() = CwtConfigHandler.get(this)

val CwtValue.configPath: CwtConfigPath?
    get() = CwtConfigHandler.get(this)

val CwtProperty.configType: CwtConfigType?
    get() = CwtConfigHandler.getConfigType(this)

val CwtValue.configType: CwtConfigType?
    get() = CwtConfigHandler.getConfigType(this)


fun CwtTemplateExpression.extract(referenceName: String): String {
    return CwtTemplateExpressionHandler.extract(this, referenceName)
}

fun CwtTemplateExpression.extract(referenceNames: Map<CwtDataExpression, String>): String {
    return CwtTemplateExpressionHandler.extract(this, referenceNames)
}

fun CwtTemplateExpression.matches(text: String, element: PsiElement, configGroup: CwtConfigGroup, matchOptions: Int = ParadoxConfigMatcher.Options.Default): Boolean {
    return CwtTemplateExpressionHandler.matches(text, element, this, configGroup, matchOptions)
}

fun CwtTemplateExpression.resolve(text: String, element: ParadoxScriptStringExpressionElement, configGroup: CwtConfigGroup): ParadoxTemplateExpressionElement? {
    return CwtTemplateExpressionHandler.resolve(text, element, this, configGroup)
}

fun CwtTemplateExpression.resolveReferences(text: String, element: ParadoxScriptStringExpressionElement, configGroup: CwtConfigGroup): List<ParadoxTemplateSnippetExpressionReference> {
    return CwtTemplateExpressionHandler.resolveReferences(text, element, this, configGroup)
}

fun CwtTemplateExpression.processResolveResult(element: PsiElement, configGroup: CwtConfigGroup, processor: Processor<String>) {
    CwtTemplateExpressionHandler.processResolveResult(element, this, configGroup, processor)
}

fun <C : CwtConfig<*>> Map<String, C>.getByTemplate(text: String, element: PsiElement, configGroup: CwtConfigGroup, matchOptions: Int = ParadoxConfigMatcher.Options.Default): C? {
    return get(text) ?: entries.find { (k) -> CwtTemplateExpression.resolve(k).matches(text, element, configGroup, matchOptions) }?.value
}