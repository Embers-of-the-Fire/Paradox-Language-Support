package icu.windea.pls.core.codeInsight

import com.intellij.codeInsight.*
import com.intellij.psi.*
import icu.windea.pls.core.*

class ParadoxContainerProvider : ContainerProvider {
    override fun getContainer(item: PsiElement): PsiElement? {
        if(item.language.isParadoxLanguage()) {
            //no implementation, only containingFile
            return item.containingFile
        }
        return null
    }
}