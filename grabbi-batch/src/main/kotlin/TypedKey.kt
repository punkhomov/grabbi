package punkhomov.grabbi.batch

import punkhomov.grabbi.core.TypedKey
import punkhomov.grabbi.core.build
import kotlin.reflect.KClass

fun <T : Any> TypedKey.Companion.ofQualifier(kClass: KClass<T>, name: String): TypedKey<T> {
    return TypedKey.build("qualifier", kClass, name)
}

fun <T : Any> TypedKey.Companion.ofQualifierNullable(kClass: KClass<T>, name: String): TypedKey<T?> {
    return TypedKey.build("qualifier", kClass, name)
}


fun <T : Any> qualifierKey(kClass: KClass<T>, name: String): TypedKey<T> {
    return TypedKey.ofQualifier(kClass, name)
}


fun <T : Any> qualifierNullableKey(kClass: KClass<T>, name: String): TypedKey<T?> {
    return TypedKey.ofQualifierNullable(kClass, name)
}