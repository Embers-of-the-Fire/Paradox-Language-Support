package icu.windea.pls.script.psi

import com.intellij.psi.stubs.*
import icu.windea.pls.lang.model.*

interface ParadoxScriptStringExpressionElementStub<T : ParadoxScriptStringExpressionElement> : StubElement<T> {
	val gameType: ParadoxGameType?
}
