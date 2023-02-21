package icu.windea.pls.core.search

import com.intellij.openapi.application.*
import com.intellij.util.*
import icu.windea.pls.core.*
import icu.windea.pls.core.index.*
import icu.windea.pls.lang.*
import icu.windea.pls.script.psi.*

/**
 * 值集值值的查询器。
 */
class ParadoxValueSetValueSearcher : QueryExecutorBase<ParadoxScriptString, ParadoxValueSetValueSearch.SearchParameters>() {
	override fun processQuery(queryParameters: ParadoxValueSetValueSearch.SearchParameters, consumer: Processor<in ParadoxScriptString>) {
		val name = queryParameters.name
		val valueSetName = queryParameters.valueSetName
		val project = queryParameters.project
		val scope = queryParameters.selector.scope
		ParadoxValueSetIndex.processAllElements(valueSetName, project, scope) {
			if((name == null || matchesName(it, name))) {
				consumer.process(it)
			} else {
				true
			}
		}
	}
	
	private fun matchesName(element: ParadoxScriptString, name: String?): Boolean {
		return ParadoxValueSetValueHandler.getName(element) == name
	}
}

