package icu.windea.pls.script.expression

import icu.windea.pls.config.cwt.*
import icu.windea.pls.core.expression.*
import icu.windea.pls.script.exp.*

/**
 * 脚本表达式。
 */
interface ParadoxScriptExpression : Expression {
	val value: String get() = expressionString
	val quoted: Boolean
	val type: ParadoxDataType
	val isKey: Boolean?
	
	companion object {
		@Deprecated("")
		fun resolveValueField(expressionString: String, configGroup: CwtConfigGroup): ParadoxScriptValueFieldExpression {
			return ParadoxScriptValueFieldExpression.resolve(expressionString, configGroup)
		}
	}
}
