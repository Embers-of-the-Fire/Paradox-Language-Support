package icu.windea.pls.core.codeInsight.generation

import com.intellij.codeInsight.generation.*
import com.intellij.icons.*
import com.intellij.ide.util.*
import com.intellij.openapi.project.*
import icons.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.model.codeInsight.*
import icu.windea.pls.model.codeInsight.ParadoxLocalisationCodeInsightContext.*
import java.awt.event.*
import javax.swing.*

class ParadoxGenerateLocalisationsChooser(
    elements: Array<out Localisation>,
    project: Project
) : MemberChooser<ParadoxGenerateLocalisationsChooser.Localisation>(elements, true, true, project, null, emptyArray()) {
    override fun isContainerNode(key: MemberChooserObject): Boolean {
        return key !is Localisation
    }
    
    override fun getAllContainersNodeName(): String {
        return PlsBundle.message("generation.localisation.allContainersNodeName")
    }
    
    override fun getShowContainersAction() = ShowContainersAction(
        PlsBundle.lazyMessage("generation.localisation.showContainers"),
        AllIcons.Actions.GroupBy
    )
    
    override fun createSouthPanel(): JComponent {
        //let left side actions actually at left side of chooser dialog
        val superPanel = super.createSouthPanel()
        val finalPanel = superPanel.components.lastOrNull()?.castOrNull<JPanel>()
        return finalPanel ?: superPanel
    }
    
    override fun createLeftSideActions(): Array<Action> {
        return buildList<Action> {
            this += SelectAction(PlsBundle.message("generation.localisation.select.All")) { selectElements(myElements) }
            this += SelectAction(PlsBundle.message("generation.localisation.select.missing")) { selectElements(myElements.filter { it.info.missing }.toTypedArray()) }
            this += SelectAction(PlsBundle.message("generation.localisation.select.missingAndChecked")) { selectElements(myElements.filter { it.info.missing && it.info.check }.toTypedArray()) }
        }.toTypedArray()
    }
    
    private class SelectAction(
        actionName: String,
        private val action: (ActionEvent) -> Unit
    ) : AbstractAction(actionName) {
        override fun actionPerformed(e: ActionEvent) {
            action(e)
        }
    }
    
    data class Localisation(
        val name: String,
        val info: ParadoxLocalisationCodeInsightInfo,
        val context: ParadoxLocalisationCodeInsightContext
    ) : MemberChooserObjectBase(name, PlsIcons.Localisation), ClassMember {
        override fun getParentNodeDelegate(): MemberChooserObject {
            return when(context.type) {
                Type.File -> Unresolved(context)
                Type.Definition -> Definition(context.name, context)
                Type.Modifier -> Modifier(context.name, context)
            }
        }
        
        override fun equals(other: Any?) = this === other || (other is Localisation && name == other.name)
        
        override fun hashCode() = name.hashCode()
    }
    
    data class Definition(
        val name: String,
        val context: ParadoxLocalisationCodeInsightContext
    ) : MemberChooserObjectBase(name, PlsIcons.Definition) {
        override fun equals(other: Any?) = this === other || (other is Definition && name == other.name)
        
        override fun hashCode() = name.hashCode()
    }
    
    data class Modifier(
        val name: String,
        val context: ParadoxLocalisationCodeInsightContext
    ) : MemberChooserObjectBase(name, PlsIcons.Modifier) {
        override fun equals(other: Any?) = this === other || (other is Modifier && name == other.name)
        
        override fun hashCode() = name.hashCode()
    }
    
    data class Unresolved(
        val context: ParadoxLocalisationCodeInsightContext
    ) : MemberChooserObjectBase(PlsBundle.message("generation.localisation.unresolved"))
}