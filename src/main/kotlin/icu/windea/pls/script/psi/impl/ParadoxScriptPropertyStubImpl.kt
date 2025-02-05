package icu.windea.pls.script.psi.impl

import com.intellij.psi.stubs.*
import icu.windea.pls.core.*
import icu.windea.pls.model.*
import icu.windea.pls.script.psi.*

class ParadoxScriptPropertyStubImpl(
	parent: StubElement<*>,
	override val name: String,
	override val type: String,
	override val subtypes: List<String>?,
	override val rootKey: String,
	override val elementPath: ParadoxElementPath,
	override val gameType: ParadoxGameType
) : StubBase<ParadoxScriptProperty>(parent, ParadoxScriptStubElementTypes.PROPERTY), ParadoxScriptPropertyStub {
	override val isValidDefinition: Boolean = type.isNotEmpty()
	override val nestedTypeRootKeys: Set<String> = getCwtConfig().get(gameType).types.get(type)?.possibleNestedTypeRootKeys.orEmpty()
	
	override fun toString(): String {
		return "ParadoxScriptPropertyStub(name=$name, type=$type, subtypes=$subtypes, rootKey=$rootKey, elementPath=$elementPath, gameType=$gameType)"
	}
}

