package icu.windea.pls.core.navigation

import com.intellij.navigation.*
import icu.windea.pls.core.psi.*
import javax.swing.*

class ParadoxParameterElementPresentation(
	private val element: ParadoxParameterElement
): ItemPresentation{
	override fun getIcon(unused: Boolean): Icon {
		return element.icon
	}
	
	override fun getPresentableText(): String {
		return element.name
	}
}

class ParadoxLocalisationParameterElementPresentation(
	private val element: ParadoxLocalisationParameterElement
): ItemPresentation{
	override fun getIcon(unused: Boolean): Icon {
		return element.icon
	}
	
	override fun getPresentableText(): String {
		return element.name
	}
}

class ParadoxValueSetValueElementPresentation(
	private val element: ParadoxValueSetValueElement
): ItemPresentation{
	override fun getIcon(unused: Boolean): Icon {
		return element.icon
	}
	
	override fun getPresentableText(): String {
		return element.name
	}
}

class ParadoxComplexEnumValueElementPresentation(
	private val element: ParadoxComplexEnumValueElement
): ItemPresentation{
	override fun getIcon(unused: Boolean): Icon {
		return element.icon
	}
	
	override fun getPresentableText(): String {
		return element.name
	}
}

class ParadoxModifierElementPresentation(
	private val element: ParadoxModifierElement
): ItemPresentation{
	override fun getIcon(unused: Boolean): Icon {
		return element.icon
	}
	
	override fun getPresentableText(): String {
		return element.name
	}
}

class ParadoxTemplateExpressionElementPresentation(
	private val element: ParadoxTemplateExpressionElement
): ItemPresentation{
	override fun getIcon(unused: Boolean): Icon {
		return element.icon
	}
	
	override fun getPresentableText(): String {
		return element.name
	}
}