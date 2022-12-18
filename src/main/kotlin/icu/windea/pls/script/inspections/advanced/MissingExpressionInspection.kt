package icu.windea.pls.script.inspections.advanced

import com.intellij.codeInspection.*
import com.intellij.openapi.progress.*
import com.intellij.psi.*
import icu.windea.pls.*
import icu.windea.pls.config.cwt.expression.*
import icu.windea.pls.core.*
import icu.windea.pls.core.util.*
import icu.windea.pls.script.psi.*

/**
 * 定义声明中缺失的表达式的检查。
 */
class MissingExpressionInspection : LocalInspectionTool() {
	override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
		if(file !is ParadoxScriptFile) return null
		val holder = ProblemsHolder(manager, file, isOnTheFly)
		file.accept(object : ParadoxScriptRecursiveElementWalkingVisitor() {
			override fun visitFile(file: PsiFile) {
				val block = file.castOrNull<ParadoxScriptFile>()?.block ?: return
				val position = file //TODO not very suitable
				doVisitBlockElement(block, position)
				super.visitFile(file)
			}
			
			override fun visitProperty(element: ParadoxScriptProperty) {
				ProgressManager.checkCanceled()
				//skip checking property if property key may contain parameters
				val propertyKey = element.propertyKey
				if(element.propertyKey.isParameterAwareExpression()) return
				val block = element.block ?: return
				val position = propertyKey
				doVisitBlockElement(block, position)
				super.visitProperty(element)
			}
			
			override fun visitBlock(element: ParadoxScriptBlock) {
				ProgressManager.checkCanceled()
				val block = element
				val position = element.findChild(ParadoxScriptElementTypes.LEFT_BRACE) ?: return
				doVisitBlockElement(block, position)
				super.visitBlock(element)
			}
			
			private fun doVisitBlockElement(block: ParadoxScriptBlockElement, position: PsiElement) {
				val definitionMemberInfo = block.definitionMemberInfo
				if(definitionMemberInfo == null) return
				definitionMemberInfo.childPropertyOccurrenceMap.takeIf { it.isNotEmpty() }
					?.forEach { (configExpression, occurrence) ->
						doCheckOccurrence(occurrence, configExpression, position)
					}
				definitionMemberInfo.childValueOccurrenceMap.takeIf { it.isNotEmpty() }
					?.forEach { (configExpression, occurrence) ->
						doCheckOccurrence(occurrence, configExpression, position)
					}
			}
			
			private fun doCheckOccurrence(occurrence: Occurrence, configExpression: CwtDataExpression, position: PsiElement) {
				val (actual, min,_, relaxMin) = occurrence
				if(min != null && actual < min) {
					val isConst = configExpression.type.isConstant()
					val description = when {
						isConst -> PlsBundle.message("script.inspection.advanced.missingExpression.description.1", configExpression, min, actual)
						else -> PlsBundle.message("script.inspection.advanced.missingExpression.description.2", configExpression, min, actual)
					}
					val highlightType = when{
						relaxMin -> ProblemHighlightType.WEAK_WARNING //weak warning (wave lines), not warning
						else -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING
					}
					holder.registerProblem(position, description, highlightType)
				}
			}
		})
		return holder.resultsArray
	}
}