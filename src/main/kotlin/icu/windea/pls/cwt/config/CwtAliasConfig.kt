package icu.windea.pls.cwt.config

import com.intellij.psi.*
import icu.windea.pls.cwt.psi.*

data class CwtAliasConfig(
	override val pointer: SmartPsiElementPointer<CwtProperty>,
	val name: String,
	val config: CwtPropertyConfig
) : CwtConfig<CwtProperty>