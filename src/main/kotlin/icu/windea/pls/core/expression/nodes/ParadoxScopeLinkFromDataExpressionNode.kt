package icu.windea.pls.core.expression.nodes

import com.intellij.openapi.util.*
import com.intellij.util.*
import icu.windea.pls.config.cwt.*
import icu.windea.pls.core.collections.*
import icu.windea.pls.core.expression.errors.*

class ParadoxScopeLinkFromDataExpressionNode (
	override val text: String,
	override val rangeInExpression: TextRange,
	override val nodes: List<ParadoxExpressionNode> = emptyList()
) : ParadoxScopeExpressionNode {
	val prefixNode get() = nodes.findIsInstance<ParadoxScopeLinkPrefixExpressionNode>()
	val dataSourceNode get() = nodes.findIsInstance<ParadoxScopeLinkDataSourceExpressionNode>()!!
	
	companion object Resolver {
		fun resolve(text: String, textRange: TextRange, configGroup: CwtConfigGroup): ParadoxScopeLinkFromDataExpressionNode? {
			val nodes = SmartList<ParadoxExpressionNode>()
			val errors = SmartList<ParadoxExpressionError>()
			val offset = textRange.startOffset
			val linkConfigs = configGroup.linksAsScopeWithPrefixSorted
				.filter { it.prefix != null && text.startsWith(it.prefix) }
			if(linkConfigs.isNotEmpty()) {
				//匹配某一前缀
				//prefix node
				val prefixText = linkConfigs.first().prefix!!
				val prefixRange = TextRange.create(offset, offset + prefixText.length)
				val prefixNode = ParadoxScopeLinkPrefixExpressionNode.resolve(prefixText, prefixRange, linkConfigs)
				nodes.add(prefixNode)
				//data source node
				val dataSourceText = text.drop(prefixText.length)
				val dataSourceRange = TextRange.create(prefixText.length + offset, text.length + offset)
				val dataSourceNode = ParadoxScopeLinkDataSourceExpressionNode.resolve(dataSourceText, dataSourceRange, linkConfigs)
				nodes.add(dataSourceNode)
				return ParadoxScopeLinkFromDataExpressionNode(text, textRange, nodes)
			} else {
				//没有前缀且允许没有前缀
				val linkConfigsNoPrefix = configGroup.linksAsScopeWithoutPrefixSorted
				if(linkConfigsNoPrefix.isNotEmpty()) {
					//这里直接认为匹配
					val node = ParadoxScopeLinkDataSourceExpressionNode.resolve(text, textRange, linkConfigsNoPrefix)
					nodes.add(node)
					return ParadoxScopeLinkFromDataExpressionNode(text, textRange, nodes)
				}
			}
			return null
		}
	}
}
