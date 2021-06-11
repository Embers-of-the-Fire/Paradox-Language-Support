package icu.windea.pls

import com.intellij.openapi.util.*

//Core Constants

const val bundleName = "messages.PlsBundle"
val locationClass = PlsBundle::class.java

//String Constants

const val cwtName = "Cwt Config"
const val cwtDescription = "Cwt config"
const val cwtId = "CWT"
const val cwtExtension = "cwt"
val cwtDemoText = "/demoText/Cwt.txt".toUrl(locationClass).readText()

const val paradoxLocalisationName = "Paradox Localisation"
const val paradoxLocalisationDescription = "Paradox localisation"
const val paradoxLocalisationId = "PARADOX_LOCALISATION"
const val paradoxLocalisationExtension = "yml"
val paradoxLocalisationDemoText = "/demoText/ParadoxLocalisation.txt".toUrl(locationClass).readText()

const val paradoxScriptName = "Paradox Script"
const val paradoxScriptDescription = "Paradox script"
const val paradoxScriptId = "PARADOX_SCRIPT"
const val paradoxScriptExtension = "txt"
val paradoxScriptDemoText = "/demoText/ParadoxScript.txt".toUrl(locationClass).readText()

//Misc Constants

const val dummyIdentifier = "windea"
const val dummyIdentifierLength = dummyIdentifier.length
const val commentFolder = "#..."
const val blockFolder = "{...}"
const val emptyBlockString = "{}"
const val anonymousString = "<anonymous>"
val utf8Bom = byteArrayOf(0xef.toByte(), 0xbb.toByte(), 0xbf.toByte())
val booleanValues = arrayOf("yes", "no")
val localisationFileExtensions = arrayOf("yml")
val scriptFileExtensions = arrayOf("txt", "mod", "gfx", "gui", "asset", "dlc","settings")
const val descriptorFileName = "descriptor.mod"
val ignoredScriptFileNameRegex = """(readme|changelog|license|credits).*\.txt""".toRegex(RegexOption.IGNORE_CASE)
const val truncateLimit = 20

//Icons

//TODO 为每个游戏类型给定对应的游戏图标
val paradoxIcon = IconLoader.getIcon("/icons/paradox.png",locationClass)
val ck2Icon = IconLoader.getIcon("/icons/paradox.png",locationClass)
val ck3Icon = IconLoader.getIcon("/icons/paradox.png",locationClass)
val eu4Icon = IconLoader.getIcon("/icons/paradox.png",locationClass)
val hoi4Icon = IconLoader.getIcon("/icons/paradox.png",locationClass)
val irIcon = IconLoader.getIcon("/icons/paradox.png",locationClass)
val stellarisIcon = IconLoader.getIcon("/icons/paradox.png",locationClass)
val vic2Icon = IconLoader.getIcon("/icons/paradox.png",locationClass)

val cwtFileIcon = IconLoader.getIcon("/icons/cwtFile.svg", locationClass)
val paradoxScriptFileIcon = IconLoader.getIcon("/icons/paradoxScriptFile.svg",locationClass)
val paradoxLocalisationFileIcon = IconLoader.getIcon("/icons/paradoxLocalisationFile.svg",locationClass)

val cwtPropertyIcon = IconLoader.getIcon("/icons/cwtProperty.svg", locationClass)
val cwtOptionIcon = IconLoader.getIcon("/icons/cwtOption.svg", locationClass)
val cwtValueIcon = IconLoader.getIcon("/icons/cwtValue.svg", locationClass)

val scriptVariableIcon = IconLoader.getIcon("/icons/paradoxScriptVariable.svg",locationClass)
val scriptPropertyIcon = IconLoader.getIcon("/icons/paradoxScriptProperty.svg",locationClass)
val scriptValueIcon = IconLoader.getIcon("/icons/paradoxScriptValue.svg",locationClass)

val localisationLocaleIcon = IconLoader.getIcon("/icons/paradoxLocalisationLocale.svg",locationClass)
val localisationPropertyIcon = IconLoader.getIcon("/icons/paradoxLocalisationProperty.svg",locationClass)
val localisationIconIcon = IconLoader.getIcon("/icons/paradoxLocalisationIcon.svg",locationClass)
val localisationSequentialNumberIcon = IconLoader.getIcon("/icons/paradoxLocalisationSequentialNumber.svg",locationClass)
val localisationCommandScopeIcon = IconLoader.getIcon("/icons/paradoxLocalisationCommandScope.svg",locationClass)
val localisationCommandFieldIcon = IconLoader.getIcon("/icons/paradoxLocalisationCommandField.svg",locationClass)

val definitionIcon = IconLoader.getIcon("/icons/paradoxDefinition.svg",locationClass)
val definitionLocalisationIcon = IconLoader.getIcon("/icons/paradoxDefinitionLocalisation.svg",locationClass)
val localisationIcon = IconLoader.getIcon("/icons/paradoxLocalisation.svg",locationClass)
val stringScriptPropertyIcon =IconLoader.getIcon("/icons/paradoxStringScriptProperty.svg",locationClass)
val stringLocalisationPropertyIcon = IconLoader.getIcon("/icons/paradoxStringLocalisationProperty.svg",locationClass)
val scriptLocalisationIcon = IconLoader.getIcon("/icons/paradoxScriptLocalisation.svg",locationClass)

val definitionGutterIcon = definitionIcon.resize(12)
val definitionLocalisationGutterIcon = definitionLocalisationIcon.resize(12)
val localisationGutterIcon = localisationIcon.resize(12)
val stringScriptPropertyGutterIcon =stringScriptPropertyIcon.resize(12)
val stringLocalisationPropertyGutterIcon = stringLocalisationPropertyIcon.resize(12)

//Pattern Prefixes

const val primitivePrefix="$"
const val primitivePrefixLength = primitivePrefix.length
const val aliasPrefix="alias:"
const val aliasPrefixLength=aliasPrefix.length
const val typePrefix="type:"
const val typePrefixLength = typePrefix.length
const val enumPrefix="enum:"
const val enumPrefixLength=enumPrefix.length
const val eventTargetPrefix = "event_target:"
const val eventTargetPrefixLength = eventTargetPrefix.length