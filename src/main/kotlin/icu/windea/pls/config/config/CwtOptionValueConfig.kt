package icu.windea.pls.config.config

import com.intellij.psi.*
import icu.windea.pls.cwt.psi.*
import icu.windea.pls.lang.model.*

data class CwtOptionValueConfig(
    override val pointer: SmartPsiElementPointer<CwtValue>, //NOTE 目前并未使用，因此直接传入emptyPointer()就行
    override val info: CwtConfigGroupInfo,
    override val value: String,
    override val valueType: CwtType = CwtType.String,
    override val options: List<CwtOptionConfig>? = null,
    override val optionValues: List<CwtOptionValueConfig>? = null
) : CwtConfig<CwtValue>, CwtValueAware, CwtOptionsAware
