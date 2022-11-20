package icu.windea.pls.script.inspections.advanced

import com.intellij.codeInspection.*
import com.intellij.openapi.progress.*
import com.intellij.psi.*
import com.intellij.ui.dsl.builder.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.handler.ParadoxCwtConfigHandler.resolvePropertyConfigs
import icu.windea.pls.core.handler.ParadoxCwtConfigHandler.resolveValueConfigs
import icu.windea.pls.script.psi.*
import javax.swing.*

/**
 * 不正确的脚本结构的检查。
 *
 * * 如果PLS找不到匹配的CWT规则，则认为对应的脚本结构不正确。
 * * 这不意味着CWTools找不到匹配的CWT规则
 * * 这也不意味着游戏本身无法识别对应的脚本结构。
 * * 默认不会开启，一方面PLS对CWT规则支持尚不完善，另一方面CWT规则本身也尚不完善。
 */
class IncorrectScriptStructureInspection : LocalInspectionTool() {
	@JvmField var forPropertyKey = true
	@JvmField var forPropertyValue = true
	@JvmField var forValue = true
	
	override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
		if(file !is ParadoxScriptFile) return null
		val holder = ProblemsHolder(manager, file, isOnTheFly)
		file.accept(object : ParadoxScriptRecursiveElementWalkingVisitor() {
			override fun visitProperty(element: ParadoxScriptProperty) {
				ProgressManager.checkCanceled()
				val shouldCheck = forPropertyKey
				if(shouldCheck) {
					//skip checking property if property key may contain parameters
					if(element.propertyKey.isParameterAwareExpression()) return
					val config = resolvePropertyConfigs(element).firstOrNull()
					//是定义元素，非定义自身，且路径中不带参数
					if(config == null && element.definitionElementInfo?.let { it.isValid && !it.elementPath.isParameterAware } == true) {
						holder.registerProblem(element, PlsBundle.message("script.inspection.advanced.incorrectScriptStructure.description.1", element.expression))
						//skip checking property value if property key is invalid 
						return
					}
				}
				super.visitProperty(element)
			}
			
			override fun visitValue(element: ParadoxScriptValue) {
				ProgressManager.checkCanceled()
				//排除block
				val shouldCheck = if(element.isPropertyValue()) forPropertyValue else forValue
				if(shouldCheck) {
					//skip checking value if it may contain parameters
					if(element is ParadoxScriptString && element.isParameterAwareExpression()) return
					//精确解析
					val config = resolveValueConfigs(element, orDefault = false).firstOrNull()
					//是定义元素，非定义自身，且路径中不带参数
					if(config == null && element.definitionElementInfo?.let { it.isValid && !it.elementPath.isParameterAware } == true) {
						holder.registerProblem(element, PlsBundle.message("script.inspection.advanced.incorrectScriptStructure.description.1", element.expression))
						//skip checking children
						return
					}
				}
				super.visitValue(element)
			}
		})
		return holder.resultsArray
	}
	
	override fun createOptionsPanel(): JComponent {
		return panel {
			row {
				checkBox(PlsBundle.message("script.inspection.advanced.incorrectScriptStructure.option.forPropertyKey"))
					.bindSelected(::forPropertyKey)
					.actionListener { _, component -> forPropertyKey = component.isSelected }
			}
			row {
				checkBox(PlsBundle.message("script.inspection.advanced.incorrectScriptStructure.option.forPropertyValue"))
					.bindSelected(::forPropertyValue)
					.actionListener { _, component -> forPropertyValue = component.isSelected }
			}
			row {
				checkBox(PlsBundle.message("script.inspection.advanced.incorrectScriptStructure.option.forValue"))
					.bindSelected(::forValue)
					.actionListener { _, component -> forValue = component.isSelected }
			}
		}
	}
}
