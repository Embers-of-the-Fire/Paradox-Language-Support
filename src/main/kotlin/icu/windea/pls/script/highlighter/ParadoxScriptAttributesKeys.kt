package icu.windea.pls.script.highlighter

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*
import com.intellij.openapi.editor.HighlighterColors.*
import com.intellij.openapi.editor.colors.TextAttributesKey.*
import com.intellij.openapi.editor.markup.*
import icu.windea.pls.*
import icu.windea.pls.localisation.highlighter.*
import java.awt.Color

object ParadoxScriptAttributesKeys {
	@JvmField val SEPARATOR_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.separator"), OPERATION_SIGN)
	@JvmField val BRACES_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.braces"), BRACES)
	@JvmField val VARIABLE_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.variable"), STATIC_FIELD)
	@JvmField val PROPERTY_KEY_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.propertyKey"), INSTANCE_FIELD)
	@JvmField val KEYWORD_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.keyword"), KEYWORD)
	@JvmField val COLOR_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.color"), FUNCTION_DECLARATION)
	@JvmField val NUMBER_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.number"), NUMBER)
	@JvmField val STRING_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.string"), STRING)
	@JvmField val CODE_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.code"), IDENTIFIER)
	@JvmField val COMMENT_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.comment"), LINE_COMMENT)
	@JvmField val VALID_ESCAPE_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.validEscape"), VALID_STRING_ESCAPE)
	@JvmField val INVALID_ESCAPE_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.invalidEscape"), INVALID_STRING_ESCAPE)
	@JvmField val BAD_CHARACTER_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.badCharacter"), BAD_CHARACTER)
	@JvmField val DEFINITION_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.definition"), PROPERTY_KEY_KEY).apply {
		//defaultAttributes.effectType = EffectType.LINE_UNDERSCORE
		//defaultAttributes.effectColor = defaultAttributes.foregroundColor
	}
	@JvmField val DEFINITION_REFERENCE_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.definitionReference"), DEFINITION_KEY).apply {
		//defaultAttributes.effectType = EffectType.LINE_UNDERSCORE
		//defaultAttributes.effectColor = defaultAttributes.foregroundColor
	}
	@JvmField val LOCALISATION_REFERENCE_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.localisationReference"), ParadoxLocalisationAttributesKeys.LOCALISATION_KEY).apply {
		//defaultAttributes.effectType = EffectType.LINE_UNDERSCORE
		//defaultAttributes.effectColor = defaultAttributes.foregroundColor
	}
	@JvmField val SYNCED_LOCALISATION_REFERENCE_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.syncedLocalisationReference"), ParadoxLocalisationAttributesKeys.SYNCED_LOCALISATION_KEY).apply {
		//defaultAttributes.effectType = EffectType.LINE_UNDERSCORE
		//defaultAttributes.effectColor = defaultAttributes.foregroundColor
	}
	@JvmField val ENUM_REFERENCE_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.enumReference"), STATIC_FIELD)
	@JvmField val PATH_REFERENCE_KEY = createTextAttributesKey(PlsBundle.message("script.externalName.pathReference"), STRING_KEY).apply {
		//defaultAttributes.effectType = EffectType.LINE_UNDERSCORE
		//defaultAttributes.effectColor = Color(0x707D95)
	}
}
