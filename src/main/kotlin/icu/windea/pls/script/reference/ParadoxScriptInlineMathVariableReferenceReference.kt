package icu.windea.pls.script.reference

import com.intellij.openapi.util.*
import icu.windea.pls.script.psi.*

class ParadoxScriptInlineMathVariableReferenceReference(
	element: ParadoxScriptInlineMathVariableReference,
	rangeInElement: TextRange
) : AbstractParadoxScriptVariableReferenceReference(element, rangeInElement) {
	override fun String.toReferenceName(): String {
		return this.trimStart('@')
	}
}