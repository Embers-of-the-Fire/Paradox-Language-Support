package icu.windea.pls

val locationClass = PlsIcons::class.java

const val ddsName = "DDS"
const val ddsDescription = "DirectDraw Surface"

const val cwtName = "Cwt"
const val cwtDescription = "Cwt config"
const val cwtId = "CWT"
const val cwtExtension = "cwt"
val cwtDemoText = "/demoText/Cwt.txt".toClasspathUrl().readText()

const val paradoxLocalisationName = "Paradox Localisation"
const val paradoxLocalisationDescription = "Paradox localisation"
const val paradoxLocalisationId = "PARADOX_LOCALISATION"
const val paradoxLocalisationExtension = "yml"
val paradoxLocalisationDemoText = "/demoText/ParadoxLocalisation.txt".toClasspathUrl().readText()

const val paradoxScriptName = "Paradox Script"
const val paradoxScriptDescription = "Paradox script"
const val paradoxScriptId = "PARADOX_SCRIPT"
const val paradoxScriptExtension = "txt"
val paradoxScriptDemoText = "/demoText/ParadoxScript.txt".toClasspathUrl().readText()

const val keywordPriority = 80.0
const val propertyPriority = 40.0
const val modifierPriority = 20.0

const val dummyIdentifier = "windea"
const val dummyIdentifierLength = dummyIdentifier.length

const val commentFolder = "#..."
const val parameterFolder = "$...$"
const val stringTemplateFolder = "..."
const val blockFolder = "{...}"
fun parameterConditionFolder(expression: String) = "[[$expression]...]"
const val inlineMathFolder = "@[...]"

const val anonymousString = "<anonymous>"
const val anonymousEscapedString = "&lt;anonymous&gt;"
//const val unknownString = "<unknown>"
//const val unknownEscapedString = "&lt;unknown&gt;"
//const val unresolvedString = "<unresolved>"
const val unresolvedEscapedString = "&lt;unresolved&gt;"

val utf8Bom = byteArrayOf(0xef.toByte(), 0xbb.toByte(), 0xbf.toByte())

val booleanValues = arrayOf("yes", "no")

val scriptFileExtensions = arrayOf("txt", "gfx", "gui", "asset", "dlc", "settings")
val localisationFileExtensions = arrayOf("yml")
val ddsFileExtensions = arrayOf("dds")

const val launcherSettingsFileName = "launcher-settings.json" 
const val descriptorFileName = "descriptor.mod"