@file:Suppress("UnusedReceiverParameter")

package punkhomov.grabbi.core

import punkhomov.grabbi.core.utils.qualifiedNameOrThrow
import kotlin.reflect.KClass

@JvmInline
value class TypedKey<T>(val key: String) {
    companion object
}


fun <T> TypedKey.Companion.build(kind: String, kClass: KClass<T & Any>?, qualifier: String?): TypedKey<T> {
    check(kClass != null || qualifier != null) { "" } // todo message
    return TypedKey(buildString {
        append('@')
        append(kind)

        if (kClass != null) {
            append(':')
            append('[').append(kClass.qualifiedNameOrThrow()).append(']')
        }

        if (qualifier != null) {
            append(':')
            append(qualifier)
        }
    })
}

inline fun <reified T : Any> TypedKey.Companion.ofProp(name: String? = null): TypedKey<T> {
    return TypedKey.build("prop", T::class, name)
}

inline fun <reified T : ApiService> TypedKey.Companion.ofService(): TypedKey<T> {
    return TypedKey.build("service", T::class, null)
}


inline fun <reified T : Any> createPropKey(name: String? = null): TypedKey<T> {
    return TypedKey.ofProp(name)
}

inline fun <reified Service : ApiService> ApiService.Factory<*, Service>.createServiceKey(): TypedKey<Service> {
    return TypedKey.ofService<Service>()
}
