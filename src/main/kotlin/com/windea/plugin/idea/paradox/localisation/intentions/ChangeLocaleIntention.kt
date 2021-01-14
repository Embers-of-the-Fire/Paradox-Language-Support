package com.windea.plugin.idea.paradox.localisation.intentions

import com.intellij.codeInsight.intention.*
import com.intellij.openapi.command.WriteCommandAction.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.project.*
import com.intellij.openapi.ui.popup.*
import com.intellij.openapi.ui.popup.util.*
import com.intellij.psi.*
import com.windea.plugin.idea.paradox.*
import com.windea.plugin.idea.paradox.localisation.psi.*
import com.windea.plugin.idea.paradox.localisation.psi.ParadoxLocalisationElementFactory.createLocale
import javax.swing.*

object ChangeLocaleIntention : IntentionAction {
	private val _name = message("paradox.localisation.intention.changeLocale")
	private val _title = message("paradox.localisation.intention.changeLocale.title")
	
	override fun startInWriteAction() = false
	
	override fun getText() = _name
	
	override fun getFamilyName() = _name
	
	override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
		if(editor == null || file == null) return false
		val element = file.findElementAt(editor.caretModel.offset)?.parent
		return element is ParadoxLocalisationLocale
	}
	
	override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
		if(editor == null || file == null) return
		val element = file.findElementAt(editor.caretModel.offset)?.parent
		if(element is ParadoxLocalisationLocale) {
			JBPopupFactory.getInstance().createListPopup(Popup(element, paradoxLocales)).showInBestPositionFor(editor)
		}
	}
	
	private class Popup(
		private val value: ParadoxLocalisationLocale,
		values: Array<ParadoxLocale>
	) : BaseListPopupStep<ParadoxLocale>(_title, *values) {
		override fun getTextFor(value: ParadoxLocale) = value.popupText
		
		override fun getIconFor(value: ParadoxLocale?) = localisationLocaleIcon
		
		override fun getDefaultOptionIndex() = 0
		
		override fun isSpeedSearchEnabled(): Boolean = true
		
		override fun onChosen(selectedValue: ParadoxLocale, finalChoice: Boolean): PopupStep<*>? {
			//需要在WriteCommandAction里面执行
			runWriteCommandAction(value.project) { value.name = selectedValue.name }
			return PopupStep.FINAL_CHOICE
		}
	}
}

