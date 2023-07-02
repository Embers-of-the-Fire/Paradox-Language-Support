package icu.windea.pls.core.index

import com.intellij.psi.stubs.*
import icu.windea.pls.script.psi.*

/**
 * 用于基于类型索引定义声明。
 */
class ParadoxDefinitionTypeIndex : StringStubIndexExtension<ParadoxScriptDefinitionElement>() {
	companion object {
		@JvmField val KEY = StubIndexKey.createIndexKey<String, ParadoxScriptDefinitionElement>("paradox.definition.type.index")
		private const val VERSION = 31 //1.1.1
		private const val CACHE_SIZE = 1024 //180+ in stellaris@3.6
	}
	
	override fun getKey() = KEY
	
	override fun getVersion() = VERSION
	
	override fun getCacheSize() = CACHE_SIZE
}

