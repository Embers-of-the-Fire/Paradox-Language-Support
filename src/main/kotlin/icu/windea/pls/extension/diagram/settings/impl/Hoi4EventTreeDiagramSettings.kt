package icu.windea.pls.extension.diagram.settings.impl

import com.intellij.openapi.components.*
import com.intellij.openapi.options.*
import com.intellij.openapi.project.*
import com.intellij.util.xmlb.annotations.*
import icu.windea.pls.core.annotations.*
import icu.windea.pls.core.collections.*
import icu.windea.pls.extension.diagram.settings.*
import icu.windea.pls.lang.*
import icu.windea.pls.model.*

@WithGameType(ParadoxGameType.Hoi4)
@Service(Service.Level.PROJECT)
@State(name = "ParadoxDiagramSettings.Hoi4.EventTree", storages = [Storage("paradox-language-support.xml")])
class Hoi4EventTreeDiagramSettings(
    val project: Project
) : ParadoxEventTreeDiagramSettings<Hoi4EventTreeDiagramSettings.State>(State()) {
    companion object {
        const val ID = "pls.diagram.Hoi4.EventTree"
    }
    
    override val id: String = ID
    override val configurableClass: Class<out Configurable> = Hoi4EventTreeDiagramSettingsConfigurable::class.java
    
    class State : ParadoxDiagramSettings.State() {
        override var scopeType by string()
        
        @get:XMap
        var type by linkedMap<String, Boolean>()
        @get:XMap
        var eventType by linkedMap<String, Boolean>()
        
        val typeSettings = TypeSettings()
        
        inner class TypeSettings {
            val hidden  by type withDefault true
        }
    }
    
    override fun initSettings() {
        val eventTypes = ParadoxEventHandler.getTypes(project, ParadoxGameType.Hoi4)
        eventTypes.forEach { state.eventType.putIfAbsent(it, true) }
    }
}