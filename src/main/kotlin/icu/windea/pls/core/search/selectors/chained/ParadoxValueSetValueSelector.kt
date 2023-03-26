package icu.windea.pls.core.search.selectors.chained

import com.intellij.openapi.project.*
import icu.windea.pls.lang.*
import icu.windea.pls.script.psi.*

class ParadoxValueSetValueSelector(project: Project, context: Any? = null): ChainedParadoxSelector<ParadoxScriptStringExpressionElement>(project, context)

fun valueSetValueSelector(project: Project, context: Any? = null) = ParadoxValueSetValueSelector(project, context)

fun ParadoxValueSetValueSelector.declarationOnly() =
	filterBy { !ParadoxValueSetValueHandler.isDeclaration(it) }

fun ParadoxValueSetValueSelector.distinctByValue() =
	distinctBy { ParadoxValueSetValueHandler.getName(it) }