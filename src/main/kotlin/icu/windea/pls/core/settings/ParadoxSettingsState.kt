package icu.windea.pls.core.settings

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.*
import icu.windea.pls.model.*

@State(name = "ParadoxSettingsState", storages = [Storage("paradoxLanguageSupport.xml")])
data class ParadoxSettingsState(
	@JvmField var preferOverridden: Boolean = false,
	@JvmField var renderLineCommentText: Boolean = false,
	@JvmField var renderDefinitionText: Boolean = true,
	@JvmField var renderLocalisationText: Boolean = true,
	@JvmField var defaultGameType: ParadoxGameType = ParadoxGameType.Stellaris
) : PersistentStateComponent<ParadoxSettingsState> {
	override fun getState() = this

	override fun loadState(state: ParadoxSettingsState) = XmlSerializerUtil.copyBean(state, this)

	companion object {
		@JvmStatic
		fun getInstance(): ParadoxSettingsState = ServiceManager.getService(ParadoxSettingsState::class.java)
	}
}
