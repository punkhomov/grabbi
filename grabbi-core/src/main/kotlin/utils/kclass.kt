package punkhomov.grabbi.core.utils

import kotlin.reflect.KClass

fun KClass<*>.qualifiedNameOrThrow() = qualifiedName ?: errorQualifiedNameIsNull()

private fun errorQualifiedNameIsNull(): Nothing = error(
    "Local or anonymous classes is not allowed here."
)