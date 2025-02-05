package icu.windea.pls.localisation.codeInsight.hints

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.ui.dsl.builder.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.localisation.codeInsight.hints.ParadoxLocalisationIconHintsProvider.*
import icu.windea.pls.localisation.psi.*
import icu.windea.pls.script.psi.*
import icu.windea.pls.tool.*
import javax.swing.*

private val settingsKey = SettingsKey<Settings>("ParadoxLocalisationIconHintsSettingsKey")

/**
 * 本地化图标的内嵌提示（显示选用的图标，如果大小合适且存在，只是显示图标而已）。
 */
@Suppress("UnstableApiUsage")
class ParadoxLocalisationIconHintsProvider : ParadoxLocalisationHintsProvider<Settings>() {
	data class Settings(
		var iconHeightLimit: Int = 32
	)
	
	override val name: String get() = PlsBundle.message("localisation.hints.localisationIcon")
	override val description: String get() = PlsBundle.message("localisation.hints.localisationIcon.description")
	override val key: SettingsKey<Settings> get() = settingsKey
	
	override fun createSettings() = Settings()
	
	override fun createConfigurable(settings: Settings): ImmediateConfigurable {
		return object : ImmediateConfigurable {
			override fun createComponent(listener: ChangeListener): JComponent = panel {
				row {
					label(PlsBundle.message("localisation.hints.settings.iconHeightLimit"))
						.applyToComponent { toolTipText = PlsBundle.message("localisation.hints.settings.iconHeightLimit.tooltip") }
					textField()
						.bindIntText(settings::iconHeightLimit)
						.bindIntWhenTextChanged(settings::iconHeightLimit)
						.errorOnApply(PlsBundle.message("error.shouldBePositive")) { (it.text.toIntOrNull() ?: 0) <= 0 }
				}
			}
		}
	}
	
	//icu.windea.pls.tool.localisation.ParadoxLocalisationTextInlayRenderer.renderIconTo
	
	override fun PresentationFactory.collect(element: PsiElement, file: PsiFile, editor: Editor, settings: Settings, sink: InlayHintsSink): Boolean {
		if(element is ParadoxLocalisationIcon) {
			val resolved = element.reference?.resolve() ?: return true
			val iconUrl = when {
				resolved is ParadoxScriptDefinitionElement -> ParadoxImageResolver.resolveUrlByDefinition(resolved, defaultToUnknown = false)
				resolved is PsiFile -> ParadoxImageResolver.resolveUrlByFile(resolved.virtualFile, defaultToUnknown = false)
				else -> return true
			}
			if(iconUrl.isNotEmpty()) {
				//忽略异常
				runCatching {
					//找不到图标的话就直接跳过
					val icon = IconLoader.findIcon(iconUrl.toFileUrl()) ?: return true
					//基于内嵌提示的字体大小缩放图标，直到图标宽度等于字体宽度
					if(icon.iconHeight <= settings.iconHeightLimit) {
						//点击可以导航到声明处（定义或DDS）
						val presentation = psiSingleReference(smallScaledIcon(icon)) { resolved }
						val finalPresentation = presentation.toFinalPresentation(this, file.project, smaller = true)
						val endOffset = element.endOffset
						sink.addInlineElement(endOffset, true, finalPresentation, false)
					}
				}
			}
		}
		return true
	}
}
