package icu.windea.pls.lang.model

import java.text.*

enum class ParadoxType(
	val id: kotlin.String,
	val text: kotlin.String
) {
	Unknown("unknown", "(unknown)"),
	Boolean("boolean", "boolean"),
	Int("int", "int"),
	Float("float", "float"),
	String("string", "string"),
	Color("color", "color"),
	Block("block", "block"),
	Parameter("parameter", "parameter"),
	InlineMath("inline_math", "inline math");
	
	override fun toString(): kotlin.String {
		return text
	}
	
	fun isBooleanType(): kotlin.Boolean {
		return this == Boolean
	}
	
	fun isIntType(): kotlin.Boolean {
		return this == Unknown || this == Int || this == Parameter || this == InlineMath
	}
	
	fun isFloatType(): kotlin.Boolean {
		return this == Unknown || this == Int || this == Float || this == Parameter || this == InlineMath
	}
	
	fun isNumberType(): kotlin.Boolean {
		return this == Unknown || this == Int || this == Float || this == Parameter || this == InlineMath
	}
	
	fun isStringType(): kotlin.Boolean {
		return this == Unknown || this == String || this == Parameter
	}
	
	fun isStringLikeType(): kotlin.Boolean {
		return this == Unknown || this == String || this == Parameter || this == Int || this == Float
	}
	
	fun isColorType(): kotlin.Boolean {
		return this == Color
	}
	
	fun isBlockLikeType(): kotlin.Boolean {
		return this == Block || this == Color || this == InlineMath
	}
	
	fun canBeScriptedVariableValue(): kotlin.Boolean {
		return this == Boolean || this == Int || this == Float || this == String
	}
	
	companion object {
		fun resolve(expression: kotlin.String): ParadoxType {
			return when {
				isBooleanYesNo(expression) -> Boolean
				isInt(expression) -> Int
				isFloat(expression) -> Float
				else -> String
			}
		}
		
		fun isBooleanYesNo(expression: kotlin.String): kotlin.Boolean {
			return expression == "yes" || expression == "no"
		}
		
		fun isInt(expression: kotlin.String): kotlin.Boolean {
			return expression.toIntOrNull() != null
		}
		
		fun isFloat(expression: kotlin.String): kotlin.Boolean {
			return expression.toFloatOrNull() != null
		}
		
		private val isPercentageFieldRegex = """[1-9]?[0-9]+%""".toRegex()
		
		fun isPercentageField(expression: kotlin.String): kotlin.Boolean {
			return expression.matches(isPercentageFieldRegex)
		}
		
		private val isColorFieldRegex = """(?:rgb|rgba|hsb|hsv|hsl)[ \t]*\{[\d. \t]*}""".toRegex()
		
		fun isColorField(expression: kotlin.String): kotlin.Boolean {
			return expression.matches(isColorFieldRegex)
		}
		
		private val threadLocalDateFormat by lazy { ThreadLocal.withInitial { SimpleDateFormat("yyyy.MM.dd") } }
		
		fun isDateField(expression: kotlin.String): kotlin.Boolean {
			return try {
				threadLocalDateFormat.get().parse(expression)
				true
			} catch(e: Exception) {
				false
			}
		}
	}
}
