@file:Suppress("NOTHING_TO_INLINE")

package punkhomov.grabbi.core

/**
 * Represents the result of a call execution or an any other operation, which can be
 * either successful or failed.
 */
sealed interface ApiCallResult<T> {
    /**
     * Represents a successful call result, containing the returned data.
     */
    class Success<T>(val data: T) : ApiCallResult<T>

    /**
     * Represents a failed call result, containing the encountered error.
     */
    class Failure<T>(val error: Exception) : ApiCallResult<T>
}

/**
 * Returns true if the result is a [success][ApiCallResult.Success], false otherwise.
 */
val ApiCallResult<*>.isSuccess get() = this is ApiCallResult.Success

/**
 * Returns true if the result is a [failure][ApiCallResult.Failure], false otherwise.
 */
val ApiCallResult<*>.isFailure get() = this is ApiCallResult.Success


/**
 * Performs the given [action] on the encapsulated value if this instance represents
 * [success][ApiCallResult.Success]. Returns the original `ApiCallResult` unchanged.
 *
 * @param action the action to execute if the call is successful.
 * @return the original call result.
 */
inline fun <T> ApiCallResult<T>.onSuccess(action: (T) -> Unit): ApiCallResult<T> {
    if (this is ApiCallResult.Success) {
        action(this.data)
    }
    return this
}

/**
 * Performs the given [action] on the encapsulated exception if this instance represents
 * [failure][ApiCallResult.Failure]. Returns the original `ApiCallResult` unchanged.
 *
 * @param action the action to execute if the call is successful.
 * @return the original call result.
 */
inline fun ApiCallResult<*>.onFailure(action: (Exception) -> Unit): ApiCallResult<*> {
    if (this is ApiCallResult.Failure) {
        action(this.error)
    }
    return this
}


inline fun <T> ApiCallResult<T>.getOrNull() = when (this) {
    is ApiCallResult.Success -> data
    is ApiCallResult.Failure -> null
}

inline fun <T> ApiCallResult<T>.getOrDefault(defaultValue: T) = when (this) {
    is ApiCallResult.Success -> data
    is ApiCallResult.Failure -> defaultValue
}

inline fun <T> ApiCallResult<T>.getOrThrow() = when (this) {
    is ApiCallResult.Success -> data
    is ApiCallResult.Failure -> throw error
}

inline fun <T> ApiCallResult<T>.getOrElse(onFailure: (exception: Exception) -> T) = when (this) {
    is ApiCallResult.Success -> data
    is ApiCallResult.Failure -> onFailure(error)
}


inline fun <T, R> ApiCallResult<T>.map(transform: (T) -> R) = when (this) {
    is ApiCallResult.Success -> ApiCallResult.Success(transform(data))
    is ApiCallResult.Failure -> ApiCallResult.Failure(error)
}

inline fun <T, R> ApiCallResult<T>.flatMap(transform: (T) -> ApiCallResult<R>) = when (this) {
    is ApiCallResult.Success -> transform(data)
    is ApiCallResult.Failure -> ApiCallResult.Failure(error)
}