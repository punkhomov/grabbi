package punkhomov.grabbi.core

abstract class ApiService {
    /**
     * The error handling strategy for this service.
     */
    open val errorHandlingStrategy: ErrorHandlingStrategy.Instantiator<*, *> = OneRetryAttemptStrategy()

    /**
     * Builds the context for the given call.
     *
     * @param call The call being executed.
     * @param context The context for the call.
     */
    abstract suspend fun buildContext(call: ApiCall<*>, context: ApiCallContext)

    /**
     * Handles errors that occur during the call execution. The error handling strategy
     * determines when this method will be called and whether it will be called at all.
     *
     * @param context The context of the call.
     * @param error The exception representing the error.
     * @return `true` if the error is handled and the call can be retried, `false` otherwise.
     */
    open suspend fun onErrorOccurred(context: ApiCallContext, error: Exception): Boolean {
        return false
    }

    /**
     * Factory interface for creating instances of [ApiService] implementations.
     */
    interface Factory<out Config : Any, Service : ApiService> {
        /**
         * The unique key associated with the service.
         */
        val key: TypedKey<Service>

        /**
         * Creates an instance of the service.
         *
         * @param configure The configuration block for customizing the service.
         * @return An instance of the service.
         */
        fun createService(configure: Config.() -> Unit): Service
    }
}