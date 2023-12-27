package org.edu.fabs.creditrequestsystem.entity

import org.edu.fabs.creditrequestsystem.ennumeration.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class Credit(
    val creditCode: UUID = UUID.randomUUID(), // gerado automaticamente (e randomicamente) assim q instanciar este objeto
    val creditValue: BigDecimal = BigDecimal.ZERO,
    val dayFirstInstallment: LocalDate,
    val numberOfInstallments: Int = 0,
    val status: Status = Status.IN_PROGRESS,
    val customer: Customer? = null,
    val id: Long? = null
)
