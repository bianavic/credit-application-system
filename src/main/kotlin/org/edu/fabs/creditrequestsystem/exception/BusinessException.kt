package org.edu.fabs.creditrequestsystem.exception

data class BusinessException(override val message: String?) : RuntimeException(message)
