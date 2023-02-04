package icu.windea.pls.core.settings

import com.intellij.openapi.application.*
import com.intellij.openapi.options.*
import com.intellij.openapi.ui.*
import com.intellij.ui.components.*
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.*
import icu.windea.pls.*
import icu.windea.pls.config.core.*
import icu.windea.pls.config.core.config.*
import icu.windea.pls.core.*
import icu.windea.pls.localisation.*
import icu.windea.pls.script.*

class ParadoxSettingsConfigurable : BoundConfigurable(PlsBundle.message("settings"), "settings.language.pls"), SearchableConfigurable {
	override fun getId() = "settings.language.pls"
	
	override fun createPanel(): DialogPanel {
		val settings = getSettings()
		return panel {
			//generic
			group(PlsBundle.message("settings.generic")) {
				//defaultGameType
				row {
					label(PlsBundle.message("settings.generic.defaultGameType")).widthGroup("generic")
						.applyToComponent {
							toolTipText = PlsBundle.message("settings.generic.defaultGameType.tooltip")
						}
					val values = ParadoxGameType.valueList
					comboBox(values)
						.bindItem({
							settings.defaultGameType
						}, {
							if(it != null) {
								settings.defaultGameType = it
							}
						})
						.onApply { doReparseFilesInRoot() }
				}
				//preferredLocale
				row {
					label(PlsBundle.message("settings.generic.preferredLocale")).widthGroup("generic")
						.applyToComponent {
							toolTipText = PlsBundle.message("settings.generic.preferredLocale.tooltip")
						}
					comboBox(settings.locales,
						listCellRenderer { value, _, _ ->
							if(value == "auto") {
								text = PlsBundle.message("settings.generic.preferredLocale.auto")
							} else {
								text = getCwtConfig().core.localisationLocales.getValue(value).description
							}
						})
						.bindItem(settings::preferredLocale.toNullableProperty())
						.onApply { doRefreshInlayHints() }
				}
				//ignoredFileNames
				row {
					label(PlsBundle.message("settings.generic.ignoredFileNames")).widthGroup("generic")
						.applyToComponent {
							toolTipText = PlsBundle.message("settings.generic.ignoredFileNames.tooltip")
						}
					expandableTextField({ it.toCommaDelimitedStringList() }, { it.toCommaDelimitedString() })
						.bindText({ settings.ignoredFileNames.orEmpty() }, { settings.ignoredFileNames = it })
						.comment(PlsBundle.message("settings.generic.ignoredFileNames.comment"))
						.align(Align.FILL)
						.resizableColumn()
						.onApply { doReparseFilesByFileNames(settings) }
				}
				//preferOverridden
				row {
					checkBox(PlsBundle.message("settings.generic.preferOverridden"))
						.bindSelected(settings::preferOverridden)
						.applyToComponent { toolTipText = PlsBundle.message("settings.generic.preferOverridden.tooltip") }
				}
			}
			//documentation
			group(PlsBundle.message("settings.documentation")) {
				//renderLineComment
				row {
					checkBox(PlsBundle.message("settings.documentation.renderLineComment"))
						.bindSelected(settings.documentation::renderLineComment)
						.applyToComponent { toolTipText = PlsBundle.message("settings.documentation.renderLineComment.tooltip") }
				}
				//renderRelatedLocalisationsForDefinitions
				row {
					checkBox(PlsBundle.message("settings.documentation.renderRelatedLocalisationsForDefinitions"))
						.bindSelected(settings.documentation::renderRelatedLocalisationsForDefinitions)
						.applyToComponent { toolTipText = PlsBundle.message("settings.documentation.renderRelatedLocalisationsForDefinitions.tooltip") }
				}
				//renderRelatedImagesForDefinitions
				row {
					checkBox(PlsBundle.message("settings.documentation.renderRelatedImagesForDefinitions"))
						.bindSelected(settings.documentation::renderRelatedImagesForDefinitions)
						.applyToComponent { toolTipText = PlsBundle.message("settings.documentation.renderRelatedImagesForDefinitions.tooltip") }
				}
				//renderRelatedLocalisationsForModifiers
				row {
					checkBox(PlsBundle.message("settings.documentation.renderRelatedLocalisationsForModifiers"))
						.bindSelected(settings.documentation::renderRelatedLocalisationsForModifiers)
						.applyToComponent { toolTipText = PlsBundle.message("settings.documentation.renderRelatedLocalisationsForModifiers.tooltip") }
				}
				//renderLocalisationForLocalisations
				row {
					checkBox(PlsBundle.message("settings.documentation.renderIconForModifiers"))
						.bindSelected(settings.documentation::renderIconForModifiers)
						.applyToComponent { toolTipText = PlsBundle.message("settings.documentation.renderIconForModifiers.tooltip") }
				}
				//renderLocalisationForLocalisations
				row {
					checkBox(PlsBundle.message("settings.documentation.renderLocalisationForLocalisations"))
						.bindSelected(settings.documentation::renderLocalisationForLocalisations)
						.applyToComponent { toolTipText = PlsBundle.message("settings.documentation.renderLocalisationForLocalisations.tooltip") }
				}
				//showScopes
				row {
					checkBox(PlsBundle.message("settings.documentation.showScopes"))
						.bindSelected(settings.documentation::showScopes)
						.applyToComponent { toolTipText = PlsBundle.message("settings.documentation.showScopes.tooltip") }
				}
				//showScopeContext
				row {
					checkBox(PlsBundle.message("settings.documentation.showScopeContext"))
						.bindSelected(settings.documentation::showScopeContext)
						.applyToComponent { toolTipText = PlsBundle.message("settings.documentation.showScopeContext.tooltip") }
				}
				//showParameters
				row {
					checkBox(PlsBundle.message("settings.documentation.showParameters"))
						.bindSelected(settings.documentation::showParameters)
						.applyToComponent { toolTipText = PlsBundle.message("settings.documentation.showParameters.tooltip") }
				}
			}
			//completion
			group(PlsBundle.message("settings.completion")) {
				//completeWithValue
				row {
					checkBox(PlsBundle.message("settings.completion.completeWithValue"))
						.bindSelected(settings.completion::completeWithValue)
						.applyToComponent { toolTipText = PlsBundle.message("settings.completion.completeWithValue.tooltip") }
				}
				//completeWithClauseTemplate
				lateinit var completeWithClauseTemplateCb: Cell<JBCheckBox>
				row {
					 checkBox(PlsBundle.message("settings.completion.completeWithClauseTemplate"))
						.bindSelected(settings.completion::completeWithClauseTemplate)
						.applyToComponent { toolTipText = PlsBundle.message("settings.completion.completeWithClauseTemplate.tooltip") }
						 .also { completeWithClauseTemplateCb = it }
				}
				indent { 
					//maxExpressionCountInOneLine
					row {
						checkBox(PlsBundle.message("settings.completion.preferCompleteWithClauseTemplate"))
							.bindSelected(settings.completion::preferCompleteWithClauseTemplate)
							.applyToComponent { toolTipText = PlsBundle.message("settings.completion.preferCompleteWithClauseTemplate.tooltip") }
					}
					//maxExpressionCountInOneLine
					row {
						label(PlsBundle.message("settings.completion.maxExpressionCountInOneLine")).applyToComponent {
							toolTipText = PlsBundle.message("settings.completion.maxExpressionCountInOneLine.tooltip")
						}
						intTextField(0..10).bindIntText(settings.completion::maxExpressionCountInOneLine)
					}
				}.enabledIf(completeWithClauseTemplateCb.selected)
				//completeOnlyScopeIsMatched
				row {
					checkBox(PlsBundle.message("settings.completion.completeOnlyScopeIsMatched"))
						.bindSelected(settings.completion::completeOnlyScopeIsMatched)
						.applyToComponent { toolTipText = PlsBundle.message("settings.completion.completeOnlyScopeIsMatched.tooltip") }
				}
			}
			//inference
			group(PlsBundle.message("settings.inference")) {
				//inlineScriptLocation
				row {
					checkBox(PlsBundle.message("settings.inference.inlineScriptLocation"))
						.bindSelected(settings.inference::inlineScriptLocation)
						.applyToComponent { toolTipText = PlsBundle.message("settings.inference.inlineScriptLocation.tooltip") }
						.onApply { doRefreshInlineScripts() }
				}
			}
			//generation
			group(PlsBundle.message("settings.generation")) {
				//fileNamePrefix
				row {
					label(PlsBundle.message("settings.generation.fileNamePrefix")).applyToComponent {
						toolTipText = PlsBundle.message("settings.generation.fileNamePrefix.tooltip")
					}
					textField().bindText(settings.generation::fileNamePrefix)
				}
			}.visible(false) //TODO
		}
	}
	
	private fun doReparseFilesInRoot() {
		//不存在模组根目录的游戏类型标记文件，设置中的默认游戏类型被更改时，需要重新解析相关文件
		runWriteAction {
			for(rootInfo in ParadoxRootInfo.values) {
				if(rootInfo is ParadoxModRootInfo && rootInfo.markerFile == null) {
					ParadoxCoreHandler.reparseFilesInRoot(rootInfo.gameRootFile)
				}
			}
		}
	}
	
	private fun doReparseFilesByFileNames(settings: ParadoxSettingsState) {
		//设置中的被忽略文件名被更改时，需要重新解析相关文件
		runWriteAction {
			val fileNames = mutableSetOf<String>()
			fileNames += settings.oldIgnoredFileNameSet
			fileNames += settings.ignoredFileNameSet
			settings.oldIgnoredFileNameSet = settings.ignoredFileNameSet
			ParadoxCoreHandler.reparseFilesByFileNames(fileNames)
		}
	}
	
	private fun doRefreshInlayHints() {
		//不存在模组根目录的游戏类型标记文件，设置中的默认游戏类型被更改时，也要刷新相关文件的内嵌提示
		ParadoxCoreHandler.refreshInlayHints { file, _ -> 
			val fileType = file.fileType
			fileType == ParadoxScriptFileType || fileType == ParadoxLocalisationFileType
		}
	}
	
	private fun doRefreshInlineScripts(){
		//清空定义成员缓存以便重新解析inline script文件
		ParadoxModificationTrackerProvider.getInstance().DefinitionMemberInfo.incModificationCount()
		//刷新inline script文件的内嵌提示
		ParadoxCoreHandler.refreshInlayHints { file, _ -> 
			ParadoxInlineScriptHandler.getInlineScriptExpression(file) != null 
		}
	}
}
