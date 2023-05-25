package icu.windea.pls.config

import com.google.common.cache.*
import com.intellij.openapi.diagnostic.*
import com.intellij.psi.*
import com.intellij.util.*
import icu.windea.pls.*
import icu.windea.pls.config.config.*
import icu.windea.pls.core.*
import icu.windea.pls.core.collections.*
import icu.windea.pls.cwt.psi.*
import icu.windea.pls.lang.model.*
import java.lang.invoke.*
import java.util.*

/**
 * Cwt规则的解析器。
 */
object CwtConfigResolver {
    private val logger = Logger.getInstance(MethodHandles.lookup().lookupClass())
    
    fun resolve(file: CwtFile, info: CwtConfigGroupInfo): CwtFileConfig {
        val rootBlock = file.block
        val properties = SmartList<CwtPropertyConfig>()
        val values = SmartList<CwtValueConfig>()
        val fileConfig = CwtFileConfig(file.createPointer(), info, properties, values, file.name)
        rootBlock?.processChild {
            when {
                it is CwtProperty -> resolveProperty(it, file, fileConfig)?.addTo(properties).let { true }
                it is CwtValue -> resolveValue(it, file, fileConfig).addTo(values).let { true }
                else -> true
            }
        }
        return fileConfig
    }
    
    private fun resolveProperty(propertyElement: CwtProperty, file: CwtFile, fileConfig: CwtFileConfig): CwtPropertyConfig? {
        val valueElement = propertyElement.propertyValue
        if(valueElement == null) {
            logger.error("Incorrect CWT config in ${fileConfig.name}\n${propertyElement.text}")
            return null
        }
        val pointer = propertyElement.createPointer(file)
        val key = propertyElement.name.intern() //intern to optimize memory
        val value: String = valueElement.value.intern() //intern to optimize memory
        val valueType: CwtType
        val separatorType = propertyElement.separatorType
        var configs: List<CwtMemberConfig<*>>? = null
        var documentationLines: LinkedList<String>? = null
        var html = false
        var options: LinkedList<CwtOptionConfig>? = null
        var optionValues: LinkedList<CwtOptionValueConfig>? = null
        
        when {
            valueElement is CwtBoolean -> {
                valueType = CwtType.Boolean
            }
            valueElement is CwtInt -> {
                valueType = CwtType.Int
            }
            valueElement is CwtFloat -> {
                valueType = CwtType.Float
            }
            valueElement is CwtString -> {
                valueType = CwtType.String
            }
            valueElement is CwtBlock -> {
                valueType = CwtType.Block
                valueElement.forEachChild f@{
                    when {
                        it is CwtProperty -> {
                            val resolved = resolveProperty(it, file, fileConfig) ?: return@f
                            if(configs == null) configs = SmartList()
                            configs!!.asMutable().add(resolved)
                        }
                        it is CwtValue -> {
                            val resolved = resolveValue(it, file, fileConfig)
                            if(configs == null) configs = SmartList()
                            configs!!.asMutable().add(resolved)
                        }
                    }
                }
                if(configs == null) configs = emptyList()
            }
            else -> {
                valueType = CwtType.Unknown
            }
        }
        
        var current: PsiElement = propertyElement
        while(true) {
            current = current.prevSibling ?: break
            when {
                current is CwtDocumentationComment -> {
                    val documentationText = current.documentationText
                    if(documentationText != null) {
                        if(documentationLines == null) documentationLines = LinkedList()
                        val docText = documentationText.text.trimStart('#').trim() //这里接受HTML
                        documentationLines.addFirst(docText)
                    }
                }
                current is CwtOptionComment -> {
                    val option = current.option
                    if(option != null) {
                        if(option.name == "format" && option.value == "html") html = true
                        if(options == null) options = LinkedList()
                        val resolved = resolveOption(option, file, fileConfig) ?: continue
                        options.addFirst(resolved)
                    } else {
                        val optionValue = current.value ?: continue
                        if(optionValues == null) optionValues = LinkedList()
                        val resolved = resolveOptionValue(optionValue, file, fileConfig)
                        optionValues.addFirst(resolved)
                    }
                }
                current is PsiWhiteSpace || current is PsiComment -> continue
                else -> break
            }
        }
        val documentation = getDocumentation(documentationLines, html)
        
        val config = CwtPropertyConfig(pointer, fileConfig.info, key, value, valueType, separatorType, configs, options, optionValues, documentation)
        fileConfig.info.acceptConfigExpression(config.keyExpression, config)
        fileConfig.info.acceptConfigExpression(config.valueExpression, config)
        configs?.forEach { it.parent = config }
        return config
    }
    
    private fun resolveValue(valueElement: CwtValue, file: CwtFile, fileConfig: CwtFileConfig): CwtValueConfig {
        val pointer = valueElement.createPointer(file)
        val value: String = valueElement.value.intern() //intern to optimize memory
        val valueType: CwtType
        var configs: List<CwtMemberConfig<*>>? = null
        var documentationLines: LinkedList<String>? = null
        var html = false
        var options: LinkedList<CwtOptionConfig>? = null
        var optionValues: LinkedList<CwtOptionValueConfig>? = null
        
        when {
            valueElement is CwtBoolean -> {
                valueType = CwtType.Boolean
            }
            valueElement is CwtInt -> {
                valueType = CwtType.Int
            }
            valueElement is CwtFloat -> {
                valueType = CwtType.Float
            }
            valueElement is CwtString -> {
                valueType = CwtType.String
            }
            valueElement is CwtBlock -> {
                valueType = CwtType.Block
                valueElement.forEachChild f@{
                    when {
                        it is CwtProperty -> {
                            val resolved = resolveProperty(it, file, fileConfig) ?: return@f
                            if(configs == null) configs = SmartList()
                            configs!!.asMutable().add(resolved)
                        }
                        it is CwtValue -> {
                            val resolved = resolveValue(it, file, fileConfig)
                            if(configs == null) configs = SmartList()
                            configs!!.asMutable().add(resolved)
                        }
                    }
                }
                if(configs == null) configs = emptyList()
            }
            else -> {
                valueType = CwtType.Unknown
            }
        }
        
        var current: PsiElement = valueElement
        while(true) {
            current = current.prevSibling ?: break
            when {
                current is CwtDocumentationComment -> {
                    val documentationText = current.documentationText
                    if(documentationText != null) {
                        if(documentationLines == null) documentationLines = LinkedList()
                        val docText = documentationText.text.trimStart('#').trim() //这里接受HTML
                        documentationLines.addFirst(docText)
                    }
                }
                current is CwtOptionComment -> {
                    val option = current.option
                    if(option != null) {
                        if(option.name == "format" && option.value == "html") html = true
                        if(options == null) options = LinkedList()
                        val resolved = resolveOption(option, file, fileConfig) ?: continue
                        options.addFirst(resolved)
                    } else {
                        val optionValue = current.value ?: continue
                        if(optionValues == null) optionValues = LinkedList()
                        val resolved = resolveOptionValue(optionValue, file, fileConfig)
                        optionValues.addFirst(resolved)
                    }
                }
                current is PsiWhiteSpace || current is PsiComment -> continue
                else -> break
            }
        }
        val documentation = getDocumentation(documentationLines, html)
        
        val config = CwtValueConfig(pointer, fileConfig.info, value, valueType, configs, options, optionValues, documentation)
        fileConfig.info.acceptConfigExpression(config.valueExpression, config)
        configs?.forEach { it.parent = config }
        return config
    }
    
    private val optionConfigCache = CacheBuilder.newBuilder().buildCache<String, CwtOptionConfig>()
    
    private fun resolveOption(optionElement: CwtOption, file: CwtFile, fileConfig: CwtFileConfig): CwtOptionConfig? {
        val optionValueElement = optionElement.optionValue
        if(optionValueElement == null) {
            logger.error("Incorrect CWT config in ${fileConfig.name}\n${optionElement.text}")
            return null
        }
        val key = optionElement.name.intern() //intern to optimize memory
        val value = optionValueElement.value.intern() //intern to optimize memory
        val valueType: CwtType
        val separatorType = optionElement.separatorType
        var options: List<CwtOptionConfig>? = null
        var optionValues: List<CwtOptionValueConfig>? = null
        
        when {
            optionValueElement is CwtBoolean -> {
                valueType = CwtType.Boolean
            }
            optionValueElement is CwtInt -> {
                valueType = CwtType.Int
            }
            optionValueElement is CwtFloat -> {
                valueType = CwtType.Float
            }
            optionValueElement is CwtString -> {
                valueType = CwtType.String
            }
            optionValueElement is CwtBlock -> {
                valueType = CwtType.Block
                optionValueElement.forEachChild f@{
                    when {
                        it is CwtOption -> {
                            val resolved = resolveOption(it, file, fileConfig) ?: return@f
                            if(options == null) options = SmartList()
                            options!!.asMutable().add(resolved)
                        }
                        it is CwtValue -> {
                            val resolved = resolveOptionValue(it, file, fileConfig)
                            if(optionValues == null) optionValues = SmartList()
                            optionValues!!.asMutable().add(resolved)
                        }
                    }
                }
                if(options == null) options = emptyList()
                if(optionValues == null) optionValues = emptyList()
            }
            else -> {
                valueType = CwtType.Unknown
            }
        }
        
        //use cache if possible to optimize memory
        if(valueType != CwtType.Block) {
            return optionConfigCache.getOrPut("${valueType.ordinal}#${key}#${value}") {
                CwtOptionConfig(emptyPointer(), fileConfig.info, key, value, valueType, separatorType, options, optionValues)
            }
        }
        return CwtOptionConfig(emptyPointer(), fileConfig.info, key, value, valueType, separatorType, options, optionValues)
    }
    
    private val optionValueConfigCache = CacheBuilder.newBuilder().buildCache<String, CwtOptionValueConfig>()
    
    private fun resolveOptionValue(optionValueElement: CwtValue, file: CwtFile, fileConfig: CwtFileConfig): CwtOptionValueConfig {
        val value = optionValueElement.value.intern() //intern to optimize memory
        val valueType: CwtType
        var options: List<CwtOptionConfig>? = null
        var optionValues: List<CwtOptionValueConfig>? = null
        
        when {
            optionValueElement is CwtBoolean -> {
                valueType = CwtType.Boolean
            }
            optionValueElement is CwtInt -> {
                valueType = CwtType.Int
            }
            optionValueElement is CwtFloat -> {
                valueType = CwtType.Float
            }
            optionValueElement is CwtString -> {
                valueType = CwtType.String
            }
            optionValueElement is CwtBlock -> {
                valueType = CwtType.Block
                optionValueElement.forEachChild f@{
                    when {
                        it is CwtOption -> {
                            val resolved = resolveOption(it, file, fileConfig) ?: return@f
                            if(options == null) options = SmartList()
                            options!!.asMutable().add(resolved)
                        }
                        it is CwtValue -> {
                            val resolved = resolveOptionValue(it, file, fileConfig)
                            if(optionValues == null) optionValues = SmartList()
                            optionValues!!.asMutable().add(resolved)
                        }
                    }
                }
                if(options == null) options = emptyList()
                if(optionValues == null) optionValues = emptyList()
            }
            else -> {
                valueType = CwtType.Unknown
            }
        }
        
        //use cache if possible to optimize memory
        if(valueType != CwtType.Block) {
            return optionValueConfigCache.getOrPut("${valueType.ordinal}#${value}") {
                CwtOptionValueConfig(emptyPointer(), fileConfig.info, value, valueType, options, optionValues)
            }
        }
        return CwtOptionValueConfig(emptyPointer(), fileConfig.info, value, valueType, options, optionValues)
    }
}
