@file:Suppress("unused")

package icu.windea.pls

import com.google.common.cache.*
import com.intellij.util.io.*
import java.io.*
import java.net.*
import java.nio.charset.*
import java.nio.file.*
import java.text.*
import java.util.*

//region Common Extensions
@Suppress("NOTHING_TO_INLINE")
inline fun pass() {
}

@Suppress("UNCHECKED_CAST")
fun <T> Array<out T?>.cast() = this as Array<T>

fun <T> Collection<T>.asList(): List<T> {
	return if(this is List) this else this.toList()
}

fun <T, E> List<T>.groupAndCountBy(selector: (T) -> E?): Map<E, Int> {
	val result = mutableMapOf<E, Int>()
	for(e in this) {
		val k = selector(e)
		if(k != null) {
			result.compute(k) { _, v -> if(v == null) 1 else v + 1 }
		}
	}
	return result
}

inline fun <T, reified R> List<T>.mapToArray(block: (T) -> R): Array<R> {
	return Array(size) { block(this[it]) }
}

inline fun <T, reified R> Array<out T>.mapToArray(block: (T) -> R): Array<R> {
	return Array(size) { block(this[it]) }
}

inline fun <T, reified R> Sequence<T>.mapToArray(block: (T) -> R): Array<R> {
	return toList().mapToArray(block)
}

fun CharSequence.surroundsWith(prefix: Char, suffix: Char, ignoreCase: Boolean = false): Boolean {
	return startsWith(prefix, ignoreCase) && endsWith(suffix, ignoreCase)
}

fun CharSequence.surroundsWith(prefix: CharSequence, suffix: CharSequence, ignoreCase: Boolean = false): Boolean {
	return endsWith(suffix, ignoreCase) && startsWith(prefix, ignoreCase) //先匹配后缀，这样可能会提高性能
}

fun CharSequence.removeSurrounding(prefix: CharSequence, suffix: CharSequence): CharSequence {
	return removePrefix(prefix).removeSuffix(suffix)
}

fun String.removeSurrounding(prefix: CharSequence, suffix: CharSequence): String {
	return removePrefix(prefix).removeSuffix(suffix)
}

fun CharSequence.removePrefixOrNull(prefix: CharSequence): String? {
	return if(startsWith(prefix)) substring(prefix.length) else null
}

fun String.removePrefixOrNull(prefix: CharSequence): String? {
	return if(startsWith(prefix)) substring(prefix.length) else null
}

fun CharSequence.removeSuffixOrNull(suffix: CharSequence): String? {
	return if(endsWith(suffix)) substring(0, length - suffix.length) else null
}

fun String.removeSuffixOrNull(suffix: CharSequence): String? {
	return if(endsWith(suffix)) substring(0, length - suffix.length) else null
}

fun CharSequence.removeSurroundingOrNull(prefix: CharSequence, suffix: CharSequence): String? {
	return if(surroundsWith(prefix, suffix)) substring(prefix.length, length - suffix.length) else null
}

fun String.removeSurroundingOrNull(prefix: CharSequence, suffix: CharSequence): String? {
	return if(surroundsWith(prefix, suffix)) substring(prefix.length, length - suffix.length) else null
}

fun String.containsBlank(): Boolean {
	return any { it.isWhitespace() }
}

fun String.containsLineBreak(): Boolean {
	return any { it == '\n' || it == '\r' }
}

fun String.containsBlankLine(): Boolean {
	var newLine = 0
	val chars = toCharArray()
	for(i in chars.indices) {
		val char = chars[i]
		if((char == '\r' && chars[i + 1] != '\n') || char == '\n') newLine++
		if(newLine >= 2) return true
	}
	forEach {
		if(it == '\r' || it == '\n') newLine++
	}
	return false
}

fun String.quote() = if(startsWith('"') && endsWith('"')) this else "\"$this\""

fun String.quoteIf(quoted: Boolean) = if(quoted) "\"$this\"" else this //不判断之前是否已经用引号括起，依据quoted 

fun String.quoteIfNecessary() = if(containsBlank()) quote() else this //如果包含空白的话要使用引号括起

fun String.unquote() = if(length >= 2 && startsWith('"') && endsWith('"')) substring(1, length - 1) else this

fun String.truncate(limit: Int) = if(this.length <= limit) this else this.take(limit) + "..."

fun String.splitToPair(delimiter: Char): Pair<String, String>? {
	val index = this.indexOf(delimiter)
	if(index == -1) return null
	return this.substring(0, index) to this.substring(index + 1)
}

fun String.toCapitalizedWord(): String {
	return if(isEmpty()) this else this[0].uppercase() + this.substring(1)
}

fun String.toCapitalizedWords(): String {
	return buildString {
		var isWordStart = true
		for(c in this@toCapitalizedWords.toCharArray()) {
			when {
				isWordStart -> {
					isWordStart = false
					append(c.uppercase())
				}
				c == '_' || c == '-' || c == '.' -> {
					isWordStart = true
					append(' ')
				}
				else -> append(c.lowercase())
			}
		}
	}
}

private val keywordDelimiters = charArrayOf('.', '_')

/**
 * 判断指定的关键词是否匹配当前字符串。
 */
fun String.matchesKeyword(keyword: String): Boolean {
	//IDEA低层如何匹配关键词：
	//com.intellij.codeInsight.completion.PrefixMatcher.prefixMatches(java.lang.String)
	//这里如何匹配关键词：包含，忽略大小写
	return keyword.isEmpty() || contains(keyword, true)
	
	////这里如何匹配关键词：部分包含，被跳过的子字符串必须以'.','_'结尾，忽略大小写
	//if(keyword.isEmpty()) return true
	//var index = -1
	//var lastIndex = -2
	//for(c in keyword) {
	//	index = indexOf(c,index+1,ignoreCase)
	//	when {
	//		index == -1 -> return false
	//		c !in keywordDelimiters && index != 0 && lastIndex != index-1 
	//		    && this[index-1] !in keywordDelimiters -> return false
	//	}
	//	lastIndex = index
	//}
	//return true
}

fun CharSequence.indicesOf(char: Char, ignoreCase: Boolean = false): MutableList<Int> {
	val indices = mutableListOf<Int>()
	var lastIndex = indexOf(char, 0, ignoreCase)
	while(lastIndex != -1) {
		indices += lastIndex
		lastIndex = indexOf(char, lastIndex + 1, ignoreCase)
	}
	return indices
}

fun <K, V> Map<K, V>.find(predicate: (Map.Entry<K, V>) -> Boolean): V? {
	for(entry in this) {
		if(predicate(entry)) return entry.value
	}
	throw NoSuchElementException()
}

fun <K, V> Map<K, V>.findOrNull(predicate: (Map.Entry<K, V>) -> Boolean): V? {
	for(entry in this) {
		if(predicate(entry)) return entry.value
	}
	return null
}

inline fun <reified T> Any?.cast(): T = this as T

inline fun <reified T> Any?.castOrNull(): T? = this as? T

fun <C : CharSequence> C.ifNotEmpty(block: (C) -> C): C = if(this.isNotEmpty()) block(this) else this

fun String.toCommaDelimitedStringList(): List<String> = if(this.isEmpty()) emptyList() else this.split(',')

fun List<String>.toCommaDelimitedString(): String = if(this.isEmpty()) "" else this.joinToString(",")

/**
 * 判断当前路径是否匹配另一个路径（相同或者是其父路径）。使用"/"作为路径分隔符。
 * @param ignoreCase 是否忽略大小写。默认为`true`。
 */
fun String.matchesPath(other: String, ignoreCase: Boolean = true): Boolean {
	val path = if(ignoreCase) this.lowercase() else this
	val otherPath = if(ignoreCase) other.lowercase() else other
	if(path == otherPath) return true
	if(path == otherPath.take(length) && otherPath[length] == '/') return true
	return false
}

/**
 * 判断当前子路径列表是否完全匹配另一个子路径列表（相同）。使用"/"作为路径分隔符。
 * @param ignoreCase 是否忽略大小写。默认为`true`。
 * @param useAnyWildcard 使用`"any"`字符串作为子路径通配符。表示匹配任意子路径
 */
fun List<String>.matchEntirePath(other: List<String>, ignoreCase: Boolean = true, useAnyWildcard: Boolean = true): Boolean {
	val size = size
	val otherSize = other.size
	if(size != otherSize) return false
	for(index in 0 until size) {
		val path = if(ignoreCase) this[index].lowercase() else this[index]
		if(useAnyWildcard && path == "any") continue
		val otherPath = if(ignoreCase) other[index].lowercase() else other[index]
		if(path != otherPath) return false
	}
	return true
}

fun Path.exists(): Boolean {
	return Files.exists(this)
}

fun Path.notExists(): Boolean {
	return Files.notExists(this)
}

fun Path.create(): Path {
	if(isDirectory()) {
		createDirectories()
	} else {
		parent?.createDirectories()
		try {
			Files.createFile(this)
		} catch(e: FileAlreadyExistsException) {
			//ignored
		}
	}
	return this
}

val nullPair = null to null

@Suppress("UNCHECKED_CAST")
fun <A, B> emptyPair() = nullPair as Pair<A, B>

fun <T> Collection<T>.toListOrThis(): List<T> {
	return when(this) {
		is List -> this
		else -> this.toList()
	}
}

fun String.isBooleanYesNo(): Boolean {
	return this == "yes" || this == "no"
}

fun String.isInt(): Boolean {
	var isFirstChar = true
	val chars = toCharArray()
	for(char in chars) {
		if(char.isDigit()) continue
		if(isFirstChar) {
			isFirstChar = false
			if(char == '+' || char == '-') continue
		}
		return false
	}
	return true
}

fun String.isFloat(): Boolean {
	var isFirstChar = true
	var missingDot = true
	val chars = toCharArray()
	for(char in chars) {
		if(char.isDigit()) continue
		if(isFirstChar) {
			isFirstChar = false
			if(char == '+' || char == '-') continue
		}
		if(missingDot) {
			if(char == '.') {
				missingDot = false
				continue
			}
		}
		return false
	}
	return true
}

fun String.isString(): Boolean {
	//以引号包围，或者不是布尔值、整数以及小数
	if(surroundsWith('"', '"')) return true
	return !isBooleanYesNo() && !isInt() && !isFloat()
}

fun String.isPercentageField(): Boolean {
	val chars = toCharArray()
	for(i in indices) {
		val char = chars[i]
		if(i == lastIndex) {
			if(char != '%') return false
		} else {
			if(!char.isDigit()) return false
		}
	}
	return true
}

private val isColorRegex = """(?:rgb|rgba|hsb|hsv|hsl)[ \t]*\{[\d. \t]*}""".toRegex()

fun String.isColorField(): Boolean {
	return this.matches(isColorRegex)
}

private val threadLocalDateFormat = ThreadLocal.withInitial { SimpleDateFormat("yyyy.MM.dd") }

fun String.isDateField(): Boolean {
	return try {
		threadLocalDateFormat.get().parse(this)
		true
	} catch(e: Exception) {
		false
	}
}

fun String.isVariableField(): Boolean {
	return this.startsWith('@') //NOTE 简单判断
}

fun String.isTypeOf(type: String): Boolean {
	return (type == "boolean" && isBooleanYesNo()) || (type == "int" && isInt()) || (type == "float" && isFloat())
		|| (type == "color" && isColorField()) || type == "string"
}

fun Any?.toStringOrEmpty() = this?.toString() ?: ""

fun Boolean.toInt() = if(this) 1 else 0

fun Boolean.toStringYesNo() = if(this) "yes" else "no"

fun String.toBooleanYesNo() = this == "yes"

fun String.toBooleanYesNoOrNull() = if(this == "yes") true else if(this == "no") false else null

fun String.toUrl(locationClass: Class<*>) = locationClass.getResource(this)!!

fun String.toPath() = Path.of(this)

fun String.toIntRangeOrNull() = runCatching { split("..", limit = 2).let { (a, b) -> a.toInt()..b.toInt() } }.getOrNull()

fun String.toFloatRangeOrNull() = runCatching { split("..", limit = 2).let { (a, b) -> a.toFloat()..b.toFloat() } }.getOrNull()

fun String.toUUID(): UUID {
	return UUID.nameUUIDFromBytes(toByteArray(StandardCharsets.UTF_8))
}

fun URL.toFile() = File(this.toURI())

fun URL.toPath() = Paths.get(this.toURI())

@PublishedApi
internal val enumValuesCache: LoadingCache<Class<*>, Array<*>> by lazy { createCache { it.enumConstants } }

/**
 * 得到共享的指定枚举类型的所有枚举常量组成的数组。
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Enum<T>> enumSharedValues(): Array<T> {
	return enumValuesCache[T::class.java] as Array<T>
}

@Suppress("UNCHECKED_CAST")
inline val <T : Enum<T>> Class<T>.enumSharedConstants get() = enumValuesCache[this] as Array<T>
//endregion

//region Collection Extensions
inline fun <reified T> T.toSingletonArray() = arrayOf(this)

inline fun <reified T> Sequence<T>.toArray() = this.toList().toTypedArray()

fun <T> T.toSingletonList() = Collections.singletonList(this)

fun <T : Any> T?.toSingletonListOrEmpty() = if(this == null) Collections.emptyList() else Collections.singletonList(this)

data class ReversibleList<T>(val list: List<T>, val notReversed: Boolean) : List<T> by list {
	override fun contains(element: T): Boolean {
		return if(notReversed) list.contains(element) else !list.contains(element)
	}
	
	override fun containsAll(elements: Collection<T>): Boolean {
		return if(notReversed) list.containsAll(elements) else !list.containsAll(elements)
	}
}

fun <T> List<T>.toReversibleList(reverse: Boolean) = ReversibleList(this, reverse)

data class ReversibleSet<T>(val set: Set<T>, val notReversed: Boolean) : Set<T> by set {
	override fun contains(element: T): Boolean {
		return if(notReversed) set.contains(element) else !set.contains(element)
	}
	
	override fun containsAll(elements: Collection<T>): Boolean {
		return if(notReversed) set.containsAll(elements) else !set.containsAll(elements)
	}
}

fun <T> Set<T>.toReversibleSet(reverse: Boolean) = ReversibleSet(this, reverse)

data class ReversibleMap<K, V>(val map: Map<K, V>, val notReversed: Boolean = false) : Map<K, V> by map {
	override fun containsKey(key: K): Boolean {
		return if(notReversed) map.containsKey(key) else !map.containsKey(key)
	}
	
	override fun containsValue(value: V): Boolean {
		return if(notReversed) map.containsValue(value) else !map.containsValue(value)
	}
}

fun <K, V> Map<K, V>.toReversibleMap(reverse: Boolean) = ReversibleMap(this, reverse)
//endregion

//region Tuple & Range Extensions
typealias Tuple2<A, B> = Pair<A, B>

typealias TypedTuple2<T> = Pair<T, T>

typealias Tuple3<A, B, C> = Triple<A, B, C>

typealias TypedTuple3<T> = Triple<T, T, T>

fun <A, B> tupleOf(first: A, second: B) = Tuple2(first, second)

fun <A, B, C> tupleOf(first: A, second: B, third: C) = Tuple3(first, second, third)

typealias FloatRange = ClosedRange<Float>
//endregion