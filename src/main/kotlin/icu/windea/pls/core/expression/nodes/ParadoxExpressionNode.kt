package icu.windea.pls.core.expression.nodes

import com.intellij.lang.annotation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import icu.windea.pls.core.*
import icu.windea.pls.core.expression.errors.*
import icu.windea.pls.script.psi.*

interface ParadoxExpressionNode : AttributesKeyAware {
    val text: String
    val rangeInExpression: TextRange
    val nodes: List<ParadoxExpressionNode> get() = emptyList()
    
    fun annotate(element: ParadoxScriptStringExpressionElement, holder: AnnotationHolder) {}
    
    fun getReference(element: ParadoxScriptStringExpressionElement): PsiReference? = null
    
    fun getUnresolvedError(element: ParadoxScriptStringExpressionElement): ParadoxExpressionError? = null
}

