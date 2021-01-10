package com.windea.plugin.idea.paradox.localisation.psi

import com.intellij.openapi.project.*
import com.intellij.psi.search.*
import com.intellij.psi.stubs.*
import com.intellij.util.CommonProcessors.*
import com.windea.plugin.idea.paradox.*

object ParadoxLocalisationPropertyKeyIndex : StringStubIndexExtension<ParadoxLocalisationProperty>() {
	private val key = StubIndexKey.createIndexKey<String, ParadoxLocalisationProperty>("paradoxLocalisation.property.index")
	
	override fun getKey() = key
	
	override fun getCacheSize() = 100 * 1024 //50000+
	
	fun getOne(name: String, locale: ParadoxLocale?, project: Project, scope: GlobalSearchScope,defaultToFirst:Boolean): ParadoxLocalisationProperty? {
		val elements = StubIndex.getElements(this.key, name, project, scope, ParadoxLocalisationProperty::class.java)
		for(element in elements) {
			if(locale == null || locale == element.paradoxLocale) return element
		}
		return if(defaultToFirst) elements.firstOrNull() else null
	}
	
	fun getAll(name: String, locale: ParadoxLocale?, project: Project, scope: GlobalSearchScope,defaultToAll:Boolean): List<ParadoxLocalisationProperty> {
		val result = mutableListOf<ParadoxLocalisationProperty>()
		var index = 0
		val elements = StubIndex.getElements(this.key, name, project, scope, ParadoxLocalisationProperty::class.java)
		for(element in elements) {
			val elementLocale = element.paradoxLocale
			if(locale == null) {
				//需要将用户的语言区域对应的本地化属性放到该组本地化属性的最前面
				if(elementLocale == inferredParadoxLocale) {
					result.add(index++, element)
				} else {
					result.add(element)
				}
			} else {
				if(locale == elementLocale) {
					result.add(element)
				}
			}
		}
		return if(defaultToAll && result.isEmpty()) elements.toList() else result
	}
	
	fun getAll(names:Iterable<String>,locale: ParadoxLocale?, project: Project, scope: GlobalSearchScope,keepOrder:Boolean): List<ParadoxLocalisationProperty> {
		val result = mutableListOf<ParadoxLocalisationProperty>()
		var index = 0
		val keys = getAllKeys(project)
		for(key in keys) {
			if(key in names) {
				val group = get(key, project, scope)
				val nextIndex = index + group.size
				for(element in group) {
					val elementLocale = element.paradoxLocale
					if(locale == null) {
						//需要将用户的语言区域对应的本地化属性放到该组本地化属性的最前面
						if(elementLocale == inferredParadoxLocale) {
							result.add(index++, element)
						} else {
							result.add(element)
						}
					} else {
						if(locale == elementLocale) {
							result.add(element)
						}
					}
				}
				index = nextIndex
			}
		}
		if(keepOrder) result.sortBy { names.indexOf(it.name) } 
		return result
	}
	
	fun getAll(locale: ParadoxLocale?, project: Project, scope: GlobalSearchScope): List<ParadoxLocalisationProperty> {
		val result = mutableListOf<ParadoxLocalisationProperty>()
		var index = 0
		val keys = getAllKeys(project)
		for(key in keys) {
			val group = get(key, project, scope)
			val nextIndex = index + group.size
			for(element in group) {
				val elementLocale = element.paradoxLocale
				if(locale == null) {
					//需要将用户的语言区域对应的本地化属性放到该组本地化属性的最前面
					if(elementLocale == inferredParadoxLocale) {
						result.add(index++, element)
					} else {
						result.add(element)
					}
				} else if(locale == elementLocale) {
					result.add(element)
				}
			}
			index = nextIndex
		}
		return result
	}
	
	fun getAll1(locale: ParadoxLocale?, project: Project, scope: GlobalSearchScope): List<ParadoxLocalisationProperty> {
		val result = mutableListOf<ParadoxLocalisationProperty>()
		var index = 0
		val processor = CollectProcessor<String>()
		processAllKeys(project,processor)
		val keys = processor.results
		for(key in keys) {
			val group = get(key, project, scope)
			val nextIndex = index + group.size
			for(element in group) {
				val elementLocale = element.paradoxLocale
				if(locale == null) {
					//需要将用户的语言区域对应的本地化属性放到该组本地化属性的最前面
					if(elementLocale == inferredParadoxLocale) {
						result.add(index++, element)
					} else {
						result.add(element)
					}
				} else if(locale == elementLocale) {
					result.add(element)
				}
			}
			index = nextIndex
		}
		return result
	}
	
	inline fun filter(locale: ParadoxLocale?, project: Project, scope: GlobalSearchScope, predicate: (String) -> Boolean): List<ParadoxLocalisationProperty> {
		val result = mutableListOf<ParadoxLocalisationProperty>()
		var index = 0
		val keys = getAllKeys(project)
		for(key in keys) {
			if(predicate(key)) {
				val group = get(key, project, scope)
				val nextIndex = index + group.size
				for(element in group) {
					val elementLocale = element.paradoxLocale
					if(locale == null) {
						//需要将用户的语言区域对应的本地化属性放到该组本地化属性的最前面
						if(elementLocale == inferredParadoxLocale) {
							result.add(index++, element)
						} else {
							result.add(element)
						}
					} else {
						if(locale == elementLocale) {
							result.add(element)
						}
					}
				}
				index = nextIndex
			}
		}
		return result
	}
}
