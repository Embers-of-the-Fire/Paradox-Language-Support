package icu.windea.pls.core.expression

import com.intellij.codeInsight.completion.*
import com.intellij.openapi.util.*
import com.intellij.util.*
import icu.windea.pls.core.*
import icu.windea.pls.core.expression.ParadoxComplexExpression.*
import icu.windea.pls.core.expression.errors.*
import icu.windea.pls.core.expression.nodes.*
import icu.windea.pls.lang.cwt.*
import icu.windea.pls.lang.cwt.config.*

/**
 * 用于表达式脚本语言中的复杂表达式，对应匹配特定CWT规则类型的key或value（或者它们的特定部分）。
 * 目前认为不能用引号括起。
 */
interface ParadoxComplexExpression : ParadoxExpressionNode {
    val configGroup: CwtConfigGroup
    
    fun validate(): List<ParadoxExpressionError> = emptyList()
    
    fun complete(context: ProcessingContext, result: CompletionResultSet) = pass()
    
    companion object Resolver
}

fun Resolver.resolve(expression: String, range: TextRange, configGroup: CwtConfigGroup, config: CwtConfig<*>, canBeMismatched: Boolean = false): ParadoxComplexExpression? {
    val dataType = config.expression?.type ?: return null
    return when {
        dataType.isValueSetValueType() -> ParadoxValueSetValueExpression.resolve(expression, range, configGroup, config, canBeMismatched)
        dataType.isScopeFieldType() -> ParadoxScopeFieldExpression.resolve(expression, range, configGroup, canBeMismatched)
        dataType.isValueFieldType() -> ParadoxValueFieldExpression.resolve(expression, range, configGroup, canBeMismatched)
        dataType.isVariableFieldType() -> ParadoxVariableFieldExpression.resolve(expression, range, configGroup, canBeMismatched)
        else -> null
    }
}
