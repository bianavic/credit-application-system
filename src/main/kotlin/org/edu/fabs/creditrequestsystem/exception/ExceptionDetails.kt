package org.edu.fabs.creditrequestsystem.exception

import java.time.LocalDateTime

data class ExceptionDetails(
    val title: String,
    val timestamp: LocalDateTime,
    val status: Int,
    val exception: String, // em que local ocorreu
    // campo onde ocorreu a exception e a mensagem retornada
    val details: MutableMap<String, String?>
)
