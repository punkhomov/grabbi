package punkhomov.grabbi.core

/**
 * The class is responsible for executing calls.
 */
class ApiClient internal constructor(
    private val services: Map<TypedKey<*>, ApiService>
) {
    /**
     * Executes the provided call.
     *
     * @param call The call to be executed.
     * @return The result of the call execution.
     */
    suspend fun <T> execute(call: ApiCall<T>): ApiCallResult<T> {
        return try {
            val service = findServiceForCall(call)
            executeInternal(service, call)
        } catch (ex: ApiErrorExceptionWrapper) {
            ApiCallResult.Failure(ex.error)
        }
    }

    /**
     * Gets the service instance corresponding to the provided factory.
     *
     * @param factory The factory of the desired service.
     * @return The service instance.
     * @throws IllegalStateException If the corresponding service was not found among
     * the registered services.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : ApiService> getService(factory: ApiService.Factory<*, T>): T {
        val rawService = services[factory.key] ?: error("") // todo error message
        return rawService as T
    }

    /**
     * Finds the appropriate service instance for the given call.
     *
     * @param call The call for which the service needs to be found.
     * @return The service instance.
     * @throws IllegalStateException If the corresponding service was not found among
     * the registered services.
     */
    private fun findServiceForCall(call: ApiCall<*>): ApiService {
        val key = call.service.key
        return services[key] ?: error("No appropriate service found for this call. Please register the '$key' service.")
    }

    /**
     * Executes the provided call, ensuring proper handling of errors that may occur
     * during the call execution.
     *
     * @param service The service to be used for executing the call.
     * @param call The call to be executed.
     */
    private suspend fun <T> executeInternal(service: ApiService, call: ApiCall<T>): ApiCallResult<T> {
        val errorResolver = service::onErrorOccurred
        val errorHandler = service.errorHandlingStrategy.getInstance(errorResolver)

        var result: ApiCallResult<T>
        do {
            val context = createCallContext(service, call)
            result = call.execute(context)
        } while (mustBeRetried(context, result, errorHandler))

        return result
    }

    /**
     * Creates a context for request execution; an instance of the context must be
     * used only once, meaning a new context must be created for each invocation of the
     * [ApiCall.execute] method.
     *
     * @param service The service to be used for the call.
     * @param call The call for which the context is being created.
     */
    private suspend fun createCallContext(service: ApiService, call: ApiCall<*>): ApiCallContext {
        return ApiCallContextImpl(this, service, call).also { context ->
            service.buildContext(call, context)
        }
    }

    /**
     * Determines whether the call needs to be retried.
     *
     * @param context The context of the call.
     * @param result The result of the call.
     * @param errorHandler The strategy for handling errors.
     * @return `true` if the call should be retried, `false` otherwise.
     */
    private suspend fun mustBeRetried(
        context: ApiCallContext,
        result: ApiCallResult<*>,
        errorHandler: ErrorHandlingStrategy
    ): Boolean {
        return when (result) {
            is ApiCallResult.Success -> false
            is ApiCallResult.Failure -> errorHandler.decide(context, result.error)
        }
    }
}

/**
 * Represents the configuration for an [ApiClient] instance.
 */
class ApiClientConfig {
    internal val services = HashMap<TypedKey<*>, ApiService>()

    /**
     * Registers a service.
     *
     * @param service The factory for creating the service instance.
     * @param configure An optional configuration block for customizing the service.
     */
    fun <Config : Any> service(
        service: ApiService.Factory<Config, *>,
        configure: Config.() -> Unit = {},
    ) {
        services[service.key] = service.createService(configure)
    }
}

/**
 * Constructs an [ApiClient] instance.
 *
 * @param config The configuration block for initializing the [ApiClientConfig].
 * @return An instance of [ApiClient].
 */
fun ApiClient(config: ApiClientConfig.() -> Unit) = ApiClient(
    ApiClientConfig().apply(config).services
)