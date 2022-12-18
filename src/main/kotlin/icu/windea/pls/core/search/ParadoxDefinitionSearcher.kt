package icu.windea.pls.core.search

import com.intellij.openapi.application.*
import com.intellij.psi.search.*
import com.intellij.util.*
import icu.windea.pls.core.*
import icu.windea.pls.core.expression.*
import icu.windea.pls.core.handler.*
import icu.windea.pls.core.index.*
import icu.windea.pls.script.psi.*

/**
 * 定义的查询器。
 */
class ParadoxDefinitionSearcher : QueryExecutorBase<ParadoxScriptDefinitionElement, ParadoxDefinitionSearch.SearchParameters>() {
	override fun processQuery(queryParameters: ParadoxDefinitionSearch.SearchParameters, consumer: Processor<in ParadoxScriptDefinitionElement>) {
		val name = queryParameters.name
		val typeExpression = queryParameters.typeExpression
		val project = queryParameters.project
		val scope = GlobalSearchScopeUtil.toGlobalSearchScope(queryParameters.scope, project)
		if(typeExpression == null) {
			if(name == null) {
				//查找所有定义
				ParadoxDefinitionNameIndex.processAllElementsByKeys(project, scope) { _, it ->
					consumer.process(it)
				}
			} else {
				//按照名字查找定义
				ParadoxDefinitionNameIndex.processAllElements(name, project, scope) {
					consumer.process(it)
				}
			}
			return
		}
		//按照类型表达式查找定义
		for(expression in typeExpression.split('|')) {
			val (type, subtype) = ParadoxDefinitionTypeExpression.resolve(expression)
			ParadoxDefinitionTypeIndex.processAllElements(type, project, scope) {
				if(name != null && !matchesName(it, name)) return@processAllElements true
				if(subtype != null && !matchesSubtype(it, subtype)) return@processAllElements true
				consumer.process(it)
			}
		}
	}
	
	private fun matchesName(element: ParadoxScriptDefinitionElement, name: String): Boolean {
		return ParadoxDefinitionHandler.getName(element) == name
	}
	
	private fun matchesSubtype(element: ParadoxScriptDefinitionElement, subtype: String): Boolean {
		return ParadoxDefinitionHandler.getSubtypes(element)?.contains(subtype) == true
	}
}
