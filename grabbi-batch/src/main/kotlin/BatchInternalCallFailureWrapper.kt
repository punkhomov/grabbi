package punkhomov.grabbi.batch

class BatchInternalCallFailureWrapper(val error: Exception) : Exception(
    "It's only a wrapper of exceptions. Normally it should caught at the 'batch execution' phase.",
    error,
)