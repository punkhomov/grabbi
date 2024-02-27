package punkhomov.grabbi.batch

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

internal class BatchCallResponsesTest {
    @Test
    fun `put and get notnull response - expect success`() {
        val responses = BatchCallResponses()

        val qualifier = qualifierKey(String::class, "qualifier")
        val value: String = "response"

        responses.writer.putResponse(qualifier, value)
        assertEquals(value, responses.reader.getResponse(qualifier))
    }

    @Test
    fun `put and get null response - expect success`() {
        val responses = BatchCallResponses()

        val qualifier = qualifierNullableKey(String::class, "qualifier")
        val value: String? = null

        responses.writer.putResponse(qualifier, value)
        assertEquals(value, responses.reader.getResponse(qualifier))
    }

    @Test
    fun `put multiple responses with one key - expect error`() {
        val responses = BatchCallResponses()

        val qualifier = qualifierKey(String::class, "qualifier")

        assertThrows<IllegalStateException> {
            responses.writer.putResponse(qualifier, "Response1")
            responses.writer.putResponse(qualifier, "Response2")
        }
    }

    @Test
    fun `get not assigned response - expect error`() {
        val responses = BatchCallResponses()

        val qualifier = qualifierKey(String::class, "qualifier")

        assertThrows<IllegalStateException> {
            responses.reader.getResponse(qualifier)
        }
    }
}