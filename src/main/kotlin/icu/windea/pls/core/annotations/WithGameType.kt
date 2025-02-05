package icu.windea.pls.core.annotations

import icu.windea.pls.model.*

/**
 * 注明此功能仅限于指定的游戏类型。
 * 
 * @property value 游戏类型。
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class WithGameType(
	vararg val value: ParadoxGameType
)

