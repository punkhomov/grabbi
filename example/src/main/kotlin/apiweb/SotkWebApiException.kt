package punkhomov.grabbi.example.apiweb

open class SotkWebApiException : Exception {
    constructor() : super("[SOTK_WEB_API_ERROR]")
    constructor(message: String?) : super("[SOTK_WEB_API_ERROR]: $message")
    constructor(message: String?, cause: Throwable?) : super("[SOTK_WEB_API_ERROR]: $message", cause)
    constructor(cause: Throwable?) : super(cause)
}