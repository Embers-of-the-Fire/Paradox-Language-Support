package icu.windea.pls.core.expression.nodes

import com.intellij.openapi.util.*
import icu.windea.pls.core.expression.*
import icu.windea.pls.lang.*
import icu.windea.pls.lang.cwt.config.*
import icu.windea.pls.script.highlighter.*

class ParadoxValueLinkDataSourceExpressionNode(
    override val text: String,
    override val rangeInExpression: TextRange,
    val linkConfigs: List<CwtLinkConfig>,
    override val nodes: List<ParadoxExpressionNode>
) : ParadoxExpressionNode {
    override fun getAttributesKey() = ParadoxScriptAttributesKeys.VALUE_LINK_DATA_SOURCE_KEY
    
    companion object Resolver {
        fun resolve(text: String, textRange: TextRange, linkConfigs: List<CwtLinkConfig>): ParadoxValueLinkDataSourceExpressionNode {
            val parameterRanges = ParadoxConfigHandler.getParameterRangesInExpression(text)
            
            //text may contain parameters
            //child node can be valueSetValueExpression / scriptValueExpression
            val nodes = mutableListOf<ParadoxExpressionNode>()
            run {
                val configs = linkConfigs.filter { it.dataSource?.type?.isValueSetValueType() == true }
                if(configs.isNotEmpty()) {
                    val configGroup = linkConfigs.first().info.configGroup
                    val node = ParadoxValueSetValueExpression.resolve(text, textRange, configGroup, configs)!!
                    nodes.add(node)
                }
            }
            run {
                if(nodes.isNotEmpty()) return@run
                val offset = textRange.startOffset
                var index: Int
                var tokenIndex = -1
                val textLength = text.length
                while(tokenIndex < textLength) {
                    index = tokenIndex + 1
                    tokenIndex = text.indexOf('|', index)
                    if(tokenIndex != -1 && ParadoxConfigHandler.inParameterRanges(parameterRanges, tokenIndex)) continue //这里需要跳过参数文本
                    if(tokenIndex == -1) break
                    val scriptValueConfig = linkConfigs.find { it.name == "script_value" }
                    if(scriptValueConfig == null) {
                        val dataText = text.substring(0, tokenIndex)
                        val dataRange = TextRange.create(offset, tokenIndex + offset)
                        val dataNode = ParadoxDataExpressionNode.resolve(dataText, dataRange, linkConfigs)
                        nodes.add(dataNode)
                        val errorText = text.substring(tokenIndex)
                        val errorRange = TextRange.create(tokenIndex + offset, text.length + offset)
                        val errorNode = ParadoxErrorTokenExpressionNode(errorText, errorRange)
                        nodes.add(errorNode)
                    } else {
                        val configGroup = linkConfigs.first().info.configGroup
                        val node = ParadoxScriptValueExpression.resolve(text, textRange, configGroup, scriptValueConfig)
                        nodes.add(node)
                    }
                    break
                }
            }
            run {
                if(nodes.isNotEmpty()) return@run
                val node = ParadoxDataExpressionNode.resolve(text, textRange, linkConfigs)
                nodes.add(node)
            }
            return ParadoxValueLinkDataSourceExpressionNode(text, textRange, linkConfigs, nodes)
        }
    }
}
