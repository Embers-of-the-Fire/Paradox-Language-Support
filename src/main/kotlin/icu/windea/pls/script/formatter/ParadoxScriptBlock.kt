package icu.windea.pls.script.formatter

import com.intellij.formatting.*
import com.intellij.formatting.Indent
import com.intellij.lang.*
import com.intellij.psi.*
import com.intellij.psi.codeStyle.*
import com.intellij.psi.formatter.common.*
import com.intellij.psi.tree.*
import com.intellij.util.*
import icu.windea.pls.core.*
import icu.windea.pls.script.*
import icu.windea.pls.script.codeStyle.*
import icu.windea.pls.script.psi.ParadoxScriptElementTypes.*

class ParadoxScriptBlock(
	node: ASTNode,
	private val settings: CodeStyleSettings,
) : AbstractBlock(node, createWrap(), createAlignment()) {
	companion object {
		private val separatorTokens = TokenSet.create(EQUAL_SIGN, NOT_EQUAL_SIGN, LT_SIGN, GT_SIGN, LE_SIGN, GE_SIGN)
		private val inlineMathOperatorTokens = TokenSet.create(PLUS_SIGN, MINUS_SIGN, TIMES_SIGN, DIV_SIGN, MOD_SIGN, LABS_SIGN, RABS_SIGN, LP_SIGN, RP_SIGN)
		private val shouldIndentParentTypes = TokenSet.create(BLOCK, PARAMETER_CONDITION)
		private val shouldIndentTypes = TokenSet.create(VARIABLE, PROPERTY, BOOLEAN, INT, FLOAT, STRING, COLOR, INLINE_MATH, PARAMETER, PARAMETER_CONDITION, COMMENT)
		private val shouldChildIndentTypes = TokenSet.create(BLOCK, PARAMETER_CONDITION, PARAMETER_CONDITION_EXPRESSION)
		
		private fun createWrap(): Wrap? {
			return null
		}
		
		private fun createAlignment(): Alignment? {
			return null
		}
		
		private fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
			//变量声明分隔符周围的空格，属性分隔符周围的空格
			val customSettings = settings.getCustomSettings(ParadoxScriptCodeStyleSettings::class.java)
			return SpacingBuilder(settings, ParadoxScriptLanguage)
				.aroundInside(separatorTokens, VARIABLE).spaceIf(customSettings.SPACE_AROUND_VARIABLE_SEPARATOR) //间隔符周围按情况可能需要空格
				.aroundInside(separatorTokens, PROPERTY).spaceIf(customSettings.SPACE_AROUND_PROPERTY_SEPARATOR) //间隔符周围按情况可能需要空格
				.around(inlineMathOperatorTokens).spaceIf(customSettings.SPACE_AROUND_INLINE_MATH_OPERATOR) //内联数学表达式操作符周围按情况可能需要空格
				.between(LEFT_BRACE, RIGHT_BRACE).none()//花括号之间总是不需要空格
				.withinPair(LEFT_BRACE, RIGHT_BRACE).spaceIf(customSettings.SPACE_WITHIN_BRACES) //花括号内侧如果非换行按情况可能需要空格
				.between(NESTED_LEFT_BRACKET, NESTED_RIGHT_BRACKET).none() //参数条件表达式如果为空则不需要空格（尽管这是语法错误）
				.withinPair(NESTED_LEFT_BRACKET, NESTED_RIGHT_BRACKET).spaceIf(customSettings.SPACE_WITHIN_PARAMETER_CONDITION_EXPRESSION_BRACKETS) //参数条件表达式内侧如果非换行按情况可能需要空格
				.between(NESTED_RIGHT_BRACKET, RIGHT_BRACKET).none() //参数条件代码块如果为空则不需要空格
				.withinPair(NESTED_RIGHT_BRACKET, RIGHT_BRACKET).spaceIf(customSettings.SPACE_WITHIN_PARAMETER_CONDITION_BRACKETS) //参数条件代码块内侧如果非换行按情况可能需要空格
				.between(INLINE_MATH_START, INLINE_MATH_END).none() //内联数字表达式如果为空则不需要空格（尽管这是语法错误）
				.withinPair(INLINE_MATH_START, INLINE_MATH_END).spaceIf(customSettings.SPACE_WITHIN_INLINE_MATH_BRACKETS) //内联数学表达式内侧如果非换行按情况可能需要空格
		}
	}
	
	private val spacingBuilder = createSpacingBuilder(settings)
	
	override fun buildChildren(): List<Block> {
		val children = SmartList<Block>()
		myNode.processChild { node -> node.takeUnless(TokenType.WHITE_SPACE)?.let { ParadoxScriptBlock(it, settings) }?.addTo(children).end() }
		return children
	}
	
	override fun getIndent(): Indent? {
		//配置缩进
		//block和parameter_condition中的variable、property、value、parameter_condition和comment需要缩进
		val elementType = myNode.elementType
		val parentElementType = myNode.treeParent?.elementType
		return when {
			parentElementType in shouldIndentParentTypes && elementType in shouldIndentTypes -> Indent.getNormalIndent()
			else -> Indent.getNoneIndent()
		}
	}
	
	override fun getChildIndent(): Indent? {
		//配置换行时的自动缩进
		//在file和rootBlock中不要缩进
		//在block、parameter_condition、parameter_condition_expression中需要缩进
		val elementType = myNode.elementType
		return when {
			elementType is IFileElementType -> Indent.getNoneIndent()
			elementType == ROOT_BLOCK -> Indent.getNoneIndent()
			elementType in shouldChildIndentTypes -> Indent.getNormalIndent()
			else -> null
		}
	}
	
	override fun getSpacing(child1: Block?, child2: Block): Spacing? {
		return spacingBuilder.getSpacing(this, child1, child2)
	}
	
	override fun isLeaf(): Boolean {
		//顶级块不是叶子节点
		return myNode.firstChildNode == null
	}
}
