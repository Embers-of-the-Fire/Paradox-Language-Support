package icu.windea.pls.core.tool.cwt

import com.intellij.openapi.application.*
import com.intellij.openapi.fileEditor.*
import com.intellij.testFramework.fixtures.*
import icu.windea.pls.core.model.*
import icu.windea.pls.core.tool.cwt.*
import org.junit.*

class CwtModifierConfigGeneratorTest : BasePlatformTestCase() {
	override fun isWriteActionRequired(): Boolean {
		return true
	}
	
	@Test
	fun test() {
		CwtModifierConfigGenerator(
			project,
			ParadoxGameType.Ck3,
			"cwt/cwtools-ck3-config/script-docs/modifiers.log",
			"cwt/cwtools-ck3-config/config/modifiers.cwt",
			"cwt/cwtools-ck3-config/config/modifier_categories.cwt"
		).generate()
		CwtModifierConfigGenerator(
			project,
			ParadoxGameType.Stellaris,
			"cwt/cwtools-stellaris-config/script-docs/modifiers.log",
			"cwt/cwtools-stellaris-config/config/modifiers.cwt",
			"cwt/cwtools-stellaris-config/config/modifier_categories.cwt"
		).generate()
		CwtModifierConfigGenerator(
			project,
			ParadoxGameType.Vic3,
			"cwt/cwtools-vic3-config/script-docs/modifiers.log",
			"cwt/cwtools-vic3-config/config/modifiers.cwt",
			"cwt/cwtools-vic3-config/config/modifier_categories.cwt"
		).generate()
		
		runInEdt { FileDocumentManager.getInstance().saveAllDocuments() }
	}
}