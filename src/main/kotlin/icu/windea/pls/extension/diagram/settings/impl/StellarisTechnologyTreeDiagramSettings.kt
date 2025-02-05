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

@WithGameType(ParadoxGameType.Stellaris)
@Service(Service.Level.PROJECT)
@State(name = "ParadoxDiagramSettings.Stellaris.TechnologyTree", storages = [Storage("paradox-language-support.xml")])
class StellarisTechnologyTreeDiagramSettings(
    val project: Project
) : ParadoxTechnologyTreeDiagramSettings<StellarisTechnologyTreeDiagramSettings.State>(State()) {
    companion object {
        const val ID = "pls.diagram.Stellaris.TechnologyTree"
    }
    
    override val id: String = ID
    override val configurableClass: Class<out Configurable> = StellarisTechnologyTreeDiagramSettingsConfigurable::class.java
    
    class State : ParadoxDiagramSettings.State() {
        override var scopeType by string()
        
        @get:XMap
        var type by linkedMap<String, Boolean>()
        @get:XMap
        var tier by linkedMap<String, Boolean>()
        @get:XMap
        var area by linkedMap<String, Boolean>()
        @get:XMap
        var category by linkedMap<String, Boolean>()
        
        val typeSettings = TypeSettings()
        
        val areaNames = mutableMapOf<String, () -> String?>()
        val categoryNames = mutableMapOf<String, () -> String?>()
        
        fun a(){}
        
        inner class TypeSettings {
            val start by type withDefault true
            val rare by type withDefault true
            val dangerous by type withDefault true
            val insight by type withDefault true
            val repeatable by type withDefault true
        }
    }
    
    override fun initSettings() {
        //it.name is ok here
        val tiers = ParadoxTechnologyHandler.Stellaris.getTechnologyTiers(project, null)
        tiers.forEach { state.tier.putIfAbsent(it.name, true) }
        val areas = ParadoxTechnologyHandler.Stellaris.getResearchAreas()
        areas.forEach { state.area.putIfAbsent(it, true) }
        val categories = ParadoxTechnologyHandler.Stellaris.getTechnologyCategories(project, null)
        categories.forEach { state.category.putIfAbsent(it.name, true) }
        areas.forEach { state.areaNames.put(it) { ParadoxPresentationHandler.getText(it.uppercase(), project) } }
        categories.forEach { state.categoryNames.put(it.name) { ParadoxPresentationHandler.getNameText(it) } }
        super.initSettings()
    }
}
