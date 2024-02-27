package punkhomov.grabbi.example.apiweb.props

import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.core.ApiCallResult
import punkhomov.grabbi.example.util.SynchronizedInvalidatableProp

class TokenProp : SynchronizedInvalidatableProp<Token>() {
    override suspend fun obtainValue(context: ApiCallContext): ApiCallResult<Token> {
        return context.client.execute(ObtainTokenCall())
    }
}

