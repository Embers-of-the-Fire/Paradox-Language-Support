package icu.windea.pls.cwt.structureView

import com.intellij.ide.structureView.*
import com.intellij.ide.structureView.impl.common.*
import com.intellij.util.*
import icu.windea.pls.core.*
import icu.windea.pls.cwt.psi.*

class CwtPropertyTreeElement(
	element: CwtProperty
) : PsiTreeElementBase<CwtProperty>(element) {
	override fun getChildrenBase(): Collection<StructureViewTreeElement> {
		val element = element ?: return emptyList()
		val value = element.propertyValue ?: return emptyList()
		if(value !is CwtBlock) return emptyList()
		//允许混合value和property
		val result: MutableList<StructureViewTreeElement> = SmartList()
		value.forEachChild { 
			when{
				it is CwtValue -> result.add(CwtValueTreeElement(it))
				it is CwtProperty -> result.add(CwtPropertyTreeElement(it))
			}
		}
		return result
	}
	
	override fun getPresentableText(): String? {
		val element = element ?: return null
		return element.name
	}
}

