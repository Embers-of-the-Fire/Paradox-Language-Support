package icu.windea.pls.extension.diagram

import com.intellij.diagram.*
import com.intellij.openapi.actionSystem.*
import com.intellij.psi.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.extension.diagram.provider.*

abstract class ParadoxDiagramElementManager(val provider: ParadoxDiagramProvider): DiagramElementManagerEx<PsiElement>() {
    override fun getProvider(): DiagramProvider<PsiElement>? {
        return provider
    }
    
    override fun findInDataContext(context: DataContext): PsiElement? {
        //rootFile
        val file = context.getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
        val project = context.getData(CommonDataKeys.PROJECT) ?: return null
        val rootInfo = file.fileInfo?.rootInfo ?: return null
        if(rootInfo.gameType != provider.gameType) return null
        val rootFile = rootInfo.rootFile
        return rootFile.toPsiDirectory(project)
    }
}
