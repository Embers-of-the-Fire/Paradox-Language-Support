package icu.windea.pls.core.index

import com.intellij.openapi.vfs.*
import com.intellij.psi.*
import com.intellij.util.indexing.*
import icu.windea.pls.core.*
import icu.windea.pls.core.collections.*
import icu.windea.pls.lang.*
import icu.windea.pls.model.*
import icu.windea.pls.script.*
import icu.windea.pls.script.psi.*
import java.io.*

private val NAME = ID.create<String, List<ParadoxComplexEnumValueInfo>>("paradox.complexEnumValue.index")
private const val VERSION = 33 //1.1.6


/**
 * 用于索引复杂枚举值。
 *
 * * 这个索引兼容需要内联的情况（此时使用懒加载的索引）。
 *
 * @see ParadoxComplexEnumValueInfo
 */
class ParadoxComplexEnumValueIndex : ParadoxFileBasedIndex<List<ParadoxComplexEnumValueInfo>>() {
    override fun getName() = NAME
    
    override fun getVersion() = VERSION
    
    override fun indexData(file: PsiFile, fileData: MutableMap<String, List<ParadoxComplexEnumValueInfo>>) {
        file.acceptChildren(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                if(element is ParadoxScriptStringExpressionElement && element.isExpression()) {
                    val info = ParadoxComplexEnumValueHandler.getInfo(element)
                    if(info != null) {
                        val list = fileData.getOrPut(info.enumName) { mutableListOf() } as MutableList
                        list.add(info)
                    }
                }
                if(element.isExpressionOrMemberContext()) super.visitElement(element)
            }
        })
        
        //排序
        if(fileData.isEmpty()) return
        fileData.mapValues { (_, v) ->
            v.sortedBy { it.name }
        }
    }
    
    //尝试减少实际需要索引的数据量以优化性能
    
    override fun writeData(storage: DataOutput, value: List<ParadoxComplexEnumValueInfo>) {
        val size = value.size
        storage.writeIntFast(size)
        if(size == 0) return
        val firstInfo = value.first()
        storage.writeUTFFast(firstInfo.enumName)
        storage.writeByte(firstInfo.gameType.toByte())
        var previousInfo: ParadoxComplexEnumValueInfo? = null
        value.forEachFast { info ->
            storage.writeOrWriteFrom(info, previousInfo, { it.name }, { storage.writeUTFFast(it) })
            storage.writeByte(info.readWriteAccess.toByte())
            storage.writeIntFast(info.elementOffset)
            previousInfo = info
        }
    }
    
    override fun readData(storage: DataInput): List<ParadoxComplexEnumValueInfo> {
        val size = storage.readIntFast()
        if(size == 0) return emptyList()
        val enumName = storage.readUTFFast()
        val gameType = storage.readByte().toGameType()
        var previousInfo: ParadoxComplexEnumValueInfo? = null
        val result = mutableListOf<ParadoxComplexEnumValueInfo>()
        repeat(size) {
            val name = storage.readOrReadFrom(previousInfo, { it.name }, { storage.readUTFFast() })
            val readWriteAccess = storage.readByte().toReadWriteAccess()
            val elementOffset = storage.readIntFast()
            val info = ParadoxComplexEnumValueInfo(name, enumName, readWriteAccess, elementOffset, gameType)
            result += info
            previousInfo = info
        }
        return result
    }
    
    override fun filterFile(file: VirtualFile): Boolean {
        val fileType = file.fileType
        if(fileType != ParadoxScriptFileType) return false
        if(file.fileInfo == null) return false
        return true
    }
    
    override fun useLazyIndex(file: VirtualFile): Boolean {
        if(ParadoxFileManager.isInjectedFile(file)) return true
        if(ParadoxInlineScriptHandler.getInlineScriptExpression(file) != null) return true
        return false
    }
}
