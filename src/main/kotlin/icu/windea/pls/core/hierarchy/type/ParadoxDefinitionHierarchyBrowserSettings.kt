package icu.windea.pls.core.hierarchy.type

import com.intellij.openapi.components.*
import com.intellij.openapi.project.*
import com.intellij.util.xmlb.*
import icu.windea.pls.core.hierarchy.*

@Service(Service.Level.PROJECT)
@State(name = "ParadoxDefinitionHierarchyBrowserSettings", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class ParadoxDefinitionHierarchyBrowserSettings : PersistentStateComponent<ParadoxDefinitionHierarchyBrowserSettings>, ParadoxHierarchyBrowserSettings {
    override var scopeType: String = "all"
    
    override fun getState() = this
    
    override fun loadState(state: ParadoxDefinitionHierarchyBrowserSettings) = XmlSerializerUtil.copyBean(state, this)
    
    companion object {
        @JvmStatic
        fun getInstance(project: Project) = project.service<ParadoxDefinitionHierarchyBrowserSettings>()
    }
}
