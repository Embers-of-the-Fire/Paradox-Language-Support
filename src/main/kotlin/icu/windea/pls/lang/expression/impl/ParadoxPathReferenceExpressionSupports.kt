package icu.windea.pls.lang.expression.impl

import com.intellij.psi.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.lang.cwt.expression.*
import icu.windea.pls.lang.expression.*

/**
 * @see CwtDataType.Icon
 */
class ParadoxIconReferenceExpressionSupport : ParadoxPathReferenceExpressionSupport() {
    override fun supports(configExpression: CwtDataExpression): Boolean {
        return configExpression.type == CwtDataType.Icon
    }
    
    override fun matches(configExpression: CwtDataExpression, element: PsiElement?, filePath: String): Boolean {
        val expression = configExpression.value ?: return false
        return expression.matchesPath(filePath, trim = true) && filePath.endsWith(".dds", true)
    }
    
    override fun extract(configExpression: CwtDataExpression, element: PsiElement?, filePath: String, ignoreCase: Boolean): String? {
        val expression = configExpression.value ?: return null
        return filePath.removeSurroundingOrNull(expression, ".dds", ignoreCase)?.substringAfterLast('/')
    }
    
    override fun resolvePath(configExpression: CwtDataExpression, pathReference: String): String? {
        return null //信息不足
    }
    
    override fun resolveFileName(configExpression: CwtDataExpression, pathReference: String): String {
        return pathReference.substringAfterLast('/') + ".dds"
    }
    
    override fun getUnresolvedMessage(configExpression: CwtDataExpression, pathReference: String): String {
        return PlsBundle.message("inspection.script.general.unresolvedPathReference.description.icon", pathReference, configExpression)
    }
}

/**
 * @see CwtDataType.FilePath
 */
class ParadoxFilePathReferenceExpressionSupport : ParadoxPathReferenceExpressionSupport() {
    override fun supports(configExpression: CwtDataExpression): Boolean {
        return configExpression.type == CwtDataType.FilePath
    }
    
    //filepath[./] - 匹配相对于脚本文件所在目录的路径
    
    override fun matches(configExpression: CwtDataExpression, element: PsiElement?, filePath: String): Boolean {
        var expression = configExpression.value ?: return true
        val expressionRel = expression.removePrefixOrNull("./")
        if(expressionRel != null) {
            val contextParentPath = element?.fileInfo?.path?.parent ?: return false
            expression = "$contextParentPath/$expressionRel"
        }
        val index = expression.lastIndexOf(',') //","应当最多出现一次
        if(index == -1) {
            //匹配父路径
            return expression.matchesPath(filePath, trim = true)
        } else {
            //匹配父路径+文件名前缀+扩展名
            val parentAndFileNamePrefix = expression.substring(0, index)
            if(!filePath.startsWith(parentAndFileNamePrefix)) return false
            val fileNameSuffix = expression.substring(index + 1)
            return filePath.endsWith(fileNameSuffix)
        }
    }
    
    override fun extract(configExpression: CwtDataExpression, element: PsiElement?, filePath: String, ignoreCase: Boolean): String? {
        var expression = configExpression.value ?: return filePath
        val expressionRel = expression.removePrefixOrNull("./")
        if(expressionRel != null) {
            val contextParentPath = element?.fileInfo?.path?.parent ?: return null
            expression = "$contextParentPath/$expressionRel"
        }
        val index = expression.lastIndexOf(',') //","应当最多出现一次
        if(index == -1) {
            return filePath.removePrefixOrNull(expression, ignoreCase)?.trimFast('/')
        } else {
            //optimized
            val l1 = index
            if(!filePath.regionMatches(0, expression, 0, l1, ignoreCase)) return null
            val l2 = expression.length - index - 1
            if(!filePath.regionMatches(filePath.length - l2, expression, index + 1, l2, ignoreCase)) return null
            return filePath.substring(l1, filePath.length - l2).trimFast('/')
            //val s1 = expression.substring(0, index)
            //val s2 = expression.substring(index + 1)
            //return filePath.removeSurroundingOrNull(s1, s2, ignoreCase)?.trimFast('/')
        }
    }
    
    override fun resolvePath(configExpression: CwtDataExpression, pathReference: String): String? {
        val expression = configExpression.value ?: return pathReference
        val expressionRel = expression.removePrefixOrNull("./")
        if(expressionRel != null) {
            return null //信息不足
        }
        val index = configExpression.lastIndexOf(',') //","应当最多出现一次
        if(index == -1) {
            if(expression.endsWith('/')) {
                return expression + pathReference
            } else {
                return "$expression/$pathReference"
            }
        } else {
            return expression.replace(",", pathReference)
        }
    }
    
    override fun resolveFileName(configExpression: CwtDataExpression, pathReference: String): String {
        val expression = configExpression.value ?: return pathReference.substringAfterLast('/')
        val index = expression.lastIndexOf(',') //","应当最多出现一次
        if(index == -1) {
            return pathReference.substringAfterLast('/')
        } else {
            return expression.replace(",", pathReference).substringAfterLast('/')
        }
    }
    
    override fun getUnresolvedMessage(configExpression: CwtDataExpression, pathReference: String): String {
        return PlsBundle.message("inspection.script.general.unresolvedPathReference.description.filePath", pathReference, configExpression)
    }
}

/**
 * @see CwtDataType.FileName
 */
class ParadoxFileNameReferenceExpressionSupport : ParadoxPathReferenceExpressionSupport() {
    override fun supports(configExpression: CwtDataExpression): Boolean {
        return configExpression.type == CwtDataType.FileName
    }
    
    //filename - filePath需要是文件名
    //filename[foo/bar] - filePath需要是文件名且该文件需要位于目录foo/bar或其子目录下
    
    override fun matches(configExpression: CwtDataExpression, element: PsiElement?, filePath: String): Boolean {
        val expression = configExpression.value ?: return true
        return expression.matchesPath(filePath, trim = true)
    }
    
    override fun extract(configExpression: CwtDataExpression, element: PsiElement?, filePath: String, ignoreCase: Boolean): String {
        return filePath.substringAfterLast('/')
    }
    
    override fun resolvePath(configExpression: CwtDataExpression, pathReference: String): String? {
        return null //信息不足
    }
    
    override fun resolveFileName(configExpression: CwtDataExpression, pathReference: String): String {
        return pathReference.substringAfterLast('/')
    }
    
    override fun getUnresolvedMessage(configExpression: CwtDataExpression, pathReference: String): String {
        return PlsBundle.message("inspection.script.general.unresolvedPathReference.description.fileName", pathReference, configExpression)
    }
}