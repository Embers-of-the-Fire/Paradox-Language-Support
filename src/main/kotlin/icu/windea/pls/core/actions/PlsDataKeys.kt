package icu.windea.pls.core.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.observable.properties.*
import icu.windea.pls.lang.model.*
import kotlin.reflect.*

object PlsDataKeys {
	val gameTypePropertyKey = DataKey.create<GraphProperty<ParadoxGameType>>("PARADOX_GAME_TYPE_PROPERTY")
	val rootTypePropertyKey = DataKey.create<GraphProperty<ParadoxRootType>>("PARADOX_ROOT_TYPE_PROPERTY")
	val gameTypeKey = DataKey.create<KMutableProperty0<ParadoxGameType>>("PARADOX_GAME_TYPE")
}
