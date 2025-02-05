package icu.windea.pls.localisation.psi

import com.intellij.lang.*
import com.intellij.lang.ParserDefinition.SpaceRequirements.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import icu.windea.pls.localisation.psi.ParadoxLocalisationElementTypes.*

class ParadoxLocalisationParserDefinition : ParserDefinition {
    override fun getWhitespaceTokens() = ParadoxLocalisationTokenSets.WHITE_SPACES
    
    override fun getCommentTokens() = ParadoxLocalisationTokenSets.COMMENTS
    
    override fun getStringLiteralElements() = ParadoxLocalisationTokenSets.STRING_LITERALS
    
    override fun getFileNodeType() = ParadoxLocalisationFile.ELEMENT_TYPE
    
    override fun createFile(viewProvider: FileViewProvider): ParadoxLocalisationFile {
        return ParadoxLocalisationFile(viewProvider)
    }
    
    override fun createElement(node: ASTNode): PsiElement {
        return Factory.createElement(node)
    }
    
    override fun createParser(project: Project?): ParadoxLocalisationParser {
        return ParadoxLocalisationParser()
    }
    
    override fun createLexer(project: Project?): ParadoxLocalisationLexer {
        return ParadoxLocalisationLexer()
    }
    
    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements {
        val leftType = left?.elementType
        val rightType = right?.elementType
        return when {
            leftType == COMMENT -> MUST_LINE_BREAK
            leftType == LOCALE_ID && rightType == COLON -> MUST_NOT
            rightType == LOCALE_ID -> MUST_LINE_BREAK
            rightType == PROPERTY_KEY_TOKEN -> MUST_LINE_BREAK
            leftType == COLORFUL_TEXT_START && rightType == COLOR_ID -> MUST_NOT
            leftType == ICON_START || rightType == ICON_END -> MUST_NOT 
            leftType == PROPERTY_REFERENCE_START || rightType == PROPERTY_REFERENCE_END -> MUST_NOT
            leftType == PIPE || rightType == PIPE -> MUST_NOT
            else -> MAY
        }
    }
}


