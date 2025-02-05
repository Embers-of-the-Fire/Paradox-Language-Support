package icu.windea.pls.script.intentions

import com.intellij.codeInsight.intention.*
import com.intellij.codeInsight.intention.preview.*
import com.intellij.codeInsight.navigation.*
import com.intellij.codeInsight.navigation.actions.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.project.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.psi.*
import icu.windea.pls.core.search.*
import icu.windea.pls.core.search.selector.*
import icu.windea.pls.model.*
import icu.windea.pls.script.psi.*

abstract class DefinitionNameIntention : IntentionAction, PriorityAction, Iconable {
	override fun getIcon(flags: Int) = null
	
	override fun getPriority() = PriorityAction.Priority.HIGH
	
	override fun getFamilyName() = text
	
	override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
		if(editor == null || file == null) return false
		val offset = editor.caretModel.offset
		val element = findElement(file, offset) ?: return false
		return element.isDefinitionName()
	}
	
	override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
		if(editor == null || file == null) return
		val offset = editor.caretModel.offset
		val element = findElement(file, offset) ?: return
		val definition = element.findParentDefinition() ?: return //unexpected
		val definitionInfo = definition.definitionInfo ?: return //unexpected
		if(element.value != definitionInfo.name) return //unexpected
		doInvoke(definition, definitionInfo, editor, project)
	}
	
	private fun findElement(file: PsiFile, offset: Int): ParadoxScriptString? {
		return ParadoxPsiFinder.findScriptExpression(file, offset).castOrNull()
	}
	
	abstract fun doInvoke(definition: ParadoxScriptDefinitionElement, definitionInfo: ParadoxDefinitionInfo, editor: Editor, project: Project)
	
	override fun generatePreview(project: Project, editor: Editor, file: PsiFile) = IntentionPreviewInfo.EMPTY
	
	override fun startInWriteAction() = false
}

/**
 * 为表示定义名称的字符串提供查找使用的功能。
 * @see icu.windea.pls.core.search.usage.ParadoxDefinitionUsagesSearcher
 */
class DefinitionNameFindUsagesIntention : DefinitionNameIntention() {
	override fun getText() = PlsBundle.message("script.intention.definitionName.findUsages")
	
	override fun doInvoke(definition: ParadoxScriptDefinitionElement, definitionInfo: ParadoxDefinitionInfo, editor: Editor, project: Project) {
		GotoDeclarationAction.startFindUsages(editor, project, definition)
	}
}

/**
 * 为表示定义名称的字符串提供导航到实现的功能。
 * @see icu.windea.pls.core.search.implementation.ParadoxDefinitionImplementationsSearch
 */
class DefinitionNameGotoImplementationsIntention: DefinitionNameIntention() {
	override fun getText() = PlsBundle.message("script.intention.definitionName.gotoImplementations")
	
	override fun doInvoke(definition: ParadoxScriptDefinitionElement, definitionInfo: ParadoxDefinitionInfo, editor: Editor, project: Project) {
		val selector = definitionSelector(project, definition).contextSensitive()
		val result = ParadoxDefinitionSearch.search(definitionInfo.name, definitionInfo.type, selector).findAll()
		if(result.isEmpty()) return
		getPsiElementPopup(result.toTypedArray(), PlsBundle.message("script.intention.definitionName.gotoImplementations.title", definitionInfo.name))
			.showInBestPositionFor(editor)
	}
}

/**
 * 为表示定义名称的字符串提供导航到声明的功能。
 * @see icu.windea.pls.core.codeInsight.ParadoxTypeDeclarationProvider
 */
class DefinitionNameGotoTypeDeclarationIntention : DefinitionNameIntention() {
	override fun getText() = PlsBundle.message("script.intention.definitionName.gotoTypeDeclaration")
	
	override fun doInvoke(definition: ParadoxScriptDefinitionElement, definitionInfo: ParadoxDefinitionInfo, editor: Editor, project: Project) {
		val result = mutableListOf<PsiElement>()
		definitionInfo.typeConfig.pointer.element?.let { result.add(it) }
		if(result.isEmpty()) return
		definitionInfo.subtypeConfigs.forEach { config -> config.pointer.element?.let { result.add(it) } }
		getPsiElementPopup(result.toTypedArray(),  PlsBundle.message("script.intention.definitionName.gotoTypeDeclaration.title", definitionInfo.name))
			.showInBestPositionFor(editor)
	}
}
