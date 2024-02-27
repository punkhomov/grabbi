package punkhomov.grabbi.core

typealias ErrorResolver = suspend (context: ApiCallContext, error: Exception) -> Boolean

/**
 * Represents a strategy for handling errors that occur during calls.
 */
interface ErrorHandlingStrategy {
    /**
     * The error resolver that will attempt to resolve errors.
     */
    val errorResolver: ErrorResolver

    /**
     * Decides whether to retry the call.
     *
     * @param context The context of the call.
     * @param error The error that occurred during the call.
     * @return true if the call should be retried, false otherwise.
     */
    suspend fun decide(context: ApiCallContext, error: Exception): Boolean

    /**
     * Factory interface for creating specific types of [ErrorHandlingStrategy].
     */
    interface Factory<TConfig, TStrategy : ErrorHandlingStrategy> {
        /**
         * Creates a configuration instance.
         *
         * @return A configuration instance.
         */
        fun createConfig(): TConfig

        /**
         * Creates an instance of [ErrorHandlingStrategy].
         *
         * @param config The configuration.
         * @param resolver The error resolver to be used by the strategy.
         * @return An instance of [ErrorHandlingStrategy].
         */
        fun createInstance(config: TConfig, resolver: ErrorResolver): TStrategy
    }

    /**
     * Instantiator class for creating instances of [ErrorHandlingStrategy].
     */
    class Instantiator<TConfig, TStrategy : ErrorHandlingStrategy>(
        private val config: TConfig,
        private val factory: (TConfig, ErrorResolver) -> TStrategy,
    ) {
        /**
         * Gets a new instance of [ErrorHandlingStrategy].
         *
         * @param resolver The error resolver to be used by the strategy.
         * @return An instance of [ErrorHandlingStrategy].
         */
        fun getInstance(resolver: ErrorResolver): TStrategy {
            return factory.invoke(config, resolver)
        }
    }
}

inline operator fun <TConfig, TStrategy : ErrorHandlingStrategy> ErrorHandlingStrategy.Factory<TConfig, TStrategy>.invoke(
    configure: TConfig.() -> Unit = {}
): ErrorHandlingStrategy.Instantiator<TConfig, TStrategy> {
    val config = createConfig().apply(configure)
    return ErrorHandlingStrategy.Instantiator(config, this::createInstance)
}


class OneRetryAttemptStrategy(override val errorResolver: ErrorResolver) : ErrorHandlingStrategy {
    private var noAttemptYet = true

    override suspend fun decide(context: ApiCallContext, error: Exception): Boolean {
        return if (noAttemptYet) {
            noAttemptYet = false
            errorResolver(context, error)
        } else {
            false
        }
    }

    companion object Factory : ErrorHandlingStrategy.Factory<Unit, OneRetryAttemptStrategy> {
        override fun createConfig() {
            return
        }

        override fun createInstance(config: Unit, resolver: ErrorResolver): OneRetryAttemptStrategy {
            return OneRetryAttemptStrategy(resolver)
        }
    }
}