package icu.windea.pls.lang.cwt.config

import com.intellij.psi.*
import icu.windea.pls.cwt.psi.*

/**
 * @property key (property key)
 * @property value (property value)
 * @property required (option) required
 * @property primary (option) primary
 */
class CwtLocationConfig(
	override val pointer: SmartPsiElementPointer<out CwtProperty>,
	override val info: CwtConfigGroupInfo,
	val key: String,
	val value: String,
	val required: Boolean = false,
	val primary: Boolean = false,
): CwtConfig<CwtProperty>
