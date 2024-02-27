package punkhomov.grabbi.core

/**
 * The class describes the complete behavior of the call: its configuration, the
 * execution process, including the return value upon successful completion, or an error
 * that may occur during the call execution.
 *
 * @param TResult The expected type of result upon successful completion of the call.
 */
abstract class ApiCall<TResult> {
    /**
     * The service factory links the call to a specified service. It is used to find an
     * instance of the specified service registered in [ApiClient].
     */
    abstract val service: ApiService.Factory<*, *>

    /**
     * Executes this call.
     *
     * Note: The class implementing this method **should ensure** that the
     * `ApiErrorExceptionWrapper` is **not caught** within the method's body, for the
     * correct call execution logic.
     *
     * @param context The context for the current call execution.
     * @return An [ApiCallResult] representing the result of the call.
     */
    abstract suspend fun execute(context: ApiCallContext): ApiCallResult<TResult>

    /**
     * Requirements necessary for the proper execution of this call.
     */
    val requirements: MutableSet<TypedKey<*>> = HashSet()

    /**
     * Registers a requirement.
     *
     * @param key The typed key representing the requirement.
     */
    protected fun require(key: TypedKey<*>) {
        requirements.add(key)
    }
}