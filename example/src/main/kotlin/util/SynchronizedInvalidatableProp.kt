package punkhomov.grabbi.example.util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.core.ApiCallResult

/**
 * The value wrapper, that itself tries to obtain a new value when the current value is null or is
 * marked invalid. The wrapper guarantees exactly one attempt to obtain a new value when the
 * specified value is marked invalid multiple times.
 */
abstract class SynchronizedInvalidatableProp<T : Any>(initValue: T? = null) {
    /**
     * Obtains a new value.
     *
     * @param context some external context.
     * @return result of the operation.
     */
    protected abstract suspend fun obtainValue(context: ApiCallContext): ApiCallResult<T>


    private var lastValue = initValue
    private val mutex = Mutex()

    /**
     * Gets the internal value or throw an error from [obtainValue].
     *
     * @return non-null value.
     */
    suspend fun getValue(context: ApiCallContext): T = mutex.withLock {
        getValueInternal(context) { throw it }
    }

    /**
     * Gets the internal value or a default value.
     *
     * @param default default value.
     * @return non-null value.
     */
    suspend fun getValueOr(context: ApiCallContext, default: T): T = mutex.withLock {
        getValueInternal(context) { default }
    }

    /**
     * Gets the internal value or a result of the `default` lambda execution.
     *
     * @param default default value lambda.
     * @return non-null value.
     */
    suspend fun getValueOrElse(context: ApiCallContext, default: (Exception) -> T): T = mutex.withLock {
        getValueInternal(context, default)
    }

    /**
     * Gets the raw internal value.
     *
     * @return nullable raw value.
     */
    suspend fun getLastValue(): T? = mutex.withLock {
        lastValue
    }

    /**
     * Manually puts value.
     *
     * @param value the value.
     */
    suspend fun putValue(value: T): T = mutex.withLock {
        putValueInternal(value)
    }

    /**
     *
     */
    suspend fun putValueIfAbsent(value: T): T = mutex.withLock {
        if (lastValue == null) {
            putValueInternal(value)
        }
        lastValue!!
    }

    /**
     *
     */
    suspend fun computeValueIfAbsent(value: () -> T): T = mutex.withLock {
        if (lastValue == null) {
            putValueInternal(value.invoke())
        }
        lastValue!!
    }

    /**
     *
     */
    suspend fun clearValue() = mutex.withLock {
        clearValueInternal()
    }

    /**
     * Rejects internal value if it is equals the given value. Tries automatically update
     * internal value via [obtainValue] method.
     *
     * @param value The value to be rejected.
     * @return true if internal value was updated, false otherwise.
     */
    suspend fun invalidateValue(context: ApiCallContext, value: T?) = mutex.withLock {
        println("invalidateValue: current = $lastValue, invalid = $value")
        if (value == null) {
            return@withLock false
        }

        if (lastValue == value) {
            when (val result = obtainValue(context)) {
                is ApiCallResult.Success -> putValueInternal(result.data)
                is ApiCallResult.Failure -> clearValueInternal()
            }
        }

        return@withLock lastValue != null && lastValue != value
    }

    private suspend fun getValueInternal(context: ApiCallContext, onFailure: (Exception) -> T): T {
        return if (lastValue == null) {
            when (val result = obtainValue(context)) {
                is ApiCallResult.Success -> putValueInternal(result.data)
                is ApiCallResult.Failure -> onFailure(result.error)
            }
        } else {
            lastValue!!
        }
    }

    private fun putValueInternal(newValue: T): T {
        lastValue = newValue
        return newValue
    }

    private fun clearValueInternal() {
        lastValue = null
    }
}