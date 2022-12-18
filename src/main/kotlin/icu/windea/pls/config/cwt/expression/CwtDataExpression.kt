package icu.windea.pls.config.cwt.expression

import icu.windea.pls.config.cwt.config.*
import icu.windea.pls.core.*

/**
 * @property type 表达式类型，即CWT规则的dataType。
 */
interface CwtDataExpression : CwtExpression {
	val type: CwtDataType
	val value: String?
	val extraValue: Any?
}

inline fun <reified T> CwtDataExpression.extraValue() = extraValue?.cast<T>()

fun CwtDataExpression.isNumberType(): Boolean {
	return type == CwtDataTypes.Int || type == CwtDataTypes.Float
		|| type == CwtDataTypes.ValueField || type == CwtDataTypes.IntValueField
		|| type == CwtDataTypes.VariableField || type == CwtDataTypes.VariableField
}

fun <T: CwtDataExpression> T.registerTo(info: CwtConfigGroupInfo): T {
	when(this.type) {
		CwtDataTypes.FilePath -> {
			this.value?.let { info.filePathExpressions.add(it) }
		}
		CwtDataTypes.Icon -> {
			this.value?.let { info.iconPathExpressions.add(it) }
		}
		else -> pass()
	}
	return this
}