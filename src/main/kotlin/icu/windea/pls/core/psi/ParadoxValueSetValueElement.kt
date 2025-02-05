package icu.windea.pls.core.psi

import com.intellij.codeInsight.highlighting.*
import com.intellij.navigation.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import icons.*
import icu.windea.pls.*
import icu.windea.pls.core.navigation.*
import icu.windea.pls.model.*
import java.util.*
import javax.swing.*

/**
 * 值集值值并不存在一个真正意义上的声明处，用这个模拟。
 */
class ParadoxValueSetValueElement(
    parent: PsiElement,
    private val name: String,
    val valueSetNames: Set<String>,
    val readWriteAccess: ReadWriteAccessDetector.Access,
    val gameType: ParadoxGameType,
    private val project: Project,
) : ParadoxFakePsiElement(parent) {
    constructor(parent: PsiElement, name: String, valueSetName: String, readWriteAccess: ReadWriteAccessDetector.Access, gameType: ParadoxGameType, project: Project)
        : this(parent, name, setOf(valueSetName), readWriteAccess, gameType, project)
    
    constructor(parent: PsiElement, info: ParadoxValueSetValueInfo, project: Project)
        : this(parent, info.name, info.valueSetName, info.readWriteAccess, info.gameType, project)
    
    val valueSetName = valueSetNames.first()
    
    override fun getIcon(): Icon {
        val valueSetName = valueSetNames.first() //first is ok
        return PlsIcons.ValueSetValue(valueSetName)
    }
    
    override fun getName(): String {
        return name
    }
    
    override fun getTypeName(): String {
        val valueSetName = valueSetNames.first() //first is ok
        return when(valueSetName) {
            "variable" -> PlsBundle.message("script.description.variable")
            else -> PlsBundle.message("script.description.valueSetValue")
        }
    }
    
    override fun getText(): String {
        return name
    }
    
    override fun getPresentation(): ItemPresentation {
        return ParadoxValueSetValueElementPresentation(this)
    }
    
    override fun getProject(): Project {
        return project
    }
    
    override fun equals(other: Any?): Boolean {
        return other is ParadoxValueSetValueElement &&
            name == other.name &&
            valueSetNames.any { it in other.valueSetNames } &&
            project == other.project &&
            gameType == other.gameType
    }
    
    override fun hashCode(): Int {
        return Objects.hash(name, project, gameType)
    }
}
