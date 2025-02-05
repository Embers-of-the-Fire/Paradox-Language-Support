package icu.windea.pls.script.psi.impl

import com.intellij.psi.stubs.*
import icu.windea.pls.core.*
import icu.windea.pls.model.*
import icu.windea.pls.script.psi.*

class ParadoxScriptFileStubImpl(
    file: ParadoxScriptFile?,
    override val name: String,
    override val type: String,
    override val subtypes: List<String>?,
    override val gameType: ParadoxGameType
) : PsiFileStubImpl<ParadoxScriptFile>(file), ParadoxScriptFileStub {
    override val elementPath: ParadoxElementPath get() = EmptyParadoxElementPath
    override val rootKey: String get() = name
    
    override val isValidDefinition: Boolean = type.isNotEmpty()
    override val nestedTypeRootKeys: Set<String> = getCwtConfig().get(gameType).types.get(type)?.possibleNestedTypeRootKeys.orEmpty()
    
    override fun toString(): String {
        return "ParadoxScriptFileStub(name=$name, type=$type, subtypes=$subtypes, rootKey=$rootKey, gameType=$gameType)"
    }
}
