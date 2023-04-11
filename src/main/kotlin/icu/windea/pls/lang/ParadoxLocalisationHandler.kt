package icu.windea.pls.lang

import com.intellij.lang.*
import com.intellij.openapi.progress.*
import com.intellij.psi.impl.source.tree.*
import com.intellij.psi.stubs.*
import com.intellij.psi.util.*
import icu.windea.pls.*
import icu.windea.pls.core.*
import icu.windea.pls.lang.model.*
import icu.windea.pls.localisation.psi.*
import icu.windea.pls.localisation.psi.impl.*

/**
 * 用于处理本地化信息。
 */
object ParadoxLocalisationHandler {
    fun getInfo(element: ParadoxLocalisationProperty): ParadoxLocalisationInfo? {
        ProgressManager.checkCanceled()
        return getInfoFromCache(element)
    }
    
    private fun getInfoFromCache(element: ParadoxLocalisationProperty): ParadoxLocalisationInfo? {
        return CachedValuesManager.getCachedValue(element, PlsKeys.cachedLocalisationInfoKey) {
            ProgressManager.checkCanceled()
            val value = resolveInfo(element)
            CachedValueProvider.Result.create(value, element)
        }
    }
    
    private fun resolveInfo(element: ParadoxLocalisationProperty): ParadoxLocalisationInfo? {
        //首先尝试直接基于stub进行解析
        val stub = runCatching { element.stub }.getOrNull()
        if(stub != null) return resolveInfoFromStub(stub)
        
        val name = element.name
        val file = element.containingFile.originalFile.virtualFile ?: return null
        val category = ParadoxLocalisationCategory.resolve(file) ?: return null
        val gameType = selectGameType(file)
        return ParadoxLocalisationInfo(name, category, gameType)
    }
    
    private fun resolveInfoFromStub(stub: ParadoxLocalisationStub): ParadoxLocalisationInfo {
        val name = stub.name.orEmpty()
        val category = stub.category
        val gameType = stub.gameType
        return ParadoxLocalisationInfo(name, category, gameType)
    }
    
    fun createStub(psi: ParadoxLocalisationProperty, parentStub: StubElement<*>): ParadoxLocalisationStub {
        val file = selectFile(parentStub.psi) ?: return createDefaultStub(parentStub)
        val name = psi.name
        val category = ParadoxLocalisationCategory.resolve(file).orDefault()
        val locale = selectLocale(file)?.id
        val gameType = selectGameType(file)
        return ParadoxLocalisationStubImpl(parentStub, name, category, locale, gameType)
    }
    
    fun createStub(tree: LighterAST, node: LighterASTNode, parentStub: StubElement<*>): ParadoxLocalisationStub {
        val file = selectFile(parentStub.psi) ?: return createDefaultStub(parentStub)
        val nameNode = LightTreeUtil.firstChildOfType(tree, node, ParadoxLocalisationElementTypes.PROPERTY_KEY)
        val nameTokenNode = LightTreeUtil.firstChildOfType(tree, nameNode, ParadoxLocalisationElementTypes.PROPERTY_KEY_TOKEN)
        val name = tree.charTable.internNode(nameTokenNode).toStringOrEmpty()
        val category = ParadoxLocalisationCategory.resolve(file).orDefault()
        val locale = selectLocale(file)?.id
        val gameType = selectGameType(file)
        return ParadoxLocalisationStubImpl(parentStub, name, category, locale, gameType)
    }
    
    private fun createDefaultStub(parentStub: StubElement<*>): ParadoxLocalisationStubImpl {
        return ParadoxLocalisationStubImpl(parentStub, "", ParadoxLocalisationCategory.Localisation, null, null)
    }
}