package icu.windea.pls.script.inspections.general

import com.intellij.codeInspection.*
import com.intellij.openapi.progress.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.ui.dsl.builder.*
import icu.windea.pls.*
import icu.windea.pls.config.config.*
import icu.windea.pls.core.*
import icu.windea.pls.core.psi.*
import icu.windea.pls.lang.*
import icu.windea.pls.lang.parameter.*
import icu.windea.pls.script.psi.*
import javax.swing.*

/**
 * 缺少的参数的检查。
 *
 * @property forInvocationExpressions 是否对调用表达式进行检查。（`some_effect = {PARAM = some_value}`）
 * @property forScriptValueExpressions 是否对SV表达式进行检查。（`some_prop = value:some_sv|PARAM|value|`）
 */
class MissingParameterInspection : LocalInspectionTool() {
    @JvmField var forInvocationExpressions = true
    @JvmField var forScriptValueExpressions = true
    
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                ProgressManager.checkCanceled()
                if(forInvocationExpressions && element is ParadoxScriptProperty && !element.name.isParameterizedExpression()) {
                    visitElementFromContextReferenceElement(element)
                } else if(forScriptValueExpressions && element is ParadoxScriptString) {
                    visitElementFromContextReferenceElement(element)
                }
            }
            
            private fun visitElementFromContextReferenceElement(element: PsiElement) {
                ProgressManager.checkCanceled()
                val configs = ParadoxConfigHandler.getConfigs(element)
                val config = configs.firstOrNull() as? CwtPropertyConfig ?: return
                ProgressManager.checkCanceled()
                val requiredParameterNames = mutableSetOf<String>()
                val from = ParadoxParameterContextReferenceInfo.FromLocation.ContextReference
                val contextReferenceInfo = ParadoxParameterSupport.findContextReferenceInfo(element, config, from) ?: return
                ParadoxParameterSupport.processContext(element, contextReferenceInfo) p@{
                    ProgressManager.checkCanceled()
                    val parameters = ParadoxParameterHandler.getParameters(it)
                    if(parameters.isNotEmpty()) {
                        parameters.forEach { (name, parameterInfo) ->
                            if(requiredParameterNames.contains(name)) return@forEach
                            if(!parameterInfo.optional) requiredParameterNames.add(name)
                        }
                    }
                    false
                }
                requiredParameterNames.removeAll(contextReferenceInfo.existingParameterNames)
                if(requiredParameterNames.isEmpty()) return
                registerProblem(element, requiredParameterNames, contextReferenceInfo.rangeInElement)
            }
            
            private fun registerProblem(element: PsiElement, names: Set<String>, rangeInElement: TextRange? = null) {
                val message = when {
                    names.isEmpty() -> return
                    names.size == 1 -> PlsBundle.message("inspection.script.general.missingParameter.description.1", names.single().let { "'$it'" })
                    else -> PlsBundle.message("inspection.script.general.missingParameter.description.2", names.joinToString(", ") { "'$it'" })
                }
                holder.registerProblem(element, rangeInElement, message)
            }
        }
    }
    
    override fun createOptionsPanel(): JComponent {
        return panel {
            row {
                checkBox(PlsBundle.message("inspection.script.general.missingParameter.option.forInvocationExpressions"))
                    .bindSelected(::forInvocationExpressions)
                    .applyToComponent { toolTipText = PlsBundle.message("inspection.script.general.missingParameter.option.forInvocationExpressions.tooltip") }
                    .actionListener { _, component -> forInvocationExpressions = component.isSelected }
            }
            row {
                checkBox(PlsBundle.message("inspection.script.general.missingParameter.option.forScriptValueExpressions"))
                    .bindSelected(::forScriptValueExpressions)
                    .applyToComponent { toolTipText = PlsBundle.message("inspection.script.general.missingParameter.option.forScriptValueExpressions.tooltip") }
                    .actionListener { _, component -> forScriptValueExpressions = component.isSelected }
            }
        }
    }
}