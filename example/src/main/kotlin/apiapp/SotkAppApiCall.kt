package punkhomov.grabbi.example.apiapp

//abstract class SotkAppApiCall<TResult> : HttpApiCall<TResult>() {
//    override val service = SotkAppService
//
//    override suspend fun transformResponse(context: ApiCallContext, response: TransformedResponse) {
//        response.tryTransform { httpResponse.bodyAsText() }
//    }
//
//    override fun initErrorChecks(context: ApiCallContext, chain: ErrorCheckerChain) {
//        TODO("Not yet implemented")
//    }
//}