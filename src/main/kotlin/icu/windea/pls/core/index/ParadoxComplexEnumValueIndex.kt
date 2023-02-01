package icu.windea.pls.core.index

import com.intellij.psi.stubs.*
import icu.windea.pls.script.psi.*

object ParadoxComplexEnumValueIndex: StringStubIndexExtension<ParadoxScriptStringExpressionElement>(){
	private val key = StubIndexKey.createIndexKey<String, ParadoxScriptStringExpressionElement>("paradox.complexEnumValue.index")
	private const val version = 14 //0.7.13
	private const val cacheSize = 2 * 1024
	
	override fun getKey() = key
	
	override fun getVersion() = version
	
	override fun getCacheSize() = cacheSize
}

