package icu.windea.pls.lang.cwt.config

import com.intellij.psi.*
import icu.windea.pls.cwt.psi.*

class CwtFileConfig(
    override val pointer: SmartPsiElementPointer<CwtFile>,
    override val info: CwtConfigGroupInfo,
    val properties: List<CwtPropertyConfig>,
    val values: List<CwtValueConfig>,
    val name: String
) : CwtConfig<CwtFile> {
    val key = name.substringBefore('.')
}
