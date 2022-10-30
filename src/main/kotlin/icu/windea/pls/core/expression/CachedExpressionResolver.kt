package icu.windea.pls.core.expression

import com.google.common.cache.*
import icu.windea.pls.*

abstract class CachedExpressionResolver<T : Expression> : ExpressionResolver<T>() {
	protected val cache: LoadingCache<String, T> by lazy { CacheBuilder.newBuilder().buildCache { doResolve(it) } }
	
	override fun resolve(expressionString: String): T {
		return cache.get(expressionString)
	}
	
	abstract override fun doResolve(expressionString: String): T
}