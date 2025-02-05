package icu.windea.pls.script.codeInsight.hints

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.*
import com.intellij.openapi.editor.*
import com.intellij.psi.*
import com.intellij.ui.dsl.builder.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.core.collections.*
import icu.windea.pls.lang.*
import icu.windea.pls.lang.cwt.expression.*
import icu.windea.pls.script.codeInsight.hints.ParadoxDefinitionReferenceLocalizedNameHintsProvider.*
import icu.windea.pls.script.psi.*
import icu.windea.pls.tool.localisation.*
import java.util.*
import javax.swing.*

/**
 * 定义引用信息的内嵌提示（对应定义的名字和类型、本地化名字）。
 */
@Suppress("UnstableApiUsage")
class ParadoxDefinitionReferenceLocalizedNameHintsProvider : ParadoxScriptHintsProvider<Settings>() {
    data class Settings(
        var textLengthLimit: Int = 30,
        var iconHeightLimit: Int = 32
    )
    
    private val settingsKey: SettingsKey<Settings> = SettingsKey("ParadoxDefinitionReferenceLocalizedNameHintsSettingsKey")
    private val expressionTypes: EnumSet<CwtDataType> = enumSetOf(
        CwtDataType.Definition,
        CwtDataType.AliasName, //需要兼容alias
        CwtDataType.AliasKeysField, //需要兼容alias
        CwtDataType.AliasMatchLeft, //需要兼容alias
        CwtDataType.SingleAliasRight, //需要兼容single_alias
    )
    
    override val name: String get() = PlsBundle.message("script.hints.definitionReferenceLocalizedName")
    override val description: String get() = PlsBundle.message("script.hints.definitionReferenceLocalizedName.description")
    override val key: SettingsKey<Settings> get() = settingsKey
    
    override fun createSettings() = Settings()
    
    override fun createConfigurable(settings: Settings): ImmediateConfigurable {
        return object : ImmediateConfigurable {
            override fun createComponent(listener: ChangeListener): JComponent = panel {
                row {
                    label(PlsBundle.message("script.hints.settings.textLengthLimit")).widthGroup("left")
                        .applyToComponent { toolTipText = PlsBundle.message("script.hints.settings.textLengthLimit.tooltip") }
                    textField()
                        .bindIntText(settings::textLengthLimit)
                        .bindIntWhenTextChanged(settings::textLengthLimit)
                        .errorOnApply(PlsBundle.message("error.shouldBePositiveOrZero")) { (it.text.toIntOrNull() ?: 0) < 0 }
                }
                row {
                    label(PlsBundle.message("script.hints.settings.iconHeightLimit")).widthGroup("left")
                        .applyToComponent { toolTipText = PlsBundle.message("script.hints.settings.iconHeightLimit.tooltip") }
                    textField()
                        .bindIntText(settings::iconHeightLimit)
                        .bindIntWhenTextChanged(settings::iconHeightLimit)
                        .errorOnApply(PlsBundle.message("error.shouldBePositive")) { (it.text.toIntOrNull() ?: 0) <= 0 }
                }
            }
        }
    }
    
    override fun PresentationFactory.collect(element: PsiElement, file: PsiFile, editor: Editor, settings: Settings, sink: InlayHintsSink): Boolean {
        //这里需要兼容 ParadoxScriptInt
        if(element !is ParadoxScriptExpressionElement) return true
        if(element !is ParadoxScriptStringExpressionElement && element !is ParadoxScriptInt) return true
        if(!element.isExpression()) return true
        val config = ParadoxConfigHandler.getConfigs(element).firstOrNull()
            ?.takeIf { it.expression.type in expressionTypes }
            ?: return true
        val configGroup = config.info.configGroup
        val isKey = element is ParadoxScriptPropertyKey
        val resolved = ParadoxConfigHandler.resolveScriptExpression(element, null, config, config.expression, configGroup, isKey)
        if(resolved is ParadoxScriptDefinitionElement) {
            val presentation = doCollect(resolved, editor, settings) ?: return true
            val finalPresentation = presentation.toFinalPresentation(this, file.project)
            val endOffset = element.endOffset
            sink.addInlineElement(endOffset, true, finalPresentation, false)
        }
        return true
    }
    
    private fun PresentationFactory.doCollect(element: ParadoxScriptDefinitionElement, editor: Editor, settings: Settings): InlayPresentation? {
        val primaryLocalisation = ParadoxDefinitionHandler.getPrimaryLocalisation(element) ?: return null
        return ParadoxLocalisationTextInlayRenderer.render(primaryLocalisation, this, editor, settings.textLengthLimit, settings.iconHeightLimit)
    }
}
