package icu.windea.pls.core.index

import com.intellij.psi.stubs.*
import icu.windea.pls.core.psi.*

//注意这里不能直接访问element.definitionInfo，需要优先通过element.stub获取定义信息

object ParadoxDefinitionNameIndex : StringStubIndexExtension<ParadoxDefinitionProperty>() {
	private val key = StubIndexKey.createIndexKey<String, ParadoxDefinitionProperty>("paradox.definition.name.index")
	private const val version = 12 //0.7.6
	private const val cacheSize = 4 * 1024
	
	override fun getKey() = key
	
	override fun getVersion() = version
	
	override fun getCacheSize() = cacheSize
}
