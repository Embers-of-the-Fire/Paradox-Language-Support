package icu.windea.pls.lang.cwt.config

import com.intellij.openapi.util.*
import com.intellij.psi.*
import icu.windea.pls.core.annotations.*
import icu.windea.pls.cwt.psi.*

class CwtEnumConfig(
    override val pointer: SmartPsiElementPointer<out CwtProperty>,
    override val info: CwtConfigGroupInfo,
    val name: String,
    val values: Set<@CaseInsensitive String>,
    val valueConfigMap: Map<@CaseInsensitive String, CwtValueConfig>
) : UserDataHolderBase(), CwtConfig<CwtProperty>
