package util

import kotlinx.coroutines.test.runTest
import punkhomov.grabbi.core.*
import punkhomov.grabbi.example.util.SynchronizedInvalidatableProp
import kotlin.test.Test
import kotlin.test.assertTrue

internal class SynchronizedInvalidatablePropTest {
    @Test
    fun `once invalidate with succeed to 'obtainValue' at 'invalidate' - expect all true `() = runTest {
        val noMoreThanOneCall = NoMoreThanOneCall()
        val value = TestProp(0) {
            noMoreThanOneCall.check()
            ApiCallResult.Success(5)
        }

        val results = buildList {
            repeat(5) {
                add(value.invalidateValue(EmptyContext(), 0))
            }
        }

        assertTrue(results.all { it == true })
    }

    @Test
    fun `once invalidate with failed to 'obtainValue' at 'invalidate' - expect all false `() = runTest {
        val noMoreThanOneCall = NoMoreThanOneCall()
        val value = TestProp(0) {
            noMoreThanOneCall.check()
            ApiCallResult.Failure(Exception())
        }

        val results = buildList {
            repeat(5) {
                add(value.invalidateValue(EmptyContext(), 0))
            }
        }

        assertTrue(results.all { it == false })
    }
}

internal class EmptyContext : ApiCallContext {
    override val client: ApiClient
        get() = TODO("Not yet implemented")
    override val service: ApiService
        get() = TODO("Not yet implemented")
    override val call: ApiCall<*>
        get() = TODO("Not yet implemented")

    override fun <T : Any> setProp(key: TypedKey<T>, value: T) {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getPropOrThrow(key: TypedKey<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getPropOrNull(key: TypedKey<T>): T? {
        TODO("Not yet implemented")
    }
}

internal class NoMoreThanOneCall {
    private var notCalledYet = true

    fun check() {
        assertTrue(notCalledYet)
        notCalledYet = false
    }
}

internal class TestProp<T : Any>(
    initWith: T? = null,
    private val getNewTestValue: (context: ApiCallContext) -> ApiCallResult<T>,
) : SynchronizedInvalidatableProp<T>(initWith) {
    override suspend fun obtainValue(context: ApiCallContext): ApiCallResult<T> {
        return getNewTestValue.invoke(context)
    }
}