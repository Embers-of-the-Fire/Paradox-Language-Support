package icu.windea.pls.lang

import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.util.*
import icu.windea.pls.core.*
import icu.windea.pls.cwt.psi.*
import icu.windea.pls.lang.cwt.*
import icu.windea.pls.lang.cwt.config.*
import java.util.*

object CwtConfigHandler {
    //region Config Path Methods
    val cachedCwtConfigPathKey = Key.create<CachedValue<CwtConfigPath>>("paradox.cached.cwtConfigPath")
    
    fun get(element: PsiElement): CwtConfigPath? {
        if(element is CwtFile) return EmptyCwtConfigPath
        if(element !is CwtProperty && element !is CwtValue) return null
        return getFromCache(element)
    }
    
    private fun getFromCache(element: PsiElement): CwtConfigPath? {
        return CachedValuesManager.getCachedValue(element, cachedCwtConfigPathKey) {
            val value = resolve(element)
            CachedValueProvider.Result.create(value, element)
        }
    }
    
    private fun resolve(element: PsiElement): CwtConfigPath? {
        var current: PsiElement = element
        var depth = 0
        val subPaths = LinkedList<String>()
        while(current !is PsiFile) {
            when {
                current is CwtProperty -> {
                    subPaths.addFirst(current.name)
                    depth++
                }
                current is CwtValue && current.isBlockValue() -> {
                    subPaths.addFirst("-")
                    depth++
                }
            }
            current = current.parent ?: break
        }
        if(current !is CwtFile) return null //unexpected
        return CwtConfigPath.resolve(subPaths)
    }
    //endregion
    
    //region Config Type Methods
    val cachedCwtConfigTypeKey = Key.create<CachedValue<CwtConfigType>>("paradox.cached.cwtConfigType")
    
    fun getConfigType(element: PsiElement): CwtConfigType? {
        if(element !is CwtProperty && element !is CwtValue) return null
        return getConfigTypeFromCache(element)
    }
    
    private fun getConfigTypeFromCache(element: PsiElement): CwtConfigType? {
        return CachedValuesManager.getCachedValue(element, cachedCwtConfigTypeKey) {
            val file = element.containingFile ?: return@getCachedValue null
            val value = when(element) {
                is CwtProperty -> doGetConfigType(element, file)
                is CwtValue -> doGetConfigType(element, file)
                else -> null
            }
            //invalidated on file modification
            CachedValueProvider.Result.create(value, file)
        }
    }
    
    private fun doGetConfigType(element: CwtProperty, file: PsiFile): CwtConfigType? {
        val fileKey = file.name.substringBefore('.')
        val configPath = element.configPath
        if(configPath == null || configPath.isEmpty()) return null
        val path = configPath.path
        return when {
            path.matchesAntPath("types/type[*]") -> {
                CwtConfigType.Type
            }
            path.matchesAntPath("types/type[*]/subtype[*]") -> {
                CwtConfigType.Subtype
            }
            path.matchesAntPath("types/type[*]/modifiers/**") -> {
                when {
                    configPath.get(3).surroundsWith("subtype[", "]") -> {
                        if(configPath.length == 5) return CwtConfigType.Modifier
                    }
                    else -> {
                        if(configPath.length == 4) return CwtConfigType.Modifier
                    }
                }
                null
            }
            path.matchesAntPath("enums/enum[*]") -> {
                CwtConfigType.Enum
            }
            path.matchesAntPath("enums/complex_enum[*]") -> {
                CwtConfigType.ComplexEnum
            }
            path.matchesAntPath("values/value[*]") -> {
                CwtConfigType.ValueSet
            }
            fileKey == "on_actions" && path.matchesAntPath("on_actions/*") -> {
                CwtConfigType.OnAction
            }
            path.matchesAntPath("single_alias[*]") -> {
                CwtConfigType.SingleAlias
            }
            path.matchesAntPath("alias[*]") -> {
                val aliasName = configPath.get(0).substringIn('[', ']', "").substringBefore(':', "")
                when {
                    aliasName == "modifier" -> return CwtConfigType.Modifier
                    aliasName == "trigger" -> return CwtConfigType.Trigger
                    aliasName == "effect" -> return CwtConfigType.Effect
                }
                CwtConfigType.Alias
            }
            fileKey == "links" && path.matchesAntPath("links/*") -> {
                CwtConfigType.Link
            }
            fileKey == "localisation" && path.matchesAntPath("localisation_links/*") -> {
                CwtConfigType.LocalisationLink
            }
            fileKey == "localisation" && path.matchesAntPath("localisation_commands/*") -> {
                CwtConfigType.LocalisationCommand
            }
            fileKey == "modifier_categories" && path.matchesAntPath("modifier_categories/*") -> {
                CwtConfigType.ModifierCategory
            }
            fileKey == "modifiers" && path.matchesAntPath("modifiers/*") -> {
                CwtConfigType.Modifier
            }
            fileKey == "scopes" && path.matchesAntPath("scopes/*") -> {
                CwtConfigType.Scope
            }
            fileKey == "scopes" && path.matchesAntPath("scope_groups/*") -> {
                CwtConfigType.ScopeGroup
            }
            fileKey == "system_links" && path.matchesAntPath("system_links/*") -> {
                CwtConfigType.SystemLink
            }
            fileKey == "localisation_locales" && path.matchesAntPath("localisation_locales/*") -> {
                CwtConfigType.LocalisationLocale
            }
            fileKey == "localisation_predefined_parameters" && path.matchesAntPath("localisation_predefined_parameters/*") -> {
                CwtConfigType.LocalisationPredefinedParameter
            }
            else -> null
        }
    }
    
    private fun doGetConfigType(element: CwtValue, file: PsiFile): CwtConfigType? {
        //val fileKey = file.name.substringBefore('.')
        val configPath = element.configPath
        if(configPath == null || configPath.isEmpty()) return null
        val path = configPath.path
        return when {
            path.matchesAntPath("enums/enum[*]/*") -> CwtConfigType.EnumValue
            path.matchesAntPath("values/value[*]/*") -> CwtConfigType.ValueSetValue
            else -> null
        }
    }
    //endregion
}