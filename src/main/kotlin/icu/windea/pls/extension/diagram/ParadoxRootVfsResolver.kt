package icu.windea.pls.extension.diagram

import com.intellij.diagram.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import icu.windea.pls.*
import icu.windea.pls.core.*

object ParadoxRootVfsResolver : DiagramVfsResolver<PsiElement> {
    //based on rootFile
    
    override fun getQualifiedName(element: PsiElement?): String? {
        if(element == null) return null
        val rootInfo = element.fileInfo?.rootInfo ?: return null
        val rootPath = rootInfo.rootFile.path
        return rootPath
    }
    
    override fun resolveElementByFQN(s: String, project: Project): PsiDirectory? {
        return try {
            s.toVirtualFile()?.toPsiDirectory(project)
        } catch(e: Exception) {
            null
        }
    }
}