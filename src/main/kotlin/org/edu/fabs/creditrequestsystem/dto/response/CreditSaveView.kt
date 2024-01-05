package org.edu.fabs.creditrequestsystem.dto.response

import org.edu.fabs.creditrequestsystem.entity.Credit
import java.util.*

data class CreditSaveView(
    val creditCode: UUID,
    val customerId: Long?
) {
    constructor(credit: Credit) : this(
        creditCode = credit.creditCode,
        customerId = credit.customer?.id,
    )
}
