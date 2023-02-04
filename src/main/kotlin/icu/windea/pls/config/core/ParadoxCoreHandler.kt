package icu.windea.pls.config.core

import com.intellij.codeInsight.hints.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileTypes.ex.*
import com.intellij.openapi.project.*
import com.intellij.openapi.vfs.*
import com.intellij.openapi.vfs.newvfs.impl.*
import com.intellij.psi.*
import com.intellij.psi.search.*
import com.intellij.psi.util.*
import com.intellij.util.*
import com.intellij.util.indexing.*
import icu.windea.pls.*
import icu.windea.pls.config.core.config.*
import icu.windea.pls.core.*
import java.lang.invoke.*
import java.util.*

object ParadoxCoreHandler {
    private val logger = Logger.getInstance(MethodHandles.lookup().lookupClass())
    
    fun shouldIndexFile(virtualFile: VirtualFile): Boolean {
        try {
            //仅索引有根目录的文件
            val fileInfo = virtualFile.fileInfo ?: return false
            val rootType = fileInfo.rootInfo.rootType
            val path = fileInfo.path.path
            //仅索引游戏或模组根目录下的文件
            if(rootType != ParadoxRootType.Game && rootType != ParadoxRootType.Mod) return false
            //不索引内联脚本文件
            if("common/inline_scripts".matchesPath(path)) {
                return false
            }
            return true
        } catch(e: Exception) {
            return false
        }
    }
    
    fun getFileInfo(virtualFile: VirtualFile): ParadoxFileInfo? {
        return virtualFile.getCopyableUserData(PlsKeys.fileInfoKey)
    }
    
    fun getFileInfo(file: PsiFile): ParadoxFileInfo? {
        return file.originalFile.virtualFile?.let { getFileInfo(it) }
    }
    
    fun getFileInfo(element: PsiElement): ParadoxFileInfo? {
        return element.containingFile?.let { getFileInfo(it) }
    }
    
    @JvmStatic
    fun resolveRootInfo(rootFile: VirtualFile, canBeNotAvailable: Boolean = true): ParadoxRootInfo? {
        val rootInfo = rootFile.getUserData(PlsKeys.rootInfoKey)
        if(rootInfo != null && (canBeNotAvailable || rootInfo.isAvailable)) {
            ParadoxRootInfo.values.add(rootInfo)
            return rootInfo
        }
        ParadoxRootInfo.values.remove(rootInfo)
        val resolvedRootInfo = try {
            doResolveRootInfo(rootFile, canBeNotAvailable)
        } catch(e: Exception) {
            logger.warn(e)
            null
        }
        runCatching {
            rootFile.putUserData(PlsKeys.rootInfoKey, resolvedRootInfo)
        }
        if(resolvedRootInfo != null) {
            ParadoxRootInfo.values.add(resolvedRootInfo)
        }
        return resolvedRootInfo
    }
    
    private fun doResolveRootInfo(rootFile: VirtualFile, canBeNotAvailable: Boolean): ParadoxRootInfo? {
        if(rootFile is StubVirtualFile || !rootFile.isValid) return null
        if(!rootFile.isDirectory) return null
        val rootName = rootFile.name
        
        // 尝试向下查找descriptor.mod，如果找到，再尝试向下查找.{gameType}，确认rootType和gameType
        // descriptor.mod > Mod
        val descriptorModFile = rootFile.findChild(PlsConstants.descriptorFileName)
        if(descriptorModFile != null) {
            var markerFile: VirtualFile? = null
            for(rootChild in rootFile.children) {
                if(rootChild.isDirectory) continue
                if(!canBeNotAvailable && !rootChild.isValid) continue
                // .{gameType} > set game type
                if(ParadoxGameType.resolve(rootChild) != null) {
                    markerFile = rootChild
                    break
                }
            }
            val descriptorInfo = getDescriptorInfo(descriptorModFile) ?: return null
            return ParadoxModRootInfo(rootFile, descriptorModFile, markerFile, ParadoxRootType.Mod, descriptorInfo)
        }
        
        // 从此目录向下递归查找launcher-settings.json，如果找到，再根据"dlcPath"的值获取游戏文件的根目录
        // 或者判断此目录是否是特定的名字，然后再从此目录的父目录向下递归查找launcher-settings.json（参见其他的ParadoxRootType）
        // 注意游戏文件可能位于此目录的game子目录中，而非直接位于此目录中
        val rootTypeByRootName = ParadoxRootType.valueMapByRootName[rootName]
        if(rootTypeByRootName != null) {
            val rootFileParent = rootFile.parent ?: return null
            val launcherSettingsFile = getLauncherSettingsFile(rootFileParent) ?: return null
            val launcherSettingsInfo = getLauncherSettingsInfo(launcherSettingsFile) ?: return null
            return ParadoxGameRootInfo(rootFile, launcherSettingsFile, rootTypeByRootName, launcherSettingsInfo)
        } else {
            val launcherSettingsFile = getLauncherSettingsFile(rootFile) ?: return null
            val launcherSettingsInfo = getLauncherSettingsInfo(launcherSettingsFile) ?: return null
            return ParadoxGameRootInfo(rootFile, launcherSettingsFile, ParadoxRootType.Game, launcherSettingsInfo)
        }
    }
    
    private fun getLauncherSettingsFile(root: VirtualFile): VirtualFile? {
        var result: VirtualFile? = null
        VfsUtilCore.visitChildrenRecursively(root, object : VirtualFileVisitor<Void?>() {
            override fun visitFileEx(file: VirtualFile): Result {
                if(file.isDirectory) {
                    if(file.name.startsWith('.')) return SKIP_CHILDREN //skip .git, .idea, .vscode, etc.
                    return CONTINUE
                }
                if(file.name == PlsConstants.launcherSettingsFileName) {
                    result = file
                    return skipTo(root)
                }
                return CONTINUE
            }
        })
        return result
    }
    
    private fun getLauncherSettingsInfo(file: VirtualFile): ParadoxLauncherSettingsInfo? {
        return CachedValuesManager.getManager(getDefaultProject()).getCachedValue(file, PlsKeys.cachedLauncherSettingsInfoKey, {
            val value = ParadoxLauncherSettingsInfo.resolve(file)
            CachedValueProvider.Result.create(value, file)
        }, false)
    }
    
    private fun getDescriptorInfo(file: VirtualFile): ParadoxDescriptorInfo? {
        return CachedValuesManager.getManager(getDefaultProject()).getCachedValue(file, PlsKeys.cachedDescriptorInfoKey, {
            val value = ParadoxDescriptorInfo.resolve(file)
            CachedValueProvider.Result.create(value, file)
        }, false)
    }
    
    @JvmStatic
    fun resolveFileInfo(file: VirtualFile): ParadoxFileInfo? {
        val resolvedFileInfo = doResolveFileInfo(file)
        runCatching {
            file.putCopyableUserData(PlsKeys.fileInfoKey, resolvedFileInfo)
        }
        return resolvedFileInfo
    }
    
    @JvmStatic
    fun doResolveFileInfo(file: VirtualFile): ParadoxFileInfo? {
        if(file is StubVirtualFile || !file.isValid) return null
        val fileName = file.name
        val subPaths = LinkedList<String>()
        subPaths.addFirst(fileName)
        var currentFile: VirtualFile? = file.parent
        while(currentFile != null) {
            val rootInfo = resolveRootInfo(currentFile, false)
            if(rootInfo != null) {
                val path = ParadoxPath.resolve(subPaths)
                val fileType = ParadoxFileType.resolve(file, rootInfo.gameType, path)
                val fileInfo = ParadoxFileInfo(fileName, path, fileType, rootInfo)
                return fileInfo
            }
            subPaths.addFirst(currentFile.name)
            currentFile = currentFile.parent
        }
        return null
    }
    
    @JvmStatic
    fun reparseFilesInRoot(rootFile: VirtualFile) {
        //重新解析指定的根目录中的所有文件，包括非脚本非本地化文件
        try {
            FileTypeManagerEx.getInstanceEx().makeFileTypesChange("Root of paradox files $rootFile changed.") { }
        } catch(e: Exception) {
            //ignore
        } finally {
            //要求重新索引
            FileBasedIndex.getInstance().requestReindex(rootFile)
        }
    }
    
    @JvmStatic
    fun reparseFilesByFileNames(fileNames: Set<String>) {
        //重新解析指定的根目录中的所有文件，包括非脚本非本地化文件
        val files = mutableListOf<VirtualFile>()
        try {
            val project = getTheOnlyOpenOrDefaultProject()
            FilenameIndex.processFilesByNames(fileNames, true, GlobalSearchScope.allScope(project), null) { file ->
                files.add(file)
                true
            }
            FileContentUtil.reparseFiles(project, files, true)
        } catch(e: Exception) {
            //ignore
        } finally {
            //要求重新索引
            for(file in files) {
                FileBasedIndex.getInstance().requestReindex(file)
            }
        }
    }
    
    @Suppress("UnstableApiUsage")
    fun refreshInlayHints(predicate: (VirtualFile, Project) -> Boolean = { _, _ -> true }) {
        //当某些配置变更后，需要刷新内嵌提示
        //com.intellij.codeInsight.hints.VcsCodeAuthorInlayHintsProviderKt.refreshCodeAuthorInlayHints
        try {
            val openProjects = ProjectManager.getInstance().openProjects
            if(openProjects.isEmpty()) return
            for(project in openProjects) {
                val allEditors = FileEditorManager.getInstance(project).allEditors
                if(allEditors.isEmpty()) continue
                for(fileEditor in allEditors) {
                    if(fileEditor is TextEditor) {
                        val file = fileEditor.file
                        if(predicate(file, project)) {
                            val editor = fileEditor.editor
                            InlayHintsPassFactory.clearModificationStamp(editor)
                        }
                    }
                }
            }
        } catch(e: Exception) {
            thisLogger().warn(e.message)
        }
    }
}