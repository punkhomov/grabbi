package punkhomov.grabbi.example.apiweb

import kotlinx.coroutines.sync.Mutex
import punkhomov.grabbi.example.apiweb.requests.LoggedInUser

data class Credentials(
    val username: String,
    val password: String,
)

class SotkWebAuthManager {
    private var currentUser: LoggedInUser? = null
    private var credentials: Credentials? = null
    private val mutex = Mutex()

    fun accept(user: LoggedInUser?) {
        currentUser = user
    }

    fun rememberCredentials(username: String, password: String) {

    }

    fun forgetCredentials() {

    }
}
