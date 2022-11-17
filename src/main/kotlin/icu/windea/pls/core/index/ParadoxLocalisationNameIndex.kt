package icu.windea.pls.core.index

import com.intellij.psi.stubs.*
import icu.windea.pls.localisation.psi.*

object ParadoxLocalisationNameIndex : AbstractParadoxLocalisationNameIndex() {
	private val key = StubIndexKey.createIndexKey<String, ParadoxLocalisationProperty>("paradox.localisation.name.index")
	private const val version = 11 //0.7.4
	private const val cacheSize = 100 * 1024
	
	override fun getKey() = key
	override fun getVersion() = version
	override fun getCacheSize() = cacheSize
}