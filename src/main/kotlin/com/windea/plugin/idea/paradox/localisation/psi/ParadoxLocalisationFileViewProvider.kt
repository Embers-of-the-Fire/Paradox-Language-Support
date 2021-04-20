package com.windea.plugin.idea.paradox.localisation.psi

import com.intellij.lang.*
import com.intellij.openapi.vfs.*
import com.intellij.psi.*
import com.intellij.testFramework.*
import com.windea.plugin.idea.paradox.*
import com.windea.plugin.idea.paradox.model.*

class ParadoxLocalisationFileViewProvider(
	manager: PsiManager,
	virtualFile: VirtualFile,
	eventSystemEnabled: Boolean
): SingleRootFileViewProvider(manager,virtualFile,eventSystemEnabled) {
	//virtualFile可能是LightVirtualFile

	
	override fun getPsiInner(target: Language): PsiFile? {
		val psi =  super.getPsiInner(target)
		//传递fileInfo，注意virtualFile可能是LightVirtualFile
		val fileInfo  = when(val file = virtualFile) {
			is LightVirtualFile -> file.originalFile?.paradoxFileInfo
			else -> file.paradoxFileInfo
		}
		psi?.putUserData(paradoxFileInfoKey,fileInfo)
		return psi
	}
}

