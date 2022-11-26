package icu.windea.pls.core.expression.nodes

import com.intellij.openapi.util.*
import icu.windea.pls.config.cwt.*

interface ParadoxValueFieldExpressionNode : ParadoxExpressionNode {
	companion object Resolver {
		fun resolve(text: String, textRange: TextRange, configGroup: CwtConfigGroup): ParadoxValueFieldExpressionNode {
			ParadoxValueLinkExpressionNode.resolve(text, textRange, configGroup)?.let { return it }
			ParadoxValueLinkFromDataExpressionNode.resolve(text, textRange, configGroup)?.let { return it }
			return ParadoxErrorValueFieldExpressionNode(text, textRange)
		}
	}
}

