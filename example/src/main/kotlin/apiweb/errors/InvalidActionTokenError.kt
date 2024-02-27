package punkhomov.grabbi.example.apiweb.errors

import punkhomov.grabbi.example.apiweb.SotkWebApiException
import punkhomov.grabbi.example.apiweb.props.Token

class InvalidActionTokenError(val token: Token? = null, cause: Throwable? = null) : SotkWebApiException(
    "Invalid action token '$token' with cause '$cause'.",
    cause
)