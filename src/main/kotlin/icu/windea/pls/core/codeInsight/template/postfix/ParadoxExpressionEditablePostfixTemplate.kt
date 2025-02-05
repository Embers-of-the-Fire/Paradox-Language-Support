package icu.windea.pls.core.codeInsight.template.postfix

import com.intellij.codeInsight.template.*
import com.intellij.codeInsight.template.impl.*
import com.intellij.codeInsight.template.postfix.templates.*
import com.intellij.codeInsight.template.postfix.templates.editable.*
import com.intellij.psi.*
import icu.windea.pls.core.*
import icu.windea.pls.core.annotations.*
import icu.windea.pls.lang.cwt.setting.*

@WithCwtSetting("postfix_template_settings.pls.cwt", CwtPostfixTemplateSetting::class)
abstract class ParadoxExpressionEditablePostfixTemplate(
	val setting: CwtPostfixTemplateSetting,
	provider: PostfixTemplateProvider
): EditablePostfixTemplate(setting.id, setting.key, createTemplate(setting), setting.example.orEmpty(), provider) {
	abstract val groupName: String
	
	override fun isBuiltin(): Boolean {
		return true
	}
	
	override fun addTemplateVariables(element: PsiElement, template: Template) {
		val variables = setting.variables
		if(variables.isEmpty()) return
		for(variable in variables) {
			template.addVariable(variable.key, "", variable.value.quote(), true)
		}
	}
	
	override fun equals(other: Any?): Boolean {
		return this === other || (other is ParadoxExpressionEditablePostfixTemplate && setting == other.setting) 
	}
	
	override fun hashCode(): Int {
		return setting.hashCode()
	}
}

private fun createTemplate(setting: CwtPostfixTemplateSetting): TemplateImpl {
	val template = TemplateImpl("fakeKey", setting.expression, "")
	template.isToReformat = true
	template.parseSegments()
	return template
}