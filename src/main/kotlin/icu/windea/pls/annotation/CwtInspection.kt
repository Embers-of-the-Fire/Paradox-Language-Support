package icu.windea.pls.annotation

@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class CwtInspection(
	val value: String
)