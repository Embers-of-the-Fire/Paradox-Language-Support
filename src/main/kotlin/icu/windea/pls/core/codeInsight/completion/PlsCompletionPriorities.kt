package icu.windea.pls.core.codeInsight.completion

object PlsCompletionPriorities {
	const val pinnedPriority = 1000.0
	const val rootKeyPriority = 100.0
	const val definitionNamePriority = 95.0
	const val keywordPriority = 90.0
	const val constantPriority = 90.0
	const val scopeLinkPrefixPriority = 85.0
	const val valueLinkPrefixPriority = 85.0
	//const val modifierPriority = 80.0
	const val systemLinkPriority = 75.0
	const val scopePriority = 70.0
	const val localisationCommandPriority = 50.0
	//const val pathPriority = 60.0
	//const val definitionPriority = 50.0
	//const val localisationPriority = 45.0
	const val enumPriority = 85.0
	//const val complexEnumPriority = 40.0
	const val valueLinkValuePriority = 30.0
	const val predefinedValueSetValuePriority = 80.0
	//const val valueSetValuePriority = 20.0
	//const val variablePriority = 15.0
	
	const val scopeMismatchOffset = -500 
}
