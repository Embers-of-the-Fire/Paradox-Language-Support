package icu.windea.pls.script.refactoring

import com.intellij.patterns.*
import com.intellij.psi.*
import com.intellij.refactoring.rename.*
import com.intellij.util.*
import icu.windea.pls.core.*
import icu.windea.pls.core.psi.*

class ParadoxScriptParameterRenameInputValidator : RenameInputValidator {
	companion object {
		private val elementPattern = PlatformPatterns.psiElement(ParadoxParameter::class.java)
	}
	
	override fun isInputValid(newName: String, element: PsiElement, context: ProcessingContext): Boolean {
		return PlsPatterns.scriptParameterNameRegex.matches(newName)
	}
	
	override fun getPattern(): ElementPattern<out PsiElement> {
		return elementPattern
	}
}
