package icu.windea.pls.config.cwt

import com.intellij.psi.*
import icu.windea.pls.cwt.psi.*

/**
 * @property internalId internal_id: int
 * @property supportedScopes supported_scopes: string[]
 */
data class CwtModifierCategoryConfig(
	override val pointer: SmartPsiElementPointer<CwtProperty>,
	val name:String,
	val internalId: String? = null,
	val supportedScopes:List<String>
): CwtConfig<CwtProperty> {
	val supportedScopesText = supportedScopes.joinToString(" ","{ "," }")
}

