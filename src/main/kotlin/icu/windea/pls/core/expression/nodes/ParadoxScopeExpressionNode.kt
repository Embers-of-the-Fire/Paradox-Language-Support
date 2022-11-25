package icu.windea.pls.core.expression.nodes

import com.intellij.openapi.util.*
import icu.windea.pls.config.cwt.*

interface ParadoxScopeExpressionNode: ParadoxExpressionNode{
	companion object Resolver {
		fun resolve(text: String, textRange: TextRange, configGroup: CwtConfigGroup): ParadoxScopeExpressionNode {
			ParadoxSystemScopeExpressionNode.resolve(text, textRange, configGroup)?.let { return it }
			ParadoxScopeLinkExpressionNode.resolve(text, textRange, configGroup)?.let {return it }
			ParadoxScopeLinkFromDataExpressionNode.resolve(text, textRange, configGroup)?.let {return it }
			return ParadoxDummyScopeExpressionNode(text, textRange)
		}
	}
}

