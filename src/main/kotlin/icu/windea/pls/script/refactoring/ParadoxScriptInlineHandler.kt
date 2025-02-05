package icu.windea.pls.script.refactoring

import com.intellij.lang.refactoring.*
import com.intellij.openapi.editor.*
import com.intellij.psi.*
import icu.windea.pls.script.psi.*

class ParadoxScriptInlineHandler: InlineHandler {
	override fun createInliner(element: PsiElement, settings: InlineHandler.Settings): InlineHandler.Inliner? {
		return when{
			element is ParadoxScriptScriptedVariable -> null
			element is ParadoxScriptScriptedVariableReference -> null
			else -> null
		}
	}

	override fun removeDefinition(element: PsiElement, settings: InlineHandler.Settings) {

	}

	override fun prepareInlineElement(element: PsiElement, editor: Editor?, invokedOnReference: Boolean): InlineHandler.Settings? {
		return null
	}
}
