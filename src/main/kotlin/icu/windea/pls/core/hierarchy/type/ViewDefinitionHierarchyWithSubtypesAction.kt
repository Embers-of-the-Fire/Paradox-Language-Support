package icu.windea.pls.core.hierarchy.type

import com.intellij.ide.hierarchy.*
import icons.*
import icu.windea.pls.*

class ViewDefinitionHierarchyWithSubtypesAction: ChangeHierarchyViewActionBase(
    PlsBundle.message("action.view.definition.hierarchy.with.subtypes"),
    PlsBundle.message("action.description.view.definition.hierarchy.with.subtypes"),
    PlsIcons.Hierarchy.Definition
) {
    override fun getTypeName(): String {
        return ParadoxDefinitionHierarchyBrowser.getDefinitionHierarchyTypeWithSubtypes()
    }
}