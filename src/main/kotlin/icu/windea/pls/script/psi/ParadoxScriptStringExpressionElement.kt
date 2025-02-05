package icu.windea.pls.script.psi

import com.intellij.openapi.util.*
import com.intellij.psi.*
import icu.windea.pls.core.psi.*

/**
 * @see ParadoxScriptPropertyKey
 * @see ParadoxScriptString
 */
interface ParadoxScriptStringExpressionElement : ParadoxScriptExpressionElement, ContributedReferenceHost, ParadoxLanguageInjectionHost {
    override fun getName(): String
    
    override val value: String
    
    override fun setValue(value: String): ParadoxScriptStringExpressionElement
    
    override fun isValidHost(): Boolean {
        return true
    }
    
    override fun updateText(text: String): ParadoxScriptStringExpressionElement {
        return this.setValue(text)
    }
    
    override fun createLiteralTextEscaper(): LiteralTextEscaper<ParadoxScriptStringExpressionElement> {
        return object : LiteralTextEscaper<ParadoxScriptStringExpressionElement>(this) {
            override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
                outChars.append(rangeInsideHost.substring(myHost.text))
                return true
            }
            
            override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
                return rangeInsideHost.startOffset + offsetInDecoded
            }
            
            override fun isOneLine(): Boolean {
                return myHost is ParadoxScriptPropertyKey
            }
        }
    }
}
