package icu.windea.pls.core.annotations

import icu.windea.pls.lang.cwt.setting.*
import kotlin.reflect.*

/**
 * 注明此功能基于指定的CWT配置实现。区别于一般的CWT规则。
 * @property fileName CWT配置文件的文件名。
 * @property settingClass 对应的CWT配置类。
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
annotation class WithCwtSetting(
	val fileName: String,
	val settingClass: KClass<out CwtSetting>
)
