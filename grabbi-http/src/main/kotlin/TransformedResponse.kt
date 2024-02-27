package punkhomov.grabbi.http

import io.ktor.client.statement.*
import kotlin.reflect.KClass

class TransformedResponse(val httpResponse: HttpResponse) {
    private val transformed = HashMap<String, Any>()
    val size: Int by transformed::size

    fun <T : Any> putAs(kClass: KClass<T>, value: T) {
        val key = getKey(kClass)
        transformed[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrNullAs(kClass: KClass<T>): T? {
        val key = getKey(kClass)
        val value = transformed[key]
        return value as T?
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrThrowAs(kClass: KClass<T>): T {
        val key = getKey(kClass)
        val value = transformed[key] ?: error("No response is present with '$key' type.")
        return value as T
    }

    private fun getKey(kClass: KClass<*>): String {
        return kClass.qualifiedName ?: error("Anonymous classes is not allowed")
    }
}


inline fun <reified T : Any> TransformedResponse.getOrNullAs(): T? {
    return getOrNullAs(T::class)
}

inline fun <reified T : Any> TransformedResponse.getOrThrowAs(): T {
    return getOrThrowAs(T::class)
}


inline fun <T : Any> TransformedResponse.tryTransform(
    kClass: KClass<T>,
    block: TransformedResponse.() -> T
) {
    runCatching { block.invoke(this) }.onSuccess { putAs(kClass, it) }
}

inline fun <reified T : Any> TransformedResponse.tryTransform(
    block: TransformedResponse.() -> T
) {
    return this.tryTransform(T::class, block)
}


inline fun <T : Any> TransformedResponse.ifPresentAs(
    kClass: KClass<T>,
    block: TransformedResponse.(data: T) -> Unit
) {
    getOrNullAs(kClass)?.also { block.invoke(this, it) }
}

inline fun <reified T : Any> TransformedResponse.ifPresentAs(
    block: TransformedResponse.(data: T) -> Unit
) {
    ifPresentAs(T::class, block)
}


inline fun <T : Any> TransformedResponse.ifPresentOnlyAs(
    kClass: KClass<T>,
    block: TransformedResponse.(data: T) -> Unit
) {
    getOrNullAs(kClass)?.takeIf { size == 1 }?.also { block.invoke(this, it) }
}

inline fun <reified T : Any> TransformedResponse.ifPresentOnlyAs(
    block: TransformedResponse.(data: T) -> Unit
) {
    ifPresentOnlyAs(T::class, block)
}
