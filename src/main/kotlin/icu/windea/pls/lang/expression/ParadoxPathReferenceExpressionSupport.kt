package icu.windea.pls.lang.expression

import com.intellij.openapi.extensions.*
import com.intellij.psi.*
import icu.windea.pls.core.index.*
import icu.windea.pls.lang.cwt.expression.*

/**
 * 提供对路径引用表达式的支持。
 *
 * 用于实现如何匹配、解析脚本文件中使用的路径表达式，以及如何基于文件路径索引进行代码补全等功能。
 *
 * @see ParadoxFilePathIndex
 */
abstract class ParadoxPathReferenceExpressionSupport {
    abstract fun supports(configExpression: CwtDataExpression): Boolean
    
    /**
     * 判断指定的文件路径表达式是否匹配另一个相对于游戏或模组目录根路径的路径。
     */
    abstract fun matches(configExpression: CwtDataExpression, element: PsiElement?, filePath: String): Boolean
    
    /**
     * 根据指定的文件路径表达式，从精确路径中提取出需要的作为值的字符串。即脚本文件中使用的路径表达式。
     * @param configExpression 对应的CWT规则表达式。拥有数种写法的文件路径表达式。
     * @param ignoreCase 匹配时是否需要忽略大小写。
     */
    abstract fun extract(configExpression: CwtDataExpression, element: PsiElement?, filePath: String, ignoreCase: Boolean = false): String?
    
    /**
     * 解析指定的文件路径表达式，得到文件路径。如果返回null则表示无法仅基于这些参数得到完整的文件路径。
     * @param configExpression 对应的CWT规则表达式。拥有数种写法的文件路径表达式。
     * @param pathReference 作为值的字符串。即脚本文件中使用的路径表达式。
     */
    abstract fun resolvePath(configExpression: CwtDataExpression, pathReference: String): String?
    
    /**
     * 解析指定的文件路径表达式，得到文件名。
     * @param configExpression 对应的CWT规则表达式。拥有数种写法的文件路径表达式。
     * @param pathReference 作为值的字符串。即脚本文件中使用的路径表达式。
     */
    abstract fun resolveFileName(configExpression: CwtDataExpression, pathReference: String): String
    
    abstract fun getUnresolvedMessage(configExpression: CwtDataExpression, pathReference: String): String
    
    companion object INSTANCE {
        val EP_NAME = ExtensionPointName.create<ParadoxPathReferenceExpressionSupport>("icu.windea.pls.pathReferenceExpressionSupport")
        
        fun get(configExpression: CwtDataExpression): ParadoxPathReferenceExpressionSupport? {
            return EP_NAME.extensionList.find { ep ->
                ep.supports(configExpression)
            }
        }
        
    }
}



