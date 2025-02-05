package icu.windea.pls.model

import com.intellij.psi.*
import com.intellij.util.ui.*
import icu.windea.pls.core.*
import icu.windea.pls.lang.documentation.*
import icu.windea.pls.script.psi.*
import java.awt.*

data class ParadoxTextColorInfo(
	val name: String,
	val gameType: ParadoxGameType,
	val pointer: SmartPsiElementPointer<ParadoxScriptProperty>,
	val r: Int,
	val g: Int,
	val b: Int
) {
	val color: Color = Color(r, g, b)
	val icon = ColorIcon(16, color)
	
	val text = buildString {
		append(name).append(" = { ").append(r).append(" ").append(g).append(" ").append(b).append(" }")
		val message = ParadoxExtendedDocumentationBundle.message(gameType, name, "textcolor")
		if(message.isNotNullOrEmpty()) append(" (").append(message).append(")")
	}
}

