package icu.windea.pls.script.inspections.general

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.*
import com.intellij.codeInspection.*
import com.intellij.openapi.application.*
import com.intellij.openapi.progress.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.search.*
import com.intellij.psi.search.searches.*
import com.intellij.ui.dsl.builder.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.annotations.*
import icu.windea.pls.core.psi.*
import icu.windea.pls.core.search.scope.*
import icu.windea.pls.localisation.*
import icu.windea.pls.script.*
import icu.windea.pls.script.psi.*
import javax.swing.*

/**
 * 值集值值（`some_flag`）被设置但未被使用的检查。
 *
 * 例如，有`set_flag = xxx`但没有`has_flag = xxx`。
 * 
 * 默认不启用。
 */
@SlowApi
class UnusedValueSetValueInspection : LocalInspectionTool() {
    @JvmField var ignoreDefinitionNames = true
    
    //may be very slow for ReferencesSearch
    
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : PsiElementVisitor() {
            var searchScope: GlobalSearchScope? = null //compute once per file
            val statusMap = mutableMapOf<PsiElement, Boolean>() //it's unnecessary to make it synced
            
            private fun shouldVisit(element: PsiElement): Boolean {
                return (element is ParadoxScriptStringExpressionElement && !element.isParameterized())
            }
            
            override fun visitFile(file: PsiFile) {
                val virtualFile = file.virtualFile
                searchScope = runReadAction { ParadoxSearchScope.fromFile(holder.project, virtualFile, virtualFile.fileInfo) }
                    .withFileTypes(ParadoxScriptFileType, ParadoxLocalisationFileType)
            }
            
            override fun visitElement(element: PsiElement) {
                ProgressManager.checkCanceled()
                if(!shouldVisit(element)) return
                
                //ignore definition names if necessary
                if(ignoreDefinitionNames && element is ParadoxScriptString && element.isDefinitionName()) return
                //may only resolve to single ParadoxValueSetValueElement (set-flag expression)
                val reference = element.reference ?: return
                if(!reference.canResolveValueSetValue()) return
                val resolved = reference.resolveFirst()
                ProgressManager.checkCanceled()
                if(resolved !is ParadoxValueSetValueElement) return
                if(resolved.readWriteAccess == Access.Write) {
                    //当确定已被使用时，后续不需要再进行ReferencesSearch
                    val used = statusMap[resolved]
                    val isUsed = if(used == null) {
                        ProgressManager.checkCanceled()
                        val searchScope = searchScope ?: GlobalSearchScope.allScope(holder.project)
                        val r = ReferencesSearch.search(resolved, searchScope).processQueryAsync p@{
                            ProgressManager.checkCanceled()
                            val res = it.resolve()
                            ProgressManager.checkCanceled()
                            if(res is ParadoxValueSetValueElement && res.readWriteAccess == Access.Read) {
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
                        used
                    }
                    if(!isUsed) {
                        registerProblem(element, resolved.name, reference.rangeInElement)
                    }
                }
            }
            
            private fun registerProblem(element: PsiElement, name: String, range: TextRange) {
                val message = PlsBundle.message("inspection.script.general.unusedValueSetValue.description", name)
                holder.registerProblem(element, message, ProblemHighlightType.LIKE_UNUSED_SYMBOL, range)
            }
        }
    }
    
    override fun createOptionsPanel(): JComponent {
        return panel {
            //ignoreDefinitionNames
            row {
                checkBox(PlsBundle.message("inspection.script.general.unusedValueSetValue.option.ignoreDefinitionNames"))
                    .bindSelected(::ignoreDefinitionNames)
                    .applyToComponent { toolTipText = PlsBundle.message("inspection.script.general.unusedValueSetValue.option.ignoreDefinitionNames.tooltip") }
                    .actionListener { _, component -> ignoreDefinitionNames = component.isSelected }
            }
        }
    }
}

