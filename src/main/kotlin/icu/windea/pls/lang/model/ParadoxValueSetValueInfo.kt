package icu.windea.pls.lang.model

import com.intellij.codeInsight.highlighting.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import icu.windea.pls.core.*
import icu.windea.pls.lang.cwt.config.*

data class ParadoxValueSetValueInfo(
    val name: String,
    val valueSetName: String,
    val readWriteAccess: ReadWriteAccessDetector.Access,
    override val elementOffset: Int,
    override val gameType: ParadoxGameType
) : ParadoxExpressionInfo {
    @Volatile override var file: PsiFile? = null //unused yet
    
    fun getConfig(project: Project): CwtEnumConfig? {
        return getCwtConfig(project).get(gameType).values[valueSetName]
    }
}
