package icu.windea.pls.core.settings

import com.intellij.navigation.*
import com.intellij.openapi.project.*
import com.intellij.openapi.roots.*
import com.intellij.openapi.vfs.*
import icons.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import javax.swing.*

class ParadoxLibrary(val project: Project) : SyntheticLibrary(), ItemPresentation {
    @Volatile var roots: MutableSet<VirtualFile> = mutableSetOf()
    
    fun computeRoots(): MutableSet<VirtualFile> {
        val newRoots = mutableSetOf<VirtualFile>()
        val projectFileIndex = ProjectFileIndex.getInstance(project)
        val allModSettings = getAllModSettings()
        for(modSettings in allModSettings.settings.values) {
            val modDirectory = modSettings.modDirectory ?: continue
            val modFile = modDirectory.toVirtualFile(true) ?: continue
            if(!modFile.isValid) continue
            if(!projectFileIndex.isInContent(modFile)) continue
            //newRoots.add(modFile) //unnecessary
            run {
                val gameDirectory = modSettings.gameDirectory ?: return@run
                val gameFile = gameDirectory.toVirtualFile(true) ?: return@run
                if(!gameFile.isValid) return@run
                newRoots.add(gameFile)
            }
            for(modDependencySettings in modSettings.modDependencies) {
                val modDependencyDirectory = modDependencySettings.modDirectory ?: continue
                if(modDependencyDirectory == modDirectory) continue //需要排除这种情况
                val modDependencyFile = modDependencyDirectory.toVirtualFile(true) ?: continue
                if(!modDependencyFile.isValid) continue
                newRoots.add(modDependencyFile)
            }
        }
        return newRoots
    }
    
    override fun getSourceRoots(): Collection<VirtualFile> {
        return roots
    }
    
    override fun isShowInExternalLibrariesNode(): Boolean {
        return true
    }
    
    override fun getIcon(unused: Boolean): Icon {
        return PlsIcons.Library
    }
    
    override fun getPresentableText(): String {
        return PlsBundle.message("library.name")
    }
    
    override fun equals(other: Any?): Boolean {
        return this === other || (other is ParadoxLibrary && project == other.project)
    }
    
    override fun hashCode(): Int {
        return project.hashCode()
    }
}