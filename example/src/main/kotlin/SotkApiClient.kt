package punkhomov.grabbi.example

import punkhomov.grabbi.batch.enableBatchCalls
import punkhomov.grabbi.core.ApiCallResult
import punkhomov.grabbi.core.ApiClient
import punkhomov.grabbi.core.onSuccess
import punkhomov.grabbi.example.apiweb.SotkWebAuthManager
import punkhomov.grabbi.example.apiweb.SotkWebService
import punkhomov.grabbi.example.apiweb.models.CardInfo
import punkhomov.grabbi.example.apiweb.models.CardNumber
import punkhomov.grabbi.example.apiweb.models.CardTransaction
import punkhomov.grabbi.example.apiweb.requests.*
import java.time.LocalDate

class SotkApiClient {
    private val sotkWebAuthManager = SotkWebAuthManager()

    private val apiClient = ApiClient {
        enableBatchCalls()
        service(SotkWebService) {
            authManager = sotkWebAuthManager
        }
    }

    suspend fun signInWebService(
        username: String, password: String, rememberMe: Boolean = false
    ): ApiCallResult<LoggedInUser> {
        val result = apiClient.execute(LoginCall(username, password, true))
        result.onSuccess { user ->
            sotkWebAuthManager.accept(user)
            apiClient.getService(SotkWebService).resetToken()
            if (rememberMe) {
                sotkWebAuthManager.rememberCredentials(username, password)
            }
        }
        return result
    }

    suspend fun signOutWebService(): ApiCallResult<Unit> {
        val result = apiClient.execute(LogoutCall())
        result.onSuccess {
            sotkWebAuthManager.accept(null)
            apiClient.getService(SotkWebService).resetToken()
            sotkWebAuthManager.forgetCredentials()
        }
        return result
    }

    suspend fun getMyCards(): ApiCallResult<List<CardNumber>> {
        return apiClient.execute(GetMyCardsCall())
    }

    suspend fun addCard(cardNumber: CardNumber): ApiCallResult<Unit> {
        return apiClient.execute(AddCardCall(cardNumber))
    }

    suspend fun removeCard(cardNumber: CardNumber): ApiCallResult<Unit> {
        return apiClient.execute(RemoveCardCall(cardNumber))
    }

    suspend fun getCardInfo(cardNumber: CardNumber): ApiCallResult<CardInfo> {
        return apiClient.execute(GetCardInfoCall(cardNumber))
    }

    suspend fun getCardTransactions(
        cardNumber: CardNumber, startDate: LocalDate, endDate: LocalDate
    ): ApiCallResult<List<CardTransaction>> {
        return apiClient.execute(GetCardTransactions(cardNumber, startDate, endDate))
    }
}

