package icu.windea.pls.script.codeStyle


import com.intellij.application.options.*
import com.intellij.psi.codeStyle.*
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.*
import icu.windea.pls.*
import icu.windea.pls.script.*
import icu.windea.pls.script.codeStyle.ParadoxScriptCodeStyleSettings.*

class ParadoxScriptLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
	override fun getLanguage() = ParadoxScriptLanguage
	
	override fun getCodeSample(settingsType: SettingsType) = paradoxScriptDemoText
	
	override fun createCustomSettings(settings: CodeStyleSettings) = ParadoxScriptCodeStyleSettings(settings)

	//需要重载这个方法以显示indentOptions设置页面
	override fun getIndentOptionsEditor() = IndentOptionsEditor(this)

	override fun customizeDefaults(commonSettings: CommonCodeStyleSettings, indentOptions: CommonCodeStyleSettings.IndentOptions) {
		indentOptions.INDENT_SIZE = 4
		indentOptions.CONTINUATION_INDENT_SIZE = 4
		indentOptions.KEEP_INDENTS_ON_EMPTY_LINES = true
		indentOptions.USE_TAB_CHARACTER = true
		commonSettings.LINE_COMMENT_AT_FIRST_COLUMN = false
		commonSettings.LINE_COMMENT_ADD_SPACE = false
	}

	override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
		when(settingsType) {
			SettingsType.INDENT_SETTINGS -> customizeIndentSettings(consumer)
			SettingsType.SPACING_SETTINGS -> customizeSpacingSettings(consumer)
			SettingsType.COMMENTER_SETTINGS -> customizeCommenterSettings(consumer)
			else -> pass()
		}
	}
	
	private fun customizeIndentSettings(consumer: CodeStyleSettingsCustomizable) {
		consumer.showStandardOptions(
			IndentOption.INDENT_SIZE.name,
			IndentOption.CONTINUATION_INDENT_SIZE.name,
			IndentOption.KEEP_INDENTS_ON_EMPTY_LINES.name,
			IndentOption.USE_TAB_CHARACTER.name
		)
	}
	
	private fun customizeSpacingSettings(consumer: CodeStyleSettingsCustomizable) {
		val spacesAroundOperators = CodeStyleSettingsCustomizableOptions.getInstance().SPACES_AROUND_OPERATORS
		consumer.showCustomOption(ParadoxScriptCodeStyleSettings::class.java, "SPACE_AROUND_VARIABLE_SEPARATOR", PlsBundle.message("script.codeStyleSettings.spacing.around.variableSeparator"), spacesAroundOperators)
		consumer.showCustomOption(ParadoxScriptCodeStyleSettings::class.java, "SPACE_AROUND_PROPERTY_SEPARATOR", PlsBundle.message("script.codeStyleSettings.spacing.around.propertySeparator"), spacesAroundOperators)
		consumer.showCustomOption(ParadoxScriptCodeStyleSettings::class.java, "SPACE_AROUND_INLINE_MATH_OPERATOR", PlsBundle.message("script.codeStyleSettings.spacing.around.inlineMathOperator"), spacesAroundOperators)
		
		val spacesWithin = CodeStyleSettingsCustomizableOptions.getInstance().SPACES_WITHIN
		consumer.showCustomOption(ParadoxScriptCodeStyleSettings::class.java, "SPACE_WITHIN_BRACES", PlsBundle.message("script.codeStyleSettings.spacing.withIn.braces"), spacesWithin)
		consumer.showCustomOption(ParadoxScriptCodeStyleSettings::class.java, "SPACE_WITHIN_INLINE_MATH_BRACKETS", PlsBundle.message("script.codeStyleSettings.spacing.withIn.inlineMathBrackets"), spacesWithin)
	}
	
	private fun customizeCommenterSettings(consumer: CodeStyleSettingsCustomizable) {
		consumer.showStandardOptions(
			CommenterOption.LINE_COMMENT_AT_FIRST_COLUMN.name,
			CommenterOption.LINE_COMMENT_ADD_SPACE.name
		)
	}
	
	class IndentOptionsEditor(
		provider: LanguageCodeStyleSettingsProvider
	) : SmartIndentOptionsEditor(provider)
}
