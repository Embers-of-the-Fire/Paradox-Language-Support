package com.windea.plugin.idea.paradox.core.rule

import com.intellij.openapi.vfs.*
import com.windea.plugin.idea.paradox.*
import org.yaml.snakeyaml.*
import java.io.*
import java.util.concurrent.*

/**
 * Paradox规则组映射的提供器。
 */
class ParadoxRuleGroupProvider {
	private val yaml = Yaml()
	
	internal val ruleGroups: MutableMap<String, ParadoxRuleGroup>
	internal val ruleGroupsCache: ParadoxRuleGroupCache
	
	init {
		ruleGroups = ConcurrentHashMap<String,ParadoxRuleGroup>()
		addRuleGroups()
		ruleGroupsCache = ParadoxRuleGroupCache(ruleGroups)
	}
	
	private fun addRuleGroups() {
		val rulesUrl = rulesPath.toClassPathResource()?:error("Paradox rules path is not exist.")
		val rulesFile = VfsUtil.findFileByURL(rulesUrl)?:error("Paradox rules path is not exist.")
		val coreGroup = mutableMapOf<String, Map<String, Any>>()
		val coreGroupName = corePath
		for(child in rulesFile.children) {
			if(child.isDirectory){
				val group = mutableMapOf<String, Map<String, Any>>()
				val groupName = child.name
				for(ruleFile in child.children) {
					if(ruleFile.extension == ruleFileExtension){
						val rule = getRule(ruleFile.inputStream)
						group.putAll(rule)
					}
				}
				ruleGroups[groupName] = ParadoxRuleGroup(group)
			}else{
				val ruleFile = child
				if(ruleFile.extension == ruleFileExtension){
					val rule = getRule(ruleFile.inputStream)
					coreGroup.putAll(rule)
				}
			}
		}
		ruleGroups[coreGroupName] = ParadoxRuleGroup(coreGroup)
	}
	
	private fun getRule(inputStream: InputStream): Map<String, Map<String, Any>> {
		try {
			return extractRule(inputStream)
		} catch(e: Exception) {
			return emptyMap()
		}
	}
	
	private fun extractRule(inputStream: InputStream): Map<String, Map<String,Any>> {
		return yaml.load(inputStream) ?: emptyMap()
	}
}