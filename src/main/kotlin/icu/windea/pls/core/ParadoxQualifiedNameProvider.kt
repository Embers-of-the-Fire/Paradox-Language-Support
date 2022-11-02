package icu.windea.pls.core

import com.intellij.ide.actions.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import icu.windea.pls.localisation.psi.*
import icu.windea.pls.script.psi.*

//用于复制路径/引用（Edit > Copy Path/Reference...）

/**
 * * 如果是封装变量名：返回封装变量的名字
 * * 如果是定义：返回定义的名字
 * * 如果是定义元素：返回定义元素的路径（保留括起的双引号）
 * * 如果是本地化：返回本地化的键名
 * * 忽略非定义、非定义元素的脚本属性，以及非本地化的本地化属性
 */
class ParadoxQualifiedNameProvider : QualifiedNameProvider {
	override fun adjustElementToCopy(element: PsiElement): PsiElement? {
		return null
	}
	
	override fun getQualifiedName(element: PsiElement): String? {
		when {
			element is ParadoxScriptVariable -> return element.name
			element is ParadoxScriptProperty -> {
				val definitionInfo = element.definitionInfo
				if(definitionInfo != null) return definitionInfo.name
				val definitionElementInfo = element.definitionElementInfo
				if(definitionElementInfo != null) return definitionElementInfo.elementPath.toString()
				return element.name
			}
			element is ParadoxScriptPropertyKey -> return getQualifiedName(element.parent)
			element is ParadoxLocalisationProperty -> return element.name
			element is ParadoxLocalisationPropertyKey -> return getQualifiedName(element.parent)
			else -> return null
		}
	}
	
	override fun qualifiedNameToElement(fqn: String, project: Project): PsiElement? {
		return null //不处理
	}
}