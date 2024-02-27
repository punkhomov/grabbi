package punkhomov.grabbi.batch

import punkhomov.grabbi.core.*

class BatchFeatureService : ApiService() {
    override suspend fun buildContext(call: ApiCall<*>, context: ApiCallContext) {
        // nothing to build
    }

    companion object Factory : ApiService.Factory<Unit, BatchFeatureService> {
        override val key = createServiceKey()

        override fun createService(configure: Unit.() -> Unit): BatchFeatureService {
            return BatchFeatureService()
        }
    }
}

fun ApiClientConfig.enableBatchCalls() {
    service(BatchFeatureService)
}