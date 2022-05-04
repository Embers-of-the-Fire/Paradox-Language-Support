package icu.windea.pls.localisation.structureView

import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.*
import com.intellij.openapi.editor.*
import com.intellij.psi.*
import icu.windea.pls.localisation.psi.*

class ParadoxLocalisationStructureViewModel(
	editor: Editor?,
	file: PsiFile
) : TextEditorBasedStructureViewModel(editor, file), StructureViewModel.ElementInfoProvider, StructureViewModel.ExpandInfoProvider {
	companion object {
		private val defaultSuitableClasses = arrayOf(
			ParadoxLocalisationFile::class.java,
			ParadoxLocalisationPropertyList::class.java,
			ParadoxLocalisationProperty::class.java
		)
		private val defaultSorters = arrayOf(Sorter.ALPHA_SORTER)
	}
	
	//指定根节点，一般为psiFile
	override fun getRoot() = ParadoxLocalisationFileTreeElement(psiFile as ParadoxLocalisationFile)
	
	//指定在结构视图中的元素
	override fun getSuitableClasses() = defaultSuitableClasses
	
	//指定可用的排序器，可自定义
	override fun getSorters() = defaultSorters
	
	override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean {
		return element is ParadoxLocalisationFileTreeElement
	}
	
	override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
		return false
	}
	
	override fun isAutoExpand(element: StructureViewTreeElement): Boolean {
		return element is ParadoxLocalisationFileTreeElement || element is ParadoxLocalisationPropertyListTreeElement
	}
	
	override fun isSmartExpand(): Boolean {
		return false
	}
}
