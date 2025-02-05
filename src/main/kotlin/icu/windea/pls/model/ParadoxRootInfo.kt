package icu.windea.pls.model

import com.intellij.openapi.vfs.*
import icu.windea.pls.core.*
import icu.windea.pls.lang.*
import java.nio.file.*

/**
 * @property rootFile 游戏或模组的根目录。
 * @property gameRootFile 游戏文件的根目录。可能等同于rootFile，也可能是其game子目录。
 */
sealed class ParadoxRootInfo {
    abstract val rootFile: VirtualFile
    abstract val gameRootFile: VirtualFile
    abstract val gameType: ParadoxGameType
    
    abstract val rootPath: Path
    abstract val gameRootPath: Path
    
    val gameEntry: String? by lazy { rootPath.relativize(gameRootPath).toString().takeIfNotEmpty() }
    
    abstract val qualifiedName: String
}

class ParadoxGameRootInfo(
    override val rootFile: VirtualFile,
    val launcherSettingsFile: VirtualFile,
    val launcherSettingsInfo: ParadoxLauncherSettingsInfo
) : ParadoxRootInfo() {
    override val gameType: ParadoxGameType = doGetGameType()
    override val gameRootFile: VirtualFile = doGetGameRootFile()
    
    private fun doGetGameType(): ParadoxGameType {
        return launcherSettingsInfo.gameId.let { ParadoxGameType.resolve(it) } ?: throw IllegalStateException()
    }
    
    private fun doGetGameRootFile(): VirtualFile {
        val dlcPath = launcherSettingsInfo.dlcPath
        val path = launcherSettingsFile.toNioPath().parent.resolve(dlcPath).normalize().toAbsolutePath()
        return VfsUtil.findFile(path, true) ?: throw IllegalStateException()
    }
    
    override val rootPath: Path = rootFile.toNioPath()
    override val gameRootPath: Path = gameRootFile.toNioPath()
    
    override val qualifiedName: String
        get() = buildString {
            append(gameType.title)
            append("@")
            append(launcherSettingsInfo.rawVersion)
        }
    
    override fun equals(other: Any?): Boolean {
        return this === other || other is ParadoxRootInfo && rootFile == other.rootFile
    }
    
    override fun hashCode(): Int {
        return rootFile.hashCode()
    }
}

data class ParadoxLauncherSettingsInfo(
    val gameId: String,
    val version: String,
    val rawVersion: String,
    val gameDataPath: String = "%USER_DOCUMENTS%/Paradox Interactive/${ParadoxGameType.resolve(gameId)!!}",
    val dlcPath: String = "",
    val exePath: String,
    val exeArgs: List<String>
)

class ParadoxModRootInfo(
    override val rootFile: VirtualFile,
    val descriptorFile: VirtualFile,
    val descriptorInfo: ParadoxModDescriptorInfo
) : ParadoxRootInfo() {
    val inferredGameType: ParadoxGameType? = doGetInferredGameType()
    override val gameType: ParadoxGameType get() = doGetGameType()
    override val gameRootFile: VirtualFile get() = rootFile
    
    private fun doGetInferredGameType(): ParadoxGameType? {
        return ParadoxCoreHandler.getInferredGameType(this)
    }
    
    private fun doGetGameType(): ParadoxGameType {
        return inferredGameType 
            ?: getProfilesSettings().modDescriptorSettings.get(rootFile.path)?.gameType
            ?: getSettings().defaultGameType
    }
    
    override val rootPath: Path = rootFile.toNioPath()
    override val gameRootPath: Path = rootPath
    
    override val qualifiedName: String
        get() = buildString {
            append(gameType.title).append(" Mod: ")
            append(descriptorInfo.name)
            descriptorInfo.version?.let { version -> append("@").append(version) }
        }
    
    override fun equals(other: Any?): Boolean {
        return this === other || other is ParadoxRootInfo && rootFile == other.rootFile
    }
    
    override fun hashCode(): Int {
        return rootFile.hashCode()
    }
}

data class ParadoxModDescriptorInfo(
    val name: String,
    val version: String? = null,
    val picture: String? = null,
    val tags: Set<String>? = null,
    val supportedVersion: String? = null,
    val remoteFileId: String? = null,
    val path: String? = null
)