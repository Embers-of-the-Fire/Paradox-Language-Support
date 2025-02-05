package icu.windea.pls.core.expression

import com.intellij.codeInsight.completion.*
import com.intellij.openapi.util.*
import com.intellij.util.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.codeInsight.completion.*
import icu.windea.pls.core.collections.*
import icu.windea.pls.core.expression.ParadoxValueSetValueExpression.*
import icu.windea.pls.core.expression.errors.*
import icu.windea.pls.core.expression.nodes.*
import icu.windea.pls.core.util.*
import icu.windea.pls.lang.*
import icu.windea.pls.lang.cwt.*
import icu.windea.pls.lang.cwt.config.*

/**
 * 值集值表达式。
 *
 * 语法：
 *
 * ```bnf
 * value_set_value_expression ::= value_set_value ("@" scope_field_expression)?
 * value_set_value ::= TOKEN //matching config expression "value[xxx]" or "value_set[xxx]"
 * //"event_target:t1.v1@event_target:t2.v2@..." is not used in vanilla files but allowed here
 * ```
 *
 * 示例：
 *
 * ```
 * some_variable
 * some_variable@root
 * ```
 */
interface ParadoxValueSetValueExpression : ParadoxComplexExpression {
    val configs: List<CwtConfig<*>>
    
    companion object Resolver
}

val ParadoxValueSetValueExpression.scopeFieldExpression: ParadoxScopeFieldExpression?
    get() = nodes.getOrNull(2)?.cast()
val ParadoxValueSetValueExpression.valueSetValueNode: ParadoxValueSetValueExpressionNode
    get() = nodes.get(0).cast()

class ParadoxValueSetValueExpressionImpl(
    override val text: String,
    override val rangeInExpression: TextRange,
    override val nodes: List<ParadoxExpressionNode>,
    override val configGroup: CwtConfigGroup,
    override val configs: List<CwtConfig<*>>
) : AbstractExpression(text), ParadoxValueSetValueExpression {
    override fun validate(): List<ParadoxExpressionError> {
        val errors = mutableListOf<ParadoxExpressionError>()
        var malformed = false
        for(node in nodes) {
            when(node) {
                is ParadoxValueSetValueExpressionNode -> {
                    if(!malformed && !isValid(node)) {
                        malformed = true
                    }
                }
                is ParadoxScopeFieldExpression -> {
                    if(node.text.isEmpty()) {
                        val error = ParadoxMissingScopeFieldExpressionExpressionError(rangeInExpression, PlsBundle.message("script.expression.missingScopeFieldExpression"))
                        errors.add(error)
                    }
                    errors.addAll(node.validate())
                }
            }
        }
        if(malformed) {
            val error = ParadoxMalformedValueSetValueExpressionExpressionError(rangeInExpression, PlsBundle.message("script.expression.malformedValueSetValueExpression", text))
            errors.add(0, error)
        }
        return errors
    }
    
    private fun isValid(node: ParadoxExpressionNode): Boolean {
        return when(node) {
            is ParadoxValueSetValueExpressionNode -> node.text.isExactParameterAwareIdentifier('.') //兼容点号
            else -> node.text.isExactParameterAwareIdentifier()
        }
    }
    
    override fun complete(context: ProcessingContext, result: CompletionResultSet) {
        val keyword = context.keyword
        val startOffset = context.startOffset
        val isKey = context.isKey
        val config = context.config
        val configs = context.configs
        val scopeContext = context.scopeContext ?: ParadoxScopeHandler.getAnyScopeContext()
        
        context.config = this.configs.first()
        context.configs = this.configs
        context.scopeContext = null //don't check now
        context.isKey = null
        
        val offsetInParent = context.offsetInParent
        for(node in nodes) {
            val nodeRange = node.rangeInExpression
            val inRange = offsetInParent >= nodeRange.startOffset && offsetInParent <= nodeRange.endOffset
            if(node is ParadoxValueSetValueExpressionNode) {
                if(inRange) {
                    val keywordToUse = node.text.substring(0, offsetInParent - nodeRange.startOffset)
                    val resultToUse = result.withPrefixMatcher(keywordToUse)
                    context.keyword = keywordToUse
                    context.startOffset = node.rangeInExpression.startOffset
                    ParadoxConfigHandler.completeValueSetValue(context, resultToUse)
                    break
                }
            } else if(node is ParadoxScopeFieldExpression) {
                if(inRange) {
                    val keywordToUse = node.text.substring(0, offsetInParent - nodeRange.startOffset)
                    val resultToUse = result.withPrefixMatcher(keywordToUse)
                    context.keyword = keywordToUse
                    context.startOffset = node.rangeInExpression.startOffset
                    node.complete(context, resultToUse)
                    break
                }
            }
        }
        
        context.keyword = keyword
        context.startOffset = startOffset
        context.isKey = isKey
        context.config = config
        context.configs = configs
        context.scopeContext = scopeContext
    }
}

fun Resolver.resolve(expression: String, range: TextRange, configGroup: CwtConfigGroup, config: CwtConfig<*>, canBeMismatched: Boolean = false): ParadoxValueSetValueExpression? {
    return resolve(expression, range, configGroup, config.toSingletonList(), canBeMismatched)
}

fun Resolver.resolve(expression: String, range: TextRange, configGroup: CwtConfigGroup, configs: List<CwtConfig<*>>, canBeMismatched: Boolean = false): ParadoxValueSetValueExpression? {
    val parameterRanges = ParadoxConfigHandler.getParameterRangesInExpression(expression)
    
    val nodes = mutableListOf<ParadoxExpressionNode>()
    val offset = range.startOffset
    var index: Int
    var tokenIndex = -1
    val textLength = expression.length
    while(tokenIndex < textLength) {
        index = tokenIndex + 1
        tokenIndex = expression.indexOf('@', index)
        if(tokenIndex != -1 && ParadoxConfigHandler.inParameterRanges(parameterRanges, tokenIndex)) continue //这里需要跳过参数文本
        if(tokenIndex == -1) {
            tokenIndex = textLength
        }
        if(index == tokenIndex && tokenIndex == textLength) break
        //resolve valueSetValueNode
        val nodeText = expression.substring(0, tokenIndex)
        val nodeTextRange = TextRange.create(offset, tokenIndex + offset)
        val node = ParadoxValueSetValueExpressionNode.resolve(nodeText, nodeTextRange, configs, configGroup)
        if(node == null) return null //unexpected
        nodes.add(node)
        if(tokenIndex != textLength) {
            //resolve at token
            val atNode = ParadoxMarkerExpressionNode("@", TextRange.create(tokenIndex + offset, tokenIndex + 1 + offset))
            nodes.add(atNode)
            //resolve scope expression
            val expText = expression.substring(tokenIndex + 1)
            val expTextRange = TextRange.create(tokenIndex + 1 + offset, textLength + offset)
            val expNode = ParadoxScopeFieldExpression.resolve(expText, expTextRange, configGroup, true)!!
            nodes.add(expNode)
        }
        break
    }
    if(!canBeMismatched && nodes.isEmpty()) return null
    return ParadoxValueSetValueExpressionImpl(expression, range, nodes, configGroup, configs)
}

