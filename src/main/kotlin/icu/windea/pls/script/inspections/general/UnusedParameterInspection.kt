package icu.windea.pls.script.inspections.general

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.*
import com.intellij.codeInspection.*
import com.intellij.openapi.application.*
import com.intellij.openapi.progress.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.annotations.*
import icu.windea.pls.core.psi.*
import icu.windea.pls.core.search.*
import icu.windea.pls.core.search.scope.*
import icu.windea.pls.core.search.selector.*
import icu.windea.pls.localisation.*
import icu.windea.pls.model.constraints.*
import icu.windea.pls.script.*
import icu.windea.pls.script.psi.*
import kotlin.collections.set

/**
 * 参数被设置/引用但未被使用的检查。
 *
 * 例如：有`some_effect = {PARAM = some_value}`但没有`some_effect = { some_prop = $PARAM$ }`，后者是定义的声明。
 */
@SlowApi
class UnusedParameterInspection : LocalInspectionTool() {
    //may be very slow for ReferencesSearch
    
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        val project = holder.project
        val file = holder.file
        //compute once per file
        val searchScope = runReadAction { ParadoxSearchScope.fromFile(project, file.virtualFile) }
            .withFileTypes(ParadoxScriptFileType, ParadoxLocalisationFileType)
        //it's unnecessary to make it synced
        val statusMap = mutableMapOf<PsiElement, Boolean>()
        
        return object : PsiElementVisitor() {
            private fun shouldVisit(element: PsiElement): Boolean {
                return when {
                    element is ParadoxScriptStringExpressionElement -> !element.text.isParameterized()
                    element is ParadoxConditionParameter -> true
                    else -> false
                }
            }
            
            override fun visitElement(element: PsiElement) {
                ProgressManager.checkCanceled()
                if(!shouldVisit(element)) return
                
                val references = element.references
                for(reference in references) {
                    ProgressManager.checkCanceled()
                    if(!reference.canResolve(ParadoxResolveConstraint.Parameter)) continue
                    val resolved = reference.resolve()
                    if(resolved !is ParadoxParameterElement) continue
                    if(resolved.contextName.isParameterized()) continue //skip if context name is parameterized
                    if(resolved.readWriteAccess != Access.Write) continue
                    val cachedStatus = statusMap[resolved]
                    val status = if(cachedStatus == null) {
                        ProgressManager.checkCanceled()
                        val selector = parameterSelector(project, file).withSearchScope(searchScope) //use file as context
                        val r = ParadoxParameterSearch.search(resolved.name, resolved.contextKey, selector).processQueryAsync p@{
                            ProgressManager.checkCanceled()
                            if(it.readWriteAccess == Access.Read) {
                                statusMap[resolved] = true
                                false
                            } else {
                                true
                            }
                        }
                        
                        if(r) {
                            statusMap[resolved] = false
                            false
                        } else {
                            true
                        }
                    } else {
                        cachedStatus
                    }
                    if(!status) {
                        registerProblem(element, resolved.name, reference.rangeInElement)
                    }
                }
            }
            
            private fun registerProblem(element: PsiElement, name: String, range: TextRange) {
                val message = PlsBundle.message("inspection.script.general.unusedParameter.description", name)
                holder.registerProblem(element, message, ProblemHighlightType.LIKE_UNUSED_SYMBOL, range)
            }
        }
    }
}

